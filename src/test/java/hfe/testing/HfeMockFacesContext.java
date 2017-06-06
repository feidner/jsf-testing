package hfe.testing;

import org.mockito.Mock;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;

import javax.faces.context.FacesContext;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

public class HfeMockFacesContext implements IInvokedMethodListener {

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        Optional<Field> facesContextField = Arrays.stream(testResult.getTestClass().getRealClass().getDeclaredFields()).filter(field -> field.isAnnotationPresent(Mock.class) && field.getType() == FacesContext.class).findAny();
        try {
            if(facesContextField.isPresent()) {
                facesContextField.get().setAccessible(true);
                FacesContext context = (FacesContext)facesContextField.get().get(testResult.getInstance());
                HfeFacesContextSetter.setContext(context);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {

    }


    private static abstract class HfeFacesContextSetter extends FacesContext {
        public static void setContext(FacesContext context) {
            FacesContext.setCurrentInstance(context);
        }
    }
}
