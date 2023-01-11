# SpringSecurity

SpringSecurity+JWT 快速基础入门

### 快速入门 SpringSecurity

- 创建web项目

- 导入相关依赖

  ```xml
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-security</artifactId>
  </dependency>
  ```

- 启动项目：

  默认用户名：user，密码：控制台输出

  



### SpringSecurity基础配置

> 使用配置需要基础 `WebSecurityConfigurerAdapter`,开启`@EnableWebSecurity`注解

开启权限认证：

<i>提供两种简单入门方式，实际开发可以结合Redis使用，减少数据库压力。</i>

##### 方式一：不做封装

直接`withUser`

```java
@Configuration
@EnableWebSecurity
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

    }
}
```

##### 方式二：封装`用户详细信息`

实现`UserDetailsService`接口

```java
@Service
public class MyUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //设置认证权限
        List<GrantedAuthority> auths =
                AuthorityUtils.createAuthorityList("vip2","vip3");

        return new User(username,new BCryptPasswordEncoder().encode("123456"),auths);
    }
}
```

配置类上调用`auth.userDetailsService()`实现注入

```java
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)  //开启使用注解设置权限
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    //权限
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        ~~~~
            
        http.authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/a/**").hasAnyAuthority("vip1")
                .antMatchers("/b/**").hasAnyAuthority("vip2")
                .antMatchers("/c/**").hasAnyAuthority("vip3");
    }
    @Autowired
    private MyUserDetailsService userDetailsService;
    
    //认证
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //方式二: 实现UserDetailsService接口，注入
        auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }
}
```

设置接口测试

<mark>SpringBoot+thymeleaf使用@RestController导致HTML无法解析作用域中数据</mark>
@RestController注解相当于@ResponseBody ＋ @Controller合在一起的作用。
在@ResponseBody将返回值设置为JSON格式,无法直接渲染视图
在@Controller注解中，返回的是字符串，或者是字符串匹配的模板名称，即直接渲染视图，与html页面配合使用的

```java
@Controller
public class HelloController {

    @RequestMapping ({"/","index"})
    public String hello(){
        System.out.println("Hello Index");
        return "index";
    }

    @RequestMapping("/toLogin")
    public String toLogin(){
        return "login";
    }

    @RequestMapping("/a/{id}")
    @PreAuthorize("hasAnyAuthority('vip1')")  //设置权限规则（方式二，需要设置prePostEnabled=true）
    public String toA(@PathVariable int id){
        return "a/"+id;
    }

    @RequestMapping("/b/{id}")
    public String toB(@PathVariable int id){
        return "b/"+id;
    }

    @RequestMapping("/c/{id}")
    public String toC(@PathVariable int id){
        return "c/"+id;
    }
}
```





# JWT快速入门

JWT （JSON Web Token） 是目前最流行的跨域认证解决方案，是一种基于 Token 的认证授权机制。 从 JWT 的全称可以看出，JWT 本身也是 Token，一种规范化之后的 JSON 结构的 Token。

[详细笔记☝️](https://muieay.github.io/2022/12/15/MD/JWT/)

```java
//设置超时时间
private long time=1000*60*60*24;
//签名密钥
private String tokenSignKey="signatureKey";

/**
 * jwtToken设置
 */
@Test
public void jwt(){
    JwtBuilder jwtBuilder= Jwts.builder();
    String jwtToken=jwtBuilder
            //header 请求头
            .setHeaderParam("typ","JWT")
            .setHeaderParam("alg","Hs256")
            //payload 载荷
            .claim("username","Tom")
            .claim("role","admin")
            .setSubject("admin-test")
            .setExpiration(new Date(System.currentTimeMillis()+time))
            .setId(UUID.randomUUID().toString())
            //signature 签名
            .signWith(SignatureAlgorithm.HS256,tokenSignKey)
            //拼接
            .compact();
}

/**
 * 解析
 */
@Test
public void parse(){

    String token="xx.xxx.xx";

    JwtParser jwtParser=Jwts.parser();
    Jws<Claims> claimsJws = jwtParser.setSigningKey(tokenSignKey).parseClaimsJws(token);
    Claims claims = claimsJws.getBody();

    System.out.println(claims.get("username"));
    System.out.println(claims.getId());
}
```

相关依赖：注意JDK版本问题，此处使用jdk17

```xml
<!--  JWT 及相关依赖   -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt</artifactId>
    <version>0.9.1</version>
</dependency>
<dependency>
    <groupId>javax.xml.bind</groupId>
    <artifactId>jaxb-api</artifactId>
    <version>2.3.1</version>
</dependency>
<dependency>
    <groupId>com.sun.xml.bind</groupId>
    <artifactId>jaxb-impl</artifactId>
    <version>2.3.0</version>
</dependency>
<dependency>
    <groupId>com.sun.xml.bind</groupId>
    <artifactId>jaxb-core</artifactId>
    <version>2.3.0</version>
</dependency>
<dependency>
    <groupId>javax.activation</groupId>
    <artifactId>activation</artifactId>
    <version>1.1.1</version>
</dependency>
```
