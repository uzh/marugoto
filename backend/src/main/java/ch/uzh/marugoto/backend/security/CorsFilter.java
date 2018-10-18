package ch.uzh.marugoto.backend.security;

import org.springframework.http.HttpMethod;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CorsFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        var response = (HttpServletResponse) servletResponse;
        var request = (HttpServletRequest) servletRequest;

        if (request.getRequestURI().startsWith("/api/")) {
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "GET,POST,DELETE,PUT,OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "*");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setIntHeader("Access-Control-Max-Age", 180);

            if ("OPTIONS".equals(request.getMethod())) {
                response.setStatus(HttpServletResponse.SC_OK);
            }
        }


        if (!"OPTIONS".equals(request.getMethod())) {
            filterChain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

    }
}