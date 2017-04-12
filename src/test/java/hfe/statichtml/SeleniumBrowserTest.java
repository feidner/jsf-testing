package hfe.statichtml;

import hfe.testing.EmbeddedTomcatListener;
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
public class SeleniumBrowserTest {

    @Test
    public void initWebsiteWithHtmlUnitDriver() {
        spoky(() -> new HtmlUnitDriver());
    }

    @Test
    public void initWebsiteWithChrome() {
        System.setProperty("webdriver.chrome.driver", "src/test/resources/selenium/chromedriver.exe");
        spoky(() -> new ChromeDriver());
    }

    @Test
    public void initWebsiteWithFirefox() {
        System.setProperty("webdriver.gecko.driver", "src/test/resources/selenium/geckodriver.exe");
        spoky(() -> new FirefoxDriver());
    }

    @Test
    public void initWebsiteWithInternetExplorer() {
        System.setProperty("webdriver.ie.driver", "src/test/resources/selenium/IEDriverServer.exe");
        spoky(() ->  new InternetExplorerDriver());
    }

    private void spoky(Supplier<WebDriver> driverSupplier) {
        WebDriver driver = null;
        try {
            driver = driverSupplier.get();
        } catch (Exception e) {
            if(driver != null) {
                driver.quit();
            }
            fail("Driver kann nicht gestartet werden");
        }
        driver.get("http://localhost:8080/hfe/mats.html");
        Logger.getLogger("peep").info(driver.getPageSource());
        assertTrue(driver.getPageSource().contains("mats"));
        driver.close();
        driver.quit();
    }
}
