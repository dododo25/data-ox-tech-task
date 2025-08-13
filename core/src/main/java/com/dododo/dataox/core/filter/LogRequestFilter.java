package com.dododo.dataox.core.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LogRequestFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogRequestFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        LOGGER.warn("trying to access {} {}", httpRequest.getMethod(), httpRequest.getRequestURI());
        chain.doFilter(request, response);
        LOGGER.warn("{} {} - server responded with {}", httpRequest.getMethod(), httpRequest.getRequestURI(),
                httpResponse.getStatus());
    }
}
