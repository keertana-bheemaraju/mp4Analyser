package com.castlabs.mp4Analyzer.filters;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@WebFilter("/*")
public class ProfilerFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfilerFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        Instant start = Instant.now();
        try {
            chain.doFilter(req, resp);
        } finally {
            Instant finish = Instant.now();
            long time = Duration.between(start, finish).toMillis();
            LOGGER.info("{}: {} ms ", ((HttpServletRequest) req).getRequestURI(),  time);
        }
    }

    @Override
    public void destroy() {

    }
}