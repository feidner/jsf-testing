package hfe.xhtml;

import hfe.beans.SettingValueViaServletFilter;
import hfe.testing.EmbeddedTomcatListener;
import hfe.testing.HfeObserver;
import hfe.tools.HfeUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Listeners(EmbeddedTomcatListener.class)
public class LabelSiteServletFilterWithMockTest {

    @Test
    public void labelHasValueMats_RequestSite_ThenResponseBodyContainsMats() throws Exception {
        HfeObserver.addProducer(SettingValueViaServletFilter.class, () -> {
            SettingValueViaServletFilter obj = mock(SettingValueViaServletFilter.class);
            when(obj.getValue()).thenReturn("Mats und Filippa");
            return obj;
        });
        assertTrue(HfeUtils.runForUrl(new HtmlUnitDriver(), "faces/settingValueViaServletFilter.xhtml", driver -> {
            assertEquals(driver.findElement(By.tagName("body")).getText(), "Mats und Filippa");
            return true;
        }));
    }

}
