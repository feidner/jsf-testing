package hfe.staticc;

import hfe.testing.EmbeddedTomcatListener;
import hfe.tools.HfeUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.function.Supplier;
import java.util.logging.Logger;

import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.fail;

@Listeners(EmbeddedTomcatListener.class)
public class SeleniumDifferentBrowserTest {

    @Test
    public void initWebsiteWithHtmlUnitDriver() {
        spoky(HtmlUnitDriver::new);
    }

    @Test
    public void initWebsiteWithChrome() {
        System.setProperty("webdriver.chrome.driver", "src/test/resources/selenium/chromedriver.exe");
        spoky(ChromeDriver::new);
    }

    @Test
    public void initWebsiteWithFirefox() {
        System.setProperty("webdriver.gecko.driver", "src/test/resources/selenium/geckodriver.exe");
        spoky(FirefoxDriver::new);
    }

    @Test
    public void initWebsiteWithInternetExplorer() {
        System.setProperty("webdriver.ie.driver", "src/test/resources/selenium/IEDriverServer.exe");
        spoky(InternetExplorerDriver::new);
    }

    private void spoky(Supplier<WebDriver> driverSupplier) {
        try {
            WebDriver driver = driverSupplier.get();
            HfeUtils.runWithHtmlSite("build/war_exploded/mats.html", null, "<html><body>mats</body></html>", () -> driver.get("http://localhost:8080/hfe/callme.html"));
            Logger.getLogger("peep").info(driver.getPageSource());
            assertTrue(driver.getPageSource().contains("mats"));
            driver.close();
            driver.quit();
        } catch (Exception e) {
            fail("Driver kann nicht gestartet werden");
        }
    }
}
