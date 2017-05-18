package hfe.testing;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

@Retention(RetentionPolicy.RUNTIME)
@Target({TYPE})
public @interface EmbeddedContainerConfig {

    String DEFAULT_MODULE_PATH = "src/main/webapp/";
    String DEFAULT_APP_NAME = "hfe";


    String modulePath() default DEFAULT_MODULE_PATH;

    String appName() default DEFAULT_APP_NAME;
}
