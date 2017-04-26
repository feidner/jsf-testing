package hfe.testing;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.openejb.BeanContext;
import org.apache.openejb.config.FinderFactory;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.spi.ContainerSystem;
import org.apache.tomee.embedded.EmbeddedTomEEContainer;
import org.apache.webbeans.spi.ScannerService;

import javax.ejb.embeddable.EJBContainer;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import java.io.File;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.testng.Assert.assertTrue;

public class EmbeddedContainer {

    public static final String MODULE_PATH = "src/main/webapp/";
    public static final String WEB_INF_PATH = MODULE_PATH + "WEB-INF/";
    public static final String APP_NAME = "hfe";

    private static EmbeddedTomEEContainer container;

    private EmbeddedContainer() {
    }

    public static void start(Object testClassObject) {
        assertTrue(getTestsNeedEjbContainer().contains(testClassObject.getClass().getTypeName()));
        if (container != null) {
            return;
        }
        Map<Object, Object> properties = new HashMap<>();
        properties.put(EJBContainer.PROVIDER, EmbeddedTomEEContainer.class);
        properties.put(EJBContainer.MODULES, new File[]{new File(MODULE_PATH)});
        properties.put(EJBContainer.APP_NAME, APP_NAME);

        System.setProperty("org.apache.openejb.servlet.filters", "hfe.testing.HfeFilter=/*");
        System.setProperty("webapp." + ScannerService.class.getName(), hfe.testing.HfeScannerService.class.getTypeName());

        //properties.put(DeploymentFilterable.CLASSPATH_INCLUDE, ".*classes.*");
        //properties.put(DeploymentFilterable.CLASSPATH_EXCLUDE, ".*InjectionTest.*");

        //System.setProperty("openejb.scan.webapp.container", Boolean.TRUE.toString());
        //System.setProperty("openejb.scan.webapp.container.skip-folder", Boolean.FALSE.toString());
        //System.setProperty("openejb.additional.include", "classes");

        System.setProperty(FinderFactory.class.getTypeName(), HfeFinderFactory.class.getTypeName());
        System.setProperty("tomee.webapp.externalRepositories", "build/classes/main,build/classes/test");

        container = (EmbeddedTomEEContainer) EmbeddedTomEEContainer.createEJBContainer(properties);

        addTestsAsManagedBean(testClassObject);

        collectContextClasses("");
    }

    public static EmbeddedTomEEContainer getContainer() {
        return container;
    }

    private static BeanContext getBeanContext() {
        return SystemInstance.get().getComponent(ContainerSystem.class).deployments()[0];
    }

    private static void addTestsAsManagedBean(Object testClassObject) {
        BeanManager beanManager = getBeanContext().getWebBeansContext().getBeanManagerImpl();
        @SuppressWarnings("unchecked")
        Class<Object> clazz = (Class<Object>) testClassObject.getClass();
        InjectionTarget<Object> target = beanManager.createInjectionTarget(beanManager.createAnnotatedType(clazz));
        target.inject(testClassObject, beanManager.createCreationalContext(null));
    }

    public static String buildUrl(String siteName) {
        String root = getBeanContext().getModuleContext().getAppContext().getWebContexts().get(0).getContextRoot();
        String host = getBeanContext().getModuleContext().getAppContext().getWebContexts().get(0).getHost();
        return String.format("http://%s:8080%s/%s", host, root, siteName);
    }

    public static String buildModuleUri(String siteName) {
        return String.format("%s/%s", getBeanContext().getModuleContext().getModuleURI().getPath(), siteName);
    }

    private static <T> Set<Class<T>> collectContextClasses(String regex) {
        Set<Class<T>> classes = new HashSet<>();
        print("java:global/");
        print("java:app/AppName");
        print("java:app/BeanManager");
        print("java:hfe");
        return classes;
    }

    private static <T> void print(String name) {

        try {
            NamingEnumeration<NameClassPair> enumeration = getContainer().getContext().list(name);
            while (enumeration.hasMoreElements()) {
                try {
                    NameClassPair pair = enumeration.next();
                    Logger.getLogger("print").info("class: " + pair.getClassName());
                } catch (NamingException e) {

                }
            }
        } catch (NamingException e) {
        }
    }

    private static Set<String> uriToClassDotName(Stream<URI> uriStream, URI substringUri) {
        return uriStream.map(uri -> FilenameUtils.getBaseName(uri.getPath().substring(substringUri.getPath().length()).replaceAll("/", "."))).collect(Collectors.toSet());
    }

    private static Set<String> getTestsNeedEjbContainer() {

        File testFile = new File("build/classes/test");
        URI testUri = testFile.toURI();
        Collection<URI> testFiles = FileUtils.listFiles(testFile, new String[]{"class"}, true).stream().map(File::toURI).collect(Collectors.toSet());
        Set<String> classNames = uriToClassDotName(testFiles.stream().filter(uri -> uri.getPath().matches(".*(Test).class")), testUri);
        Set<String> testingClassStrings = uriToClassDotName(testFiles.stream().filter(uri -> uri.getPath().matches(".*hfe/testing.*")), testUri);
        Set<ContainerPredicate> containerPredicates = testingClassStrings.stream().
                map(classString -> {
                    try {
                        return Class.forName(classString);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }).
                filter(clazz -> Arrays.stream(clazz.getInterfaces()).filter(interfaze -> interfaze == ContainerPredicate.class).findAny().isPresent()).
                map(clazz -> {
                    try {
                        return (ContainerPredicate) clazz.newInstance();
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }).
                collect(Collectors.toSet());

        Set<String> testsNeedEjbContainer = classNames.stream().
                filter(classString -> containerPredicates.stream().filter(containerPredicate -> containerPredicate.isContainerTest(classString)).findAny().isPresent()).
                collect(Collectors.toSet());
        return testsNeedEjbContainer;
    }

    @FunctionalInterface
    interface ContainerPredicate {
        boolean isContainerTest(Class<?> clazz);

        default boolean isContainerTest(String classString) {
            try {
                Class<?> clazz = Class.forName(classString);
                return !Modifier.isAbstract(clazz.getModifiers()) && isContainerTest(clazz);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}