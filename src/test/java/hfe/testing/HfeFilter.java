package hfe.testing;

import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.servlet4preview.GenericFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HfeFilter extends GenericFilter {
    public static Map<String, Runnable> REQUEST_MANIPULATIONS = new HashMap<>();
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if(RequestFacade.class.isInstance(request)) {
            RequestFacade facade = (RequestFacade)request;
            REQUEST_MANIPULATIONS.keySet().stream().
               filter(uriSubString -> facade.getRequestURI().
               contains(uriSubString)).forEach(uriSubString -> REQUEST_MANIPULATIONS.get(uriSubString).run());
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
