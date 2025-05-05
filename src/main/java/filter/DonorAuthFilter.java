package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(urlPatterns = {"/donor/*"})
public class DonorAuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        HttpSession session = httpRequest.getSession(false);
        String requestURI = httpRequest.getRequestURI();
        
        // Debug logging
        System.out.println("DonorAuthFilter: Processing request for " + requestURI);
        if (session != null) {
            System.out.println("DonorAuthFilter: Session exists, userId=" + session.getAttribute("userId") + ", userRole=" + session.getAttribute("userRole"));
        } else {
            System.out.println("DonorAuthFilter: No session exists");
        }
        
        boolean isLoggedIn = (session != null && session.getAttribute("userId") != null);
        boolean isDonor = isLoggedIn && "donor".equals(session.getAttribute("userRole"));
        boolean isAdmin = isLoggedIn && "admin".equals(session.getAttribute("userRole"));
        
        // Allow access for donors and admins
        if (isDonor || isAdmin) {
            System.out.println("DonorAuthFilter: User is authorized, proceeding with request");
            chain.doFilter(request, response);
        } else {
            // Save the requested URL for redirection after login
            String requestedUrl = httpRequest.getRequestURI();
            if (httpRequest.getQueryString() != null) {
                requestedUrl += "?" + httpRequest.getQueryString();
            }
            
            if (session != null) {
                session.setAttribute("requestedUrl", requestedUrl);
                System.out.println("DonorAuthFilter: Saved requested URL: " + requestedUrl);
            }
            
            System.out.println("DonorAuthFilter: User is not authorized, redirecting to login");
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/auth/login");
        }
    }

    @Override
    public void destroy() {
    }
}
