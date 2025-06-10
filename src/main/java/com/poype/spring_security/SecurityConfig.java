package com.poype.spring_security;

import com.poype.spring_security.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    // 如果不加任何配置，那会有一个默认配置，默认配置跟这里的配置是一样的
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        // 这行配置会增加BasicAuthenticationFilter，它是浏览器弹窗认证，正常不会被使用到
//        httpSecurity.httpBasic(Customizer.withDefaults());

        // 这行配置会增加3个filter：UsernamePasswordAuthenticationFilter、DefaultLoginPageGeneratingFilter、DefaultLogoutPageGeneratingFilter
//        httpSecurity.formLogin(Customizer.withDefaults());

        // 自定义登录页面
        httpSecurity.formLogin((formLogin) -> {
            formLogin.loginPage("/login.html").permitAll();   // 登录页面地址
            formLogin.loginProcessingUrl("/user/login");      // 登录接口

            // 定制参数名字，默认是 username 和 password
            formLogin.usernameParameter("test_username");
            formLogin.passwordParameter("test_password");

            // 登录成功时的handler
            formLogin.successHandler(new TestAuthenticationSuccessHandler());
            // 登录失败时的handler
            formLogin.failureHandler(new TestAuthenticationFailureHandler());
        });

        httpSecurity.authorizeHttpRequests((authorize) -> {
            // 必须放在anyRequest 前面
//            authorize.requestMatchers("/user/list").hasAuthority("USER_LIST");
//            authorize.requestMatchers("/user/add").hasAuthority("USER_ADD");

            authorize.anyRequest().authenticated();  // 所有的request都要进行认证，已经被authenticated的用户就能够access
        });

        // 登出成功时的handler
        httpSecurity.logout((logout) -> {
            logout.logoutSuccessHandler(new TestLogoutSuccessHandler());
        });

        // 当没有登陆就访问一个受限资源时的处理handler
        httpSecurity.exceptionHandling((exception) -> {
            exception.authenticationEntryPoint(new TestAuthenticationEntryPoint());

            // 指定当没有权限访问某个资源时，由哪个handler处理
            exception.accessDeniedHandler(new TestAccessDeniedHandler());
        });

        httpSecurity.sessionManagement((session) -> {
            // 一个用户同一时间最多只能有一个设备登录，session超时会调用TestSessionExpiredStrategy进行处理
            session.maximumSessions(1).expiredSessionStrategy(new TestSessionExpiredStrategy());
        });

        // 定义加载SecurityContext的方式，默认是从HttpSession中加载，改成从redis中加载SecurityContext
        httpSecurity.securityContext((securityContext) -> {
            securityContext.securityContextRepository(new RedisSecurityContextRepository());
        });

        return httpSecurity.build();
    }
}
