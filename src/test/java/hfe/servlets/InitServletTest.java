package hfe.servlets;

import hfe.testing.EmbeddedContainer;
import hfe.testing.EmbeddedTomcatListener;
import hfe.testing.HfeUtils;
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
        HfeUtils.runWithHtmlSite("WEB-INF/init/call.html", "WEB-INF/init/", "pippa",
                () -> driver.get(EmbeddedContainer.buildUrl("init/call.html")));
        assertTrue(driver.getPageSource().contains("pippa"));
        driver.close();
        driver.quit();
    }
}
