package hfe.testing;

import com.google.common.collect.Sets;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.core.ApplicationContextFacade;
import org.apache.catalina.core.StandardContext;
import org.apache.openejb.AppContext;
import org.apache.openejb.assembler.classic.event.AssemblerAfterApplicationCreated;
import org.apache.openejb.assembler.classic.event.ContainerSystemPostCreate;
import org.apache.openejb.cdi.WebBeansContextBeforeDeploy;
import org.apache.openejb.cdi.WebBeansContextCreated;
import org.apache.openejb.config.AppModule;
import org.apache.openejb.config.event.BeforeAppInfoBuilderEvent;
import org.apache.openejb.config.event.BeforeDeploymentEvent;
import org.apache.openejb.observer.Observes;
import org.apache.webbeans.annotation.AnyLiteral;
import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.portable.events.generics.GProcessInjectionTarget;
import org.apache.webbeans.portable.events.generics.GenericBeanEvent;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Reception;
import javax.enterprise.event.TransactionPhase;
import javax.enterprise.inject.spi.*;
import javax.servlet.SessionTrackingMode;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * Wird als Extension fuer TomEE in resources/META-INF/org.apache.openejb.extensions angegeben.
 * Siehe org.apache.openejb.Extensions.find.....
 */
public class HfeObserver {

    public static final Map<Class<?>, HfeSupplier> PRODUCERS = new HashMap<>();

    public static void addProducer(Class<?> clazz, Supplier<Object> supplier) {
        PRODUCERS.get(clazz).setSupplier(supplier);
    }

    public void observer(@Observes WebBeansContextBeforeDeploy event) {
        event.getContext().getBeanManagerImpl().getNotificationManager().addObserver(new HfeObserverMethod(), ProcessInjectionTarget.class);
    }

    public void event(@Observes LifecycleEvent event) {
        if(Lifecycle.CONFIGURE_START_EVENT.equals(event.getType())) {
            StandardContext context = (StandardContext)event.getLifecycle();
            context.addPropertyChangeListener(evt -> {
                if("configured".equals(evt.getPropertyName())) {
                    ApplicationContextFacade facade = (ApplicationContextFacade)context.getServletContext();
                    if(facade.getEffectiveSessionTrackingModes().contains(SessionTrackingMode.COOKIE) &&
                            facade.getSessionCookieConfig().isSecure()) {
                        //facade.setSessionTrackingModes(Sets.newHashSet(SessionTrackingMode.URL));
                        facade.getSessionCookieConfig().setSecure(false);
                    }
                }
            });
        } else if(Lifecycle.AFTER_START_EVENT.equals(event.getType())) {
            Logger.getLogger("nop").info("started");
        }
    }

    public void event(@Observes AssemblerAfterApplicationCreated event) {
        AppContext context = event.getContext();
    }

    public void event(@Observes ContainerSystemPostCreate event) {

    }

    public void event(@Observes WebBeansContextCreated event) {
        WebBeansContext context = event.getContext();
    }

    public void event(@Observes BeforeAppInfoBuilderEvent event) {
        AppModule module = event.getAppModule();
    }

    public void event(@Observes BeforeDeploymentEvent event) {
        URL[] urls = event.getUrls();
    }

    private static class HfeObserverMethod implements ObserverMethod<Object>, GenericBeanEvent, Extension {

        @Override
        public Class<?> getBeanClass() {
            return HfeObserverMethod.class;
        }

        @Override
        public Type getObservedType() {
            return null;
        }

        @Override
        public Set<Annotation> getObservedQualifiers() {
            return Sets.newHashSet(AnyLiteral.INSTANCE);
        }

        @Override
        public Reception getReception() {
            return null;
        }

        @Override
        public TransactionPhase getTransactionPhase() {
            return null;
        }

        @Override
        public void notify(Object event) {
            if (event instanceof GProcessInjectionTarget) {
                GProcessInjectionTarget injectionTarget = (GProcessInjectionTarget) event;
                Class<?> clazz = injectionTarget.getAnnotatedType().getJavaClass();
                HfeSupplier supplier = PRODUCERS.get(clazz);
                if (supplier == null) {
                    PRODUCERS.put(clazz, supplier = new HfeSupplier());
                }
                InjectionTarget<Object> target = injectionTarget.getInjectionTarget();
                assert target != null;
                injectionTarget.setInjectionTarget(new HfeInjectionTarget(target, supplier));
            }
        }

        @Override
        public Class<?> getBeanClassFor(Class<?> eventClass) {
            return null;
        }
    }


    private static class HfeInjectionTarget implements InjectionTarget<Object> {

        private InjectionTarget<Object> target;
        private Supplier<Object> supplier;

        public HfeInjectionTarget(InjectionTarget<Object> target, Supplier<Object> supplier) {
            assert supplier != null;
            this.target = target;
            this.supplier = supplier;
        }

        public void inject(Object instance, CreationalContext<Object> ctx) {
            target.inject(instance, ctx);
        }

        public void postConstruct(Object instance) {
            target.postConstruct(instance);
        }

        public void preDestroy(Object instance) {
            target.preDestroy(instance);
        }

        public Object produce(CreationalContext<Object> creationalContext) {
            return supplier.get() == null ? target.produce(creationalContext) : supplier.get();
        }

        public void dispose(Object instance) {
            target.dispose(instance);
        }

        public Set<InjectionPoint> getInjectionPoints() {
            return target.getInjectionPoints();
        }

    }

    public static class HfeSupplier implements Supplier<Object> {
        private Supplier<Object> supplier;


        public void setSupplier(Supplier<Object> supplier) {
            this.supplier = supplier;
        }

        @Override
        public Object get() {
            return supplier == null ? null : supplier.get();
        }
    }

}
