package hfe.testing;

import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.servlet4preview.GenericFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

public class HfeFilter extends GenericFilter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if(RequestFacade.class.isInstance(request)) {
            RequestFacade facade = (RequestFacade)request;
            if(facade.getRequestURI().contains("label.xhtml")) {
                facade.getScheme();
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
