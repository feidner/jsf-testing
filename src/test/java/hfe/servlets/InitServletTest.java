package hfe.servlets;

import config.UrlPatternPrefix;
import hfe.testing.EmbeddedTomcatListener;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static org.testng.Assert.assertTrue;

@Listeners(EmbeddedTomcatListener.class)
public class InitServletTest {

    @Test
    public void initWebsiteWithHtmlUnitDriver() throws IOException {
        File callmeHtml = new File(EmbeddedTomcatListener.WEB_INF_PATH + "/init/callme.html");
        FileUtils.write(callmeHtml, "pippa", Charset.defaultCharset());
        WebDriver driver = new HtmlUnitDriver();
        driver.get("http://localhost:8080/" + EmbeddedTomcatListener.APP_NAME + "/" + UrlPatternPrefix.INIT_SERVLET_URL_PATH + "/callme.html");
        assertTrue(driver.getPageSource().contains("pippa"));
        driver.close();
        driver.quit();
        FileUtils.deleteDirectory(new File(EmbeddedTomcatListener.WEB_INF_PATH + "/init/"));
    }
}
