package org.mui.config;

import org.mui.entity.RestBean;
import org.mui.entity.dto.Account;
import org.mui.entity.vo.AuthorizeVO;
import org.mui.filter.JwtAuthorizeFilter;
import org.mui.service.AccountService;
import org.mui.utils.JwtUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


@Configuration
public class SecurityConfig {

    @Resource
    private JwtUtils jwtUtils;

    @Resource
    private JwtAuthorizeFilter jwtAuthorizeFilter;

    @Resource
    private AccountService accountService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http
                //接口管理
                .authorizeHttpRequests(conf -> conf
                        .antMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(conf -> conf
                        .loginProcessingUrl("/api/auth/login")
                        //登录成功处理
                        .successHandler(this::onAuthenticationSuccess)
                        //登录失败处理
                        .failureHandler(this::onAuthenticationFailure)
                )
                .logout(conf->conf
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler(this::onLogoutSuccess)

                )
                .exceptionHandling(conf-> conf
                        //校验jwt失败处理
                        .authenticationEntryPoint(this::commence)
                        //校验权限失败处理
                        .accessDeniedHandler(this::handle)
                )
                .csrf(AbstractHttpConfigurer::disable)
                //session改为无状态
                .sessionManagement(conf->conf.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //添加过滤器
                .addFilterBefore(jwtAuthorizeFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
        //获取用户消息->UserDetails
        User user = (User) authentication.getPrincipal();
        //获取用户消息->实际用户
        Account account = accountService.findAccountByNameOrEmail(user.getUsername());
        //创建token
        String token = jwtUtils.creatToken(user, account.getId(), account.getUsername());

        // 将数据存入vo -> vo用于前端交互，dto用于数据层更严谨
        AuthorizeVO vo = new AuthorizeVO();
        BeanUtils.copyProperties(account,vo);
        vo.setExpire(jwtUtils.expiresTime());
        vo.setToken(token);
        response.getWriter().write(RestBean.success(vo).asJsonString());
    }

    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(RestBean.failure(401,"请求失败").asJsonString());
    }
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(RestBean.failure(403,"请求失败").asJsonString());
    }
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(RestBean.failure(401,"登录失败").asJsonString());
    }

    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        String authorization = request.getHeader("Authorization");
        //让指定Jwt令牌失效
        if (jwtUtils.invalidateJwt(authorization)) {
            writer.write(RestBean.success("退出登录成功").asJsonString());
            return;
        }
        writer.write(RestBean.failure(400, "退出登录失败").asJsonString());
    }

}
