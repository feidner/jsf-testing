package hfe.servlets;

import hfe.config.UrlPatternPrefix;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

@WebServlet("/" + UrlPatternPrefix.INIT_SERVLET_URL_PATH + "/*")
public class InitServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String relativePath = "/WEB-INF" + request.getRequestURI().substring(request.getContextPath().length());
        Logger.getLogger(InitServlet.class.getSimpleName()).info(String.format("forward to %s", relativePath));
        request.getRequestDispatcher(relativePath).forward(request, response);
    }
}
