package com.poype.spring_security.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.DeferredSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;

public class RedisSecurityContextRepository implements SecurityContextRepository {

    private static final String COOKIE_NAME = "SECURITY_KEY";

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        System.out.println("MY  ------  loadContext");
        return null;
    }

    @Override
    public DeferredSecurityContext loadDeferredContext(HttpServletRequest request) {
        System.out.println("MY  ------  loadDeferredContext");
        return SecurityContextRepository.super.loadDeferredContext(request);
    }

    // 登录成功时会调用saveContext方法保存信息
    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
        System.out.println("MY  ------  saveContext");
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        System.out.println("MY  ------  containsContext");
        return false;
    }
}
