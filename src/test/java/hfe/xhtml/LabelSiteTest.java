package hfe.xhtml;

import hfe.beans.DataHolder;
import hfe.beans.Hello;
import hfe.testing.EmbeddedTomcatListener;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;
import javax.naming.NamingException;
import java.util.logging.Logger;

import static org.testng.Assert.assertEquals;

@Listeners(EmbeddedTomcatListener.class)
public class LabelSiteTest {

    @Inject
    private DataHolder dataHolder;

    private static String testVal;

    @BeforeMethod
    public void setup() throws NamingException {
    }

    @Test
    public void labelHasValueMats_RequestSite_ThenResponseBodyContainsMats() throws Exception {
        Logger.getLogger("LabelSiteTest").info(dataHolder.getValue());
        testVal = "Mats und Filippa";
        WebDriver driver = new HtmlUnitDriver();
        driver.get("http://localhost:8080/" + EmbeddedTomcatListener.APP_NAME + "/faces/label.xhtml");
        String pageSource = driver.getPageSource();
        WebElement body = driver.findElement(By.tagName("body"));
        assertEquals(body.getText(), testVal);
        Logger.getLogger("LabelSiteTest").info(pageSource);
        driver.close();
    }

    @Specializes
    public static class HelloSpec extends Hello {
        @PostConstruct
        public void fill() {
            setValue(testVal);
        }
    }
}
