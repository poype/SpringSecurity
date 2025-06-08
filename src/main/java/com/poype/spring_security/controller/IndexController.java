package com.poype.spring_security.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
public class IndexController {

    @GetMapping("/index")
    public Map<String, Object> index() {
        // 获取用户信息
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();

        Object principal = authentication.getPrincipal();
        Object credentials = authentication.getCredentials();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        Map<String, Object> result = new HashMap<>();
        result.put("principal", principal);
        result.put("credentials", credentials);
        result.put("authorities", authorities);

        return result;
    }

    @GetMapping("/user/list")
    public String userList() {
        return "User List Page";
    }

    @GetMapping("/user/add")
    public String userAdd() {
        return "User Add Page";
    }
}
