package hfe.staticc;

import hfe.testing.EmbeddedContainer;
import hfe.testing.EmbeddedTomcatListener;
import hfe.testing.HfeUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
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
        System.setProperty("webdriver.chrome.driver", "src/test/selenium/chromedriver2.27.exe");
        spoky(ChromeDriver::new);
    }

    @Test
    public void initWebsiteWithFirefox() {
        System.setProperty("webdriver.gecko.driver", "src/test/selenium/geckodriver0.16.1.exe");
        DesiredCapabilities capabilities = DesiredCapabilities.firefox();
        capabilities.setCapability("marionette", false);
        spoky(FirefoxDriver::new);
    }

    @Test
    public void initWebsiteWithInternetExplorer() {
        System.setProperty("webdriver.ie.driver", "src/test/selenium/IEDriverServer3.4.0.exe");
        DesiredCapabilities caps = DesiredCapabilities.internetExplorer();
        caps.setCapability("ignoreZoomSetting", true);
        spoky(() -> new InternetExplorerDriver(caps));
    }

    private void spoky(Supplier<WebDriver> driverSupplier) {
        WebDriver driver = null;
        try {
            driver = driverSupplier.get();
            WebDriver finalDriver = driver;
            HfeUtils.runWithHtmlSite("callme.html", null, "<html><body>mats</body></html>", () -> finalDriver.get(EmbeddedContainer.buildUrl("callme.html")));
            Logger.getLogger("peep").info(driver.getPageSource());
            assertTrue(driver.getPageSource().contains("mats"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Driver kann nicht gestartet werden");
        } finally {
            driver.close();
            try {
                driver.quit();
            } catch (Exception e) {
                // ignore
            }
        }
    }
}
