package org.mui.config;

import org.mui.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)  //开启使用注解设置权限
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    //权限
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //首页所有人可以访问，功能页只有对应有权限的人才能访问
        //请求授权的规则~
        http.authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/a/**").hasRole("vip1")
                .antMatchers("/b/**").hasRole("vip2")
                .antMatchers("/c/**").hasRole("vip3");

//        http.authorizeRequests()
//                .antMatchers("/").permitAll()
//                .antMatchers("/a/**").hasAnyAuthority("vip1")
//                .antMatchers("/b/**").hasAnyAuthority("vip2")
//                .antMatchers("/c/**").hasAnyAuthority("vip3");
        //跳转登陆页面
        http.formLogin()
                .loginPage("/toLogin")          //自定义登陆界面
                .usernameParameter("user")      //修改用户名参数
                .passwordParameter("pwd")       //修改密码参数
                .loginProcessingUrl("/toLogin");//自定义登陆请求地址

        //CSRF(跨站请求伪造漏洞）关闭，不然自定义登陆界面可能不生效
        //csrf与JWT都会进行token验证，csrf可以关闭
        http.csrf().disable();

        //退出登陆
        http.logout()
                .logoutSuccessUrl("/");

        //记住我，cookie保存
        http.rememberMe()
                .rememberMeParameter("remember");
    }

    @Autowired
    private MyUserDetailsService userDetailsService;
    //认证
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        //设置密码，禁止明文密码
        String pwd=new BCryptPasswordEncoder().encode("123456");

        //方式一: 直接通过用户名密码设置
        auth.inMemoryAuthentication()
                .passwordEncoder(new BCryptPasswordEncoder())           //开启密码编译器
                .withUser("muieay").password(pwd).roles("vip1")
                .and()
                .withUser("root").password(pwd).roles("vip1","vip2","vip3");

        //方式二: 实现UserDetailsService接口，注入
//        auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }
}
