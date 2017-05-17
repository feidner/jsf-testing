package hfe.testing;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({TYPE})
public @interface EmbeddedContainerConfig {

    String MODULE_PATH = "src/main/webapp/";
    String APP_NAME = "hfe";


    String modulePath() default MODULE_PATH;

    String appName() default APP_NAME;
}
