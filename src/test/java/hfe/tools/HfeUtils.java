package hfe.tools;

import hfe.testing.EmbeddedContainer;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

public class HfeUtils {

    //StreamSupport.stream(Spliterators.spliteratorUnknownSize(dropDatabases.iterator(), 0), false).forEach(drop -> drop.drop(connection));

    private HfeUtils() {

    }

    public static URL getClassesFolderURL() throws IOException {
        List<URL> resources = Collections.list(HfeUtils.class.getClassLoader().getResources("."));
        URL classesURL = resources.stream().filter(url -> url.getPath().contains("classes") && !url.getPath().contains("test")).findAny().orElseThrow(() -> new RuntimeException("Verzeichnis classes kann nicht gefunden werden"));
        return classesURL;
    }

    public static Class<?> getTopLevelEnclosingClass(Class<?> clazz) {
        while (clazz.getEnclosingClass() != null) {
            clazz = clazz.getEnclosingClass();
        }
        return clazz;
    }

    public static void runWithHtmlSite(String siteName, String dirPathToDelete, String siteContent, Runnable run) throws Exception {
        runWithHtmlSite(siteName, dirPathToDelete, siteContent, () -> {
            run.run();
            return null;
        });
    }

    public static <T> T runWithHtmlSite(String siteName, String dirPathToDelete, String siteContent, Callable<T> run) throws Exception {
        siteName = EmbeddedContainer.buildModuleUri(siteName);
        File f = new File(siteName);
        if(dirPathToDelete == null) {
            f.deleteOnExit();
        }
        FileUtils.write(f, siteContent, Charset.defaultCharset());
        T obj = run.call();
        if(dirPathToDelete != null) {
            dirPathToDelete = EmbeddedContainer.buildModuleUri(dirPathToDelete);
            FileUtils.deleteDirectory(new File(dirPathToDelete));
        }
        return obj;
    }
}
