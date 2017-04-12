package hfe.testing;

import org.apache.openejb.config.FinderFactory;
import org.apache.tomee.embedded.EmbeddedTomEEContainer;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;

import javax.ejb.embeddable.EJBContainer;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class EmbeddedTomcatListener implements IInvokedMethodListener {

    public static final String MODULE_PATH = "build/war_exploded/";
    public static final String WEB_INF_PATH = MODULE_PATH + "WEB-INF/";
    public static final String APP_NAME = "hfe";

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        HfeFinderFactory.replaceScan(new HashSet<>(), new HashSet<>(), new HashSet<>());

        Map<Object, Object> properties = new HashMap();
        properties.put(EJBContainer.PROVIDER, EmbeddedTomEEContainer.class);
        properties.put(EJBContainer.MODULES, new File[] { new File(MODULE_PATH)});
        properties.put(EJBContainer.APP_NAME, APP_NAME);

        System.setProperty(FinderFactory.class.getTypeName(), HfeFinderFactory.class.getTypeName());
        //System.setProperty("openejb.scan.webapp.container", Boolean.TRUE.toString());
        //System.setProperty("openejb.scan.webapp.container.skip-folder", Boolean.FALSE.toString());
        //System.setProperty("openejb.additional.include", "classes");

        EmbeddedTomEEContainer.createEJBContainer(properties);
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {

    }
}
