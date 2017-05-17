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
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EmbeddedContainer {

    private static EmbeddedTomEEContainer container;

    private EmbeddedContainer() {
    }

    public static void start(Object testClassObject, String modulePath, String appName) {
        assert getTestsNeedEjbContainer().contains(testClassObject.getClass().getTypeName()) : "Muss ein ContainerTest sein";
        if (container != null) {
            return;
        }
        Map<Object, Object> properties = new HashMap<>();
        properties.put(EJBContainer.PROVIDER, EmbeddedTomEEContainer.class);
        properties.put(EJBContainer.MODULES, new File[]{new File(modulePath)});
        properties.put(EJBContainer.APP_NAME, appName);

        /**
         * Falls in der web.xml eine solche Resource definiert ist:
         *
         * <resource-ref>
         *     <res-ref-name>hfe/HfeString</res-ref-name>
         *     <res-type>java.lang.String</res-type>
         * </resource-ref>
         *
         * java.lang.String sind in Tomee keine Resources: http://tomee-openejb.979440.n4.nabble.com/resource-ref-settings-within-openEJB-td4665781.html
         *
         * properties.put("resourceid", String.format("new://Resource?type=java.lang.String&class-name=%s&factory-name=create", HfeStringResource.class.getTypeName()));
         */

        System.setProperty("org.apache.openejb.servlet.filters", "hfe.testing.HfeFilter=/*");
        System.setProperty("webapp." + ScannerService.class.getName(), hfe.testing.HfeScannerService.class.getTypeName());

        //properties.put(DeploymentFilterable.CLASSPATH_INCLUDE, ".*classes.*");
        //properties.put(DeploymentFilterable.CLASSPATH_EXCLUDE, ".*InjectionTest.*");

        //System.setProperty("openejb.scan.webapp.container", Boolean.TRUE.toString());
        //System.setProperty("openejb.scan.webapp.container.skip-folder", Boolean.FALSE.toString());
        //System.setProperty("openejb.additional.include", "classes");
        //System.setProperty("xbean.finder.use.get-resources", Boolean.FALSE.toString());

        System.setProperty(FinderFactory.class.getTypeName(), HfeFinderFactory.class.getTypeName());
        System.setProperty("tomee.webapp.externalRepositories", "build/classes/main,build/classes/test");

        SystemInstance.get().addObserver(new HfeObserver());

        container = (EmbeddedTomEEContainer) EmbeddedTomEEContainer.createEJBContainer(properties);

        addObjectAsInjectionTarget(testClassObject);

        collectContextClasses("");
    }

    public static EmbeddedTomEEContainer getContainer() {
        return container;
    }

    public static BeanContext getBeanContext() {
        return SystemInstance.get().getComponent(ContainerSystem.class).deployments()[0];
    }

    public static void addObjectAsInjectionTarget(Object testClassObject) {
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
        print("");
        return classes;
    }

    private static <T> void print(String name) {

        try {
            //Logger.getLogger("print").info("init: " + name);
            NamingEnumeration<NameClassPair> enumeration = getContainer().getContext().list(name);
            while (enumeration.hasMoreElements()) {
                try {
                    NameClassPair pair = enumeration.next();
              //      Logger.getLogger("print").info(pair.getName() + " -class: " + pair.getClassName());
                    print(name + ":" + pair.getName());
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
