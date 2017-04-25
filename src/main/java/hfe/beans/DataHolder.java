package hfe.beans;

import javax.annotation.PostConstruct;

public class DataHolder {

    public static final String INITIAL = "LOGISCH";

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @PostConstruct
    public void fill() {
        setValue(INITIAL);
    }
}
