package com.poype.spring_security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 如果不加任何配置，那会有一个默认配置，默认配置跟这里的配置是一样的
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        // 这行配置会增加BasicAuthenticationFilter，它是浏览器弹窗认证，正常不会被使用到
//        httpSecurity.httpBasic(Customizer.withDefaults());

        // 这行配置会增加3个filter：UsernamePasswordAuthenticationFilter、DefaultLoginPageGeneratingFilter、DefaultLogoutPageGeneratingFilter
        httpSecurity.formLogin(Customizer.withDefaults());

        httpSecurity.authorizeHttpRequests((authorize) -> {
            authorize.anyRequest().authenticated();  // 所有的request都要进行认证，已经被authenticated的用户就能够access
        });

        return httpSecurity.build();
    }
}
