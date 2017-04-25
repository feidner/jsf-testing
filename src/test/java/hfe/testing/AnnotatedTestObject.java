package hfe.testing;

import org.apache.webbeans.config.DefaultAnnotation;
import org.apache.xbean.finder.Annotated;

import javax.annotation.ManagedBean;
import java.lang.annotation.Annotation;

public class AnnotatedTestObject<T> implements Annotated<T> {
    private Annotation annotation = DefaultAnnotation.of(ManagedBean.class);
    private T obj;

    public AnnotatedTestObject(T obj) {
        this.obj = obj;
    }

    @Override
    public T get() {
        return obj;
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return (T)annotation;
    }

    @Override
    public Annotation[] getAnnotations() {
        return new Annotation[] { annotation };
    }

    @Override
    public Annotation[] getDeclaredAnnotations() {
        return new Annotation[] { annotation };
    }

}
