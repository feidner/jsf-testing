package hfe.xhtml;

import hfe.beans.SettingValueViaServletFilter;
import hfe.testing.EmbeddedTomcatListener;
import hfe.testing.HfeFilter;
import hfe.testing.HfeUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import javax.enterprise.inject.spi.CDI;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Listeners(EmbeddedTomcatListener.class)
public class LabelSiteServletFilterTest {



    @Test
    public void labelHasValueMats_RequestSite_ThenResponseBodyContainsMats() throws Exception {
        HfeFilter.REQUEST_MANIPULATIONS.put("settingValueViaServletFilter.xhtml", () -> CDI.current().select(SettingValueViaServletFilter.class).get().setValue("Mats"));
        assertTrue(HfeUtils.runForUrl(new HtmlUnitDriver(), "faces/settingValueViaServletFilter.xhtml", driver -> {
            assertEquals(driver.findElement(By.tagName("body")).getText(), "Mats");
            return true;
        }));
    }
}
