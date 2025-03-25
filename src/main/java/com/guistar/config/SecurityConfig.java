package com.guistar.config;

import com.guistar.entity.Account;
import com.guistar.entity.utils.Const;
import com.guistar.entity.utils.RestBean;
import com.guistar.filter.JwtAuthFilter;
import com.guistar.filter.RequestFilter;
import com.guistar.service.AccountService;
import com.guistar.utils.JwtUtils;
import com.guistar.vo.AuthorizeVO;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfig {

    @Resource
    AccountService accountService;

    @Resource
    JwtUtils utils;

    @Resource
    RequestFilter requestFilter;

    @Resource
    JwtAuthFilter jwtAuthFilter;

    @Resource
    CorsConfig config;
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(conf -> conf.requestMatchers("/api/auth/**")
                .permitAll().anyRequest().hasAnyRole(Const.DEFAULT_ROLE))
                .cors(conf -> conf.configurationSource(config.corsConfigurationSource()))
                .formLogin(conf -> conf
                        .loginProcessingUrl("/api/auth/login")
                        .failureHandler(this::onAuthenticationFailure)
                        .successHandler(this::onAuthenticationSuccess))
                .logout(conf -> conf.logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler(this::onLogoutSuccess))
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(conf -> conf
                        .accessDeniedHandler(this::handle)
                        .authenticationEntryPoint(this::commence)
                )
                .addFilterBefore(requestFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthFilter,requestFilter.getClass())
                .sessionManagement(conf -> conf.
                        sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
    }
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        User user = (User) authentication.getPrincipal();
        Account ac = accountService.findAccountByUsernameOrEmail(user.getUsername());
        String jwt = utils.createJwt(ac.getId(),user,ac.getUsername());
        if(jwt == null) writer.write(RestBean.forbidden("请求频繁，请稍后再试").asJsonString());
        AuthorizeVO vo = ac.asViewObj(AuthorizeVO.class,o -> o.setToken(jwt));
        vo.setExpire(utils.expireTime());
        writer.write(RestBean.success(vo).asJsonString());
    }
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        String headerToken = request.getHeader("Authorization");
        if(utils.invalidateJwt(headerToken)){
            writer.write(RestBean.success("退出登录成功").asJsonString());
            return;
        }
        writer.write(RestBean.failure(400,"退出登录失败").asJsonString());
    }
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(RestBean.forbidden(accessDeniedException.getMessage()).asJsonString());
    }
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(RestBean.unauthorized(authException.getMessage()).asJsonString());
    }
}
