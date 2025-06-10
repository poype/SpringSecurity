package com.poype.spring_security.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.DeferredSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class RedisSecurityContextRepository implements SecurityContextRepository {
    private static final Logger log = LoggerFactory.getLogger(RedisSecurityContextRepository.class);

    private static final int COOKIE_MAX_AGE = 60 * 60 * 24 * 7;

    private static final String COOKIE_NAME = "LOGIN_SUCCESS_SECURITY_KEY";

    private Map<String, SecurityContext> mockRedis = new HashMap<>();

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        HttpServletRequest request = requestResponseHolder.getRequest();

        String securityKey = getSecurityKeyFromCookie(request);
        log.info("loadContext securityKey: {}", securityKey);

        return getSecurityContextFromRedis(securityKey);
    }

    @Override
    public DeferredSecurityContext loadDeferredContext(HttpServletRequest request) {
        Supplier<SecurityContext> contextSupplier = () -> {
            String securityKey = getSecurityKeyFromCookie(request);
            log.info("loadDeferredContext securityKey: {}", securityKey);

            return getSecurityContextFromRedis(securityKey);
        };

        return new DeferredSecurityContext() {
            private SecurityContext context;
            private boolean loaded = false;

            @Override
            public SecurityContext get() {
                if (!loaded) {
                    this.context = contextSupplier.get();
                    this.loaded = true;
                }
                return this.context;
            }

            // true: 表示用户未认证，当前上下文是默认生成的，需要进行身份验证。
            // false: 表示用户已认证，当前上下文是从存储中加载的，可直接用于权限控制。
            @Override
            public boolean isGenerated() {
                SecurityContext context = get();

                if (context == null) {
                    return true;
                }

                Authentication authentication = context.getAuthentication();
                return authentication == null || authentication instanceof AnonymousAuthenticationToken;
            }
        };
    }

    // 登录成功时会调用saveContext方法保存信息
    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
        String securityKey = UUID.randomUUID().toString();
        addSecurityKeyCookie(response, securityKey);

        log.info("saveContext securityKey: {}", securityKey);
        mockRedis.put(securityKey, context);
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        String securityKey = getSecurityKeyFromCookie(request);
        log.info("containsContext securityKey: {}", securityKey);

        if (securityKey == null) {
            return false;
        }
        return mockRedis.containsKey(securityKey);
    }

    private SecurityContext getSecurityContextFromRedis(String securityKey) {
        if (securityKey == null) {
            return generateNewContext();
        }

        SecurityContext context = mockRedis.get(securityKey);
        if (context == null) {
            context = generateNewContext();
        }

        return context;
    }

    private String getSecurityKeyFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (COOKIE_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private void addSecurityKeyCookie(HttpServletResponse response, String securityKey) {
        Cookie cookie = new Cookie(COOKIE_NAME, securityKey);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(COOKIE_MAX_AGE);
        response.addCookie(cookie);
    }

    private SecurityContext generateNewContext() {
        return SecurityContextHolder.createEmptyContext();
    }
}
