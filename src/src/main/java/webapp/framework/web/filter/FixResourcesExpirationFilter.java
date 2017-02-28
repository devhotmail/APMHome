package webapp.framework.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Extend resources expiration.
 * <p>
 * Note: we still have weird behavior with Chrome which seems to do not take into account our directive.
 */
public final class FixResourcesExpirationFilter extends OncePerRequestFilter {
    private static final Logger log = Logger.getLogger(FixResourcesExpirationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String uri = httpRequest.getRequestURI();
        if (uri.contains("/javax.faces.resource/")) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            log.info("fixing expires header for " + uri);
            httpResponse.setDateHeader("Expires", System.currentTimeMillis() + 86400000000l); // in 1000 days            
            httpResponse.setHeader("Cache-Control", "public, max-age=86400000"); // 1000 days
        }
        filterChain.doFilter(request, response);
    }
}