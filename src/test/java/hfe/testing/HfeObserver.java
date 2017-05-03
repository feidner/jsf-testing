package hfe.testing;

import com.google.common.collect.Sets;
import hfe.beans.SettingValueViaServletFilter;
import org.apache.openejb.cdi.WebBeansContextBeforeDeploy;
import org.apache.openejb.observer.Observes;
import org.apache.webbeans.annotation.AnyLiteral;
import org.apache.webbeans.portable.events.generics.GProcessInjectionTarget;
import org.apache.webbeans.portable.events.generics.GenericBeanEvent;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Reception;
import javax.enterprise.event.TransactionPhase;
import javax.enterprise.inject.spi.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Wird als Extension fuer TomEE in resources/META-INF/org.apache.openejb.extensions angegeben.
 * Siehe org.apache.openejb.Extensions.find.....
 */
public class HfeObserver {

    public static final Map<Class<?>, HfeSupplier> PRODUCERS = new HashMap<>();

    public static void addProducer(Class<SettingValueViaServletFilter> settingValueViaServletFilterClass, Supplier<Object> supplier) {
        PRODUCERS.get(settingValueViaServletFilterClass).setSupplier(supplier);
    }

    public void observer(@Observes WebBeansContextBeforeDeploy event) {
        event.getContext().getBeanManagerImpl().getNotificationManager().addObserver(new HfeObserverMethod(), ProcessInjectionTarget.class);
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
