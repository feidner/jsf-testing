package hfe.testing;

import javax.faces.context.FacesContext;

public abstract class HfeFaceContext extends FacesContext {

    public static void setCurrent(FacesContext mock) {
        FacesContext ff = FacesContext.getCurrentInstance();
        setCurrentInstance(mock);
        FacesContext fff = FacesContext.getCurrentInstance();
        fff.getApplication();
    }
}
