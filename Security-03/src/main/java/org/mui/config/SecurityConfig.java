package org.mui.config;

import org.mui.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsUtils;

@Configuration
@EnableWebSecurity
@PropertySource(value = "classpath:db.properties")
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private MyUserDetailsService userDetailsService;

    /**
     * 描述:
     * http方式走 Spring Security 过滤器链，在过滤器链中，给请求放行，而web方式是不走 Spring Security 过滤器链。
     * 通常http方式用于请求的放行和限制，web方式用于放行静态资源
     **/
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                //用于配置直接放行的
//                .antMatchers("/login","/**","/error").permitAll()
                .antMatchers("/login").permitAll()
                //请求授权的规则~
                .antMatchers("/vip/2").hasAnyAuthority("vip2")
                .antMatchers("/vip/3").hasAnyAuthority("vip3")
//                .anyRequest().authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .cors()
                .and()
                //禁用跨站伪造
                .csrf().disable();

        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = http.authorizeRequests();
        registry.requestMatchers(CorsUtils::isPreFlightRequest).permitAll();

        http.formLogin().loginProcessingUrl("login")
                        .failureForwardUrl("/error");
        http.logout();


    }

    /**
     * 描述: 静态资源放行，这里的放行，是不走 Spring Security 过滤器链
     **/
//    @Override
//    public void configure(WebSecurity web) throws Exception {
//        // 可以直接访问的静态数据
//        web.ignoring()
//                .antMatchers("/css/**")
//                .antMatchers("/html/**")
//                .antMatchers("/js/**");
//    }

    /**
     * 验证账号密码
     *
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }
}
