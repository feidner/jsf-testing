package hfe.beans;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import java.util.logging.Logger;

@Named
@RequestScoped
public class Hello {

    private String value = "henrik";

    public Hello() {
        Logger.getLogger(Hello.class.getSimpleName()).info("Konstruktor");
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
