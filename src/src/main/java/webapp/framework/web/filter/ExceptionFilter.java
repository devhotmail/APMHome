package webapp.framework.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * In case of exception, this filter redirects the user to an error page that displays a unique exception ID, also dumped in the log.
 * In case of an Ajax request corresponding to an expired session, this filter redirects the user to the login page.
 */
public final class ExceptionFilter implements Filter {
    private static final Logger log = Logger.getLogger(ExceptionFilter.class);

    @Override
    public void init(FilterConfig config) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException,
            ServletException {

        try {
            // chain...
            HttpServletRequest request = (HttpServletRequest) req;

            if (isAjax(request) && !request.isRequestedSessionIdValid()) {
                log.warn("Session expiration during ajax request, partial redirect to login page");
                HttpServletResponse response = (HttpServletResponse) resp;
                response.getWriter().print(xmlPartialRedirectToPage(request, "/login?session_expired=1"));
                response.flushBuffer();
            } else {
                chain.doFilter(req, resp);
            }

        } catch (Exception e) {
            // redirect to error page
            HttpServletRequest request = (HttpServletRequest) req;
            request.getSession().setAttribute("lastException", e);
            request.getSession().setAttribute("lastExceptionUniqueId", e.hashCode());
            
            log.error("EXCEPTION unique id: " + e.hashCode(), e);

            HttpServletResponse response = (HttpServletResponse) resp;

            if(isRestfulAPI(request)) {
                response.setStatus(500);
                response.getWriter().print(e.getMessage());
                response.flushBuffer();
                return;
            }

            if (!isAjax(request)) {
                response.sendRedirect(request.getContextPath() + request.getServletPath() + "/error");
            } else {
                // let's leverage jsf2 partial response
                response.getWriter().print(xmlPartialRedirectToPage(request, "/error"));
                response.flushBuffer();
            }
        }
    }

    private String xmlPartialRedirectToPage(HttpServletRequest request, String page) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version='1.0' encoding='UTF-8'?>");
        sb.append("<partial-response><redirect url=\"").append(request.getContextPath()).append(
                request.getServletPath()).append(page).append("\"/></partial-response>");
        return sb.toString();
    }

    private boolean isAjax(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }

    private boolean isRestfulAPI(HttpServletRequest request) {
        return request.getServletPath().contains("/web") || request.getServletPath().contains("/api");
    }
    
    @Override
    public void destroy() {
    }
}