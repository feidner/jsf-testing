package hfe.servlets;

import hfe.testing.EmbeddedTomcatListener;
import hfe.tools.HfeUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

@Listeners(EmbeddedTomcatListener.class)
public class InitServletTest {

    @Test
    public void initWebsiteWithHtmlUnitDriver() throws Exception {
        WebDriver driver = new HtmlUnitDriver();
        HfeUtils.runWithHtmlSite(EmbeddedTomcatListener.WEB_INF_PATH + "/init/callme.html", EmbeddedTomcatListener.WEB_INF_PATH + "/init/", "pippa",
                () -> driver.get("http://localhost:8080/" + EmbeddedTomcatListener.APP_NAME + "/init/callme.html"));
        assertTrue(driver.getPageSource().contains("pippa"));
        driver.close();
        driver.quit();
    }
}
