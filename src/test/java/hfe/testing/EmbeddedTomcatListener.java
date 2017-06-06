package hfe.testing;

import org.testng.*;

public class EmbeddedTomcatListener implements IConfigurationListener2 {

    @Override
    public void beforeConfiguration(ITestResult testResult) {
        String modulePath = EmbeddedContainerConfig.DEFAULT_MODULE_PATH;
        String appName = EmbeddedContainerConfig.DEFAULT_APP_NAME;
        if(testResult.getInstance().getClass().isAnnotationPresent(EmbeddedContainerConfig.class)) {
            modulePath = testResult.getInstance().getClass().getAnnotation(EmbeddedContainerConfig.class).modulePath();
            appName = testResult.getInstance().getClass().getAnnotation(EmbeddedContainerConfig.class).appName();
        }
        EmbeddedContainer.start(testResult.getInstance(), modulePath, appName);
    }

    @Override
    public void onConfigurationSuccess(ITestResult itr) {

    }

    @Override
    public void onConfigurationFailure(ITestResult itr) {

    }

    @Override
    public void onConfigurationSkip(ITestResult itr) {

    }
}
