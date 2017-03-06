package webapp.framework.web.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

public class SessionExpirationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (isAjax(request) && !request.isRequestedSessionIdValid()) {
            response.getWriter().print(xmlPartialRedirectToPage(request, "/login.xhtml?session_expired=1"));
            response.flushBuffer();
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isAjax(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }

    private String xmlPartialRedirectToPage(HttpServletRequest request, String page) {
        return "<?xml version='1.0' encoding='UTF-8'?>" //
                + "<partial-response>" //
                + "<redirect url=\"" + request.getContextPath() + page + "\"/>" //
                + "</partial-response>";
    }
}