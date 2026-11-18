package com.cakedelight.api_gateway.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestIdLoggingFilter extends OncePerRequestFilter {

    public static final String REQUEST_ID_HEADER = "X-Request-Id";
    private static final Logger log = LoggerFactory.getLogger(RequestIdLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String requestId = Optional.ofNullable(request.getHeader(REQUEST_ID_HEADER))
                .filter(id -> !id.isBlank())
                .orElse(UUID.randomUUID().toString());

        response.setHeader(REQUEST_ID_HEADER, requestId);
        long start = System.currentTimeMillis();

        try {
            chain.doFilter(new RequestIdRequestWrapper(request, requestId), response);
        } finally {
            log.info("{} {}{} -> {} ({} ms) requestId={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    request.getQueryString() == null ? "" : "?" + request.getQueryString(),
                    response.getStatus(),
                    System.currentTimeMillis() - start,
                    requestId);
        }
    }

    /** Adds the X-Request-Id header to the request, so it is forwarded to the downstream service. */
    private static class RequestIdRequestWrapper extends HttpServletRequestWrapper {
        private final String requestId;

        RequestIdRequestWrapper(HttpServletRequest request, String requestId) {
            super(request);
            this.requestId = requestId;
        }

        @Override
        public String getHeader(String name) {
            return REQUEST_ID_HEADER.equalsIgnoreCase(name) ? requestId : super.getHeader(name);
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            return REQUEST_ID_HEADER.equalsIgnoreCase(name)
                    ? Collections.enumeration(List.of(requestId))
                    : super.getHeaders(name);
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            Set<String> names = new LinkedHashSet<>(Collections.list(super.getHeaderNames()));
            names.add(REQUEST_ID_HEADER);
            return Collections.enumeration(names);
        }
    }
}