package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(urlPatterns = {"/admin/*"})
public class AdminAuthFilter implements Filter {

@Override
public void init(FilterConfig filterConfig) throws ServletException {
}

@Override
public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;
    
    HttpSession session = httpRequest.getSession(false);
    
    boolean isLoggedIn = false;
    boolean isAdmin = false;
    
    try {
        isLoggedIn = (session != null && session.getAttribute("userId") != null);
        isAdmin = isLoggedIn && "admin".equals(session.getAttribute("userRole"));
    } catch (Exception e) {
        // Log the exception
        e.printStackTrace();
    }
    
    // Allow access only for admins
    if (isAdmin) {
        chain.doFilter(request, response);
    } else {
        // Save the requested URL for redirection after login
        String requestedUrl = httpRequest.getRequestURI();
        if (httpRequest.getQueryString() != null) {
            requestedUrl += "?" + httpRequest.getQueryString();
        }
        
        if (session != null) {
            session.setAttribute("requestedUrl", requestedUrl);
        }
        
        httpResponse.sendRedirect(httpRequest.getContextPath() + "/auth/login");
    }
}

@Override
public void destroy() {
}
}
