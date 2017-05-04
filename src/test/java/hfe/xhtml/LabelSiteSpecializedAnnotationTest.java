package hfe.xhtml;

import hfe.beans.SettingValueViaSpecializesAnnotation;
import hfe.testing.EmbeddedTomcatListener;
import hfe.tools.HfeUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Specializes;

import java.util.logging.Logger;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Listeners(EmbeddedTomcatListener.class)
public class LabelSiteSpecializedAnnotationTest {

    private static String TEST_VALUE;

    @Test
    public void labelHasValueMats_RequestSite_ThenResponseBodyContainsMats() throws Exception {
        TEST_VALUE = "Mats";
        assertTrue(HfeUtils.runForUrl(new HtmlUnitDriver(), "faces/settingValueViaSpecializesAnnotation.xhtml", driver -> {
            assertEquals(driver.findElement(By.tagName("body")).getText(), "Mats");
            Logger.getLogger("html").info(driver.getPageSource());
            return true;
        }));
    }

    @Specializes
    public static class SpecialzedAnnotation extends SettingValueViaSpecializesAnnotation {
        @PostConstruct
        public void fill() {
            setValue(TEST_VALUE);
        }
    }
}
