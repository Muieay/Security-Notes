package org.mui.filter;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.mui.utils.JwtUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthorizeFilter extends OncePerRequestFilter {

    @Resource
    private JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 获取请求头中的Authorization
        String authorization = request.getHeader("Authorization");

        // 解析JWT
        DecodedJWT jwt = jwtUtils.resolveJwt(authorization);

        // 如果JWT不为空，则将JWT转换为UserDetails
        if (jwt != null) {
            UserDetails user = jwtUtils.toUser(jwt);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

            // 设置详情
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            // 设置SecurityContextHolder中的Authentication
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // 设置id
            request.setAttribute("id", jwtUtils.toId(jwt));
        }
        // 执行过滤器
        filterChain.doFilter(request, response);
    }
}

