package hfe.testing;

import org.apache.openejb.cdi.CdiScanner;

import java.util.Set;

public class HfeScannerService extends CdiScanner {

    @Override
    public void init(Object object) {
        super.init(object);
    }

    @Override
    public Set<Class<?>> getBeanClasses() {
        return super.getBeanClasses();
    }
}
