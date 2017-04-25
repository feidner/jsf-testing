package hfe.injection;

import hfe.beans.DataHolder;
import hfe.testing.EmbeddedTomcatListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import javax.inject.Inject;

import static org.testng.Assert.assertEquals;

@Listeners(EmbeddedTomcatListener.class)
public class InjectionTest {

    @Inject
    private DataHolder dataHolder;

    @Test
    public void injectedObject_ThenValueIsPostconstructedValue() {
        assertEquals(dataHolder.getValue(), DataHolder.INITIAL);
    }
}
