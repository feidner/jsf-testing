package hfe.beans;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import java.util.logging.Logger;

@Named
@RequestScoped
public class SettingValueViaSpecializesAnnotation {

    private String value;

    public SettingValueViaSpecializesAnnotation() {
        Logger.getLogger(SettingValueViaSpecializesAnnotation.class.getSimpleName()).info("Konstruktor");
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
