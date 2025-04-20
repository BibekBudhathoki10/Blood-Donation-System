package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(urlPatterns = {"/user/*"})
public class UserAuthFilter implements Filter {

@Override
public void init(FilterConfig filterConfig) throws ServletException {
}

@Override
public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;
    
    HttpSession session = httpRequest.getSession(false);
    
    boolean isLoggedIn = (session != null && session.getAttribute("userId") != null);
    boolean isGeneralUser = isLoggedIn && "general".equals(session.getAttribute("userRole"));
    boolean isDonor = isLoggedIn && "donor".equals(session.getAttribute("userRole"));
    boolean isAdmin = isLoggedIn && "admin".equals(session.getAttribute("userRole"));
    
    // Allow access for general users, donors, and admins
    if (isGeneralUser || isDonor || isAdmin) {
        chain.doFilter(request, response);
    } else {
        httpResponse.sendRedirect(httpRequest.getContextPath() + "/auth/login");
    }
}

@Override
public void destroy() {
}
}
