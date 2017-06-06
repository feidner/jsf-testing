package hfe.beans;

import hfe.testing.HfeMockFacesContext;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

@Listeners(HfeMockFacesContext.class)
public class LoginBeanTest {

    @Mock
    private FacesContext context;

    private HttpServletRequest request;

    @BeforeClass
    public void setup() {
        MockitoAnnotations.initMocks(this);
        request = mock(HttpServletRequest.class);
        ExternalContext externalContext = mock(ExternalContext.class);
        when(externalContext.getRequest()).thenReturn(request);
        when(context.getExternalContext()).thenReturn(externalContext);
    }

    @Test
    public void login_ThenException() throws ServletException {
        Object[][] args = new Object[1][1];
        doAnswer(invocation -> {
            args[0] = invocation.getArguments();
            return null;
        }).when(request).login(anyString(), anyString());
        LoginBean loginBean = new LoginBean();
        loginBean.login("henrik", "passi");
        assertEquals(args[0][0], "henrik");
    }
}