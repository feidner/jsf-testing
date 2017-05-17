package hfe.testing;

import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;

public class EmbeddedTomcatListener implements IInvokedMethodListener {

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        String modulePath = EmbeddedContainerConfig.MODULE_PATH;
        String appName = EmbeddedContainerConfig.APP_NAME;
        if(testResult.getInstance().getClass().isAnnotationPresent(EmbeddedContainerConfig.class)) {
            modulePath = testResult.getInstance().getClass().getAnnotation(EmbeddedContainerConfig.class).modulePath();
            appName = testResult.getInstance().getClass().getAnnotation(EmbeddedContainerConfig.class).appName();
        }
        EmbeddedContainer.start(testResult.getInstance(), modulePath, appName);
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {

    }
}
