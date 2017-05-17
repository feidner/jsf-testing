package hfe.beans;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

@Named
@RequestScoped
public class LoginBean {
    public void login() {
        getClass();
    }
}
