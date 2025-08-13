package com.dododo.dataox.client.filter;

import com.dododo.dataox.client.service.ClientTokenService;
import com.dododo.dataox.core.model.Client;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class BearerTokenAuthFilter extends OncePerRequestFilter {

    private static final Pattern BEARER_TOKEN = Pattern.compile("Bearer (.+)");

    @Autowired
    private ClientTokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header == null) {
            chain.doFilter(request, response);
            return;
        }

        Matcher m = BEARER_TOKEN.matcher(header);

        if (m.find()) {
            Client client = tokenService.findClientByToken(m.group(1));

            if (client == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            } else {
                Authentication authenticationToken = new UsernamePasswordAuthenticationToken(
                        client.getId(), null, Collections.emptyList());
                SecurityContextHolder.getContext()
                        .setAuthentication(authenticationToken);

                request.setAttribute("authClient", client);
                request.setAttribute("token", m.group(1));
            }
        }

        chain.doFilter(request, response);
    }
}