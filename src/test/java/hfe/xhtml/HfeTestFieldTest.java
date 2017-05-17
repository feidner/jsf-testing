package hfe.xhtml;

import hfe.beans.LoginBean;
import hfe.testing.EmbeddedTomcatListener;
import hfe.testing.HfeObserver;
import hfe.testing.HfeUtils;
import hfe.testing.EmbeddedContainerConfig;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.Locale;
import java.util.function.Consumer;
import java.util.logging.Logger;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertTrue;

@EmbeddedContainerConfig(modulePath = "src/main/webapp-secure-trackingMode/webapp")
@Listeners(EmbeddedTomcatListener.class)
public class HfeTestFieldTest {

    private LoginBean obj;

    @BeforeTest
    public void setup() {
        System.setProperty("webdriver.chrome.driver", "chromedriver2.27.exe");
        System.setProperty("phantomjs.binary.path", "phantomjs.exe");
        Locale.setDefault(Locale.ENGLISH);
        obj = mock(LoginBean.class);
        HfeObserver.addProducer(LoginBean.class, () -> obj);
    }

    @Test
    public void login_ThenBeanMethodCalledOneTime() {
        submit(webDriver -> {});
        verify(obj, times(1)).login();
    }

    @Test
    public void login_NoErrorPage() {
        submit(driver -> {
            Document document = Jsoup.parse(driver.getPageSource());
            Elements elements = document.getElementsMatchingText(".*HTTP Status 500.*");
            assertTrue(elements.isEmpty(), "Status 500 darf nicht gefunden werden");
        });
    }

    private void submit(Consumer<WebDriver> checkMe) {
        assertTrue(HfeUtils.runForUrl(new HtmlUnitDriver(), "hfe-test-field.xhtml", driver -> {
            Logger.getLogger("html").info(driver.getPageSource());
            WebElement button = driver.findElement(By.id("hfe-form:hfe-button"));
            button.submit();
            checkMe.accept(driver);
            return true;
        }));
    }


     /*
        DesiredCapabilities sCaps = new DesiredCapabilities();
        sCaps.setJavascriptEnabled(true);
        sCaps.setCapability("takesScreenshot", false);


        // 01
        // Change "User-Agent" via page-object capabilities
        sCaps.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX + "userAgent", "My User Agent - Chrome");

        // 02
        // Disable "web-security", enable all possible "ssl-protocols" and "ignore-ssl-errors" for PhantomJSDriver
        sCaps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, new String[] {
                "--web-security=false",
                "--ssl-protocol=any",
                "--ignore-ssl-errors=true",
                "--webdriver-loglevel=DEBUG"
        });

// 03 (UPCOMING)
//        // Control LogLevel for GhostDriver, via CLI arguments
//        sCaps.setCapability(PhantomJSDriverService.PHANTOMJS_GHOSTDRIVER_CLI_ARGS, new String[] {
//            "--logLevel=" + (sConfig.getProperty("phantomjs_driver_loglevel") != null ? sConfig.getProperty("phantomjs_driver_loglevel") : "INFO")
//        });

// OPTIONAL
//        // Fetch configuration parameters
//        // "phantomjs_exec_path"
//        if (sConfig.getProperty("phantomjs_exec_path") != null) {
//            sCaps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, sConfig.getProperty("phantomjs_exec_path"));
//        } else {
//            throw new IOException(String.format("Property '%s' not set!", PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY));
//        }
//        // "phantomjs_driver_path"
//        if (sConfig.getProperty("phantomjs_driver_path") != null) {
//            System.out.println("Test will use an external GhostDriver");
//            sCaps.setCapability(PhantomJSDriverService.PHANTOMJS_GHOSTDRIVER_PATH_PROPERTY, sConfig.getProperty("phantomjs_driver_path"));
//        } else {
//            System.out.println("Test will use PhantomJS internal GhostDriver");
//        }
*/

}
