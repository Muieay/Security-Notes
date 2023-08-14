package org.mui.filter;
import org.mui.utils.Const;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 跨域配置过滤器，仅处理跨域，添加跨域响应头
 */
@Component
@Order(Const.ORDER_CORS)
public class CorsFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response,
                            FilterChain chain) throws IOException, ServletException {

        this.addCorsHeader(request, response);
        chain.doFilter(request, response);
    }

    private void addCorsHeader(HttpServletRequest request, HttpServletResponse response) {
        // 设置允许跨域的域名
        response.addHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        //response.addHeader("Access-Control-Allow-Origin", "http://localhost:8080/");
        // 设置允许的请求方式
        response.addHeader("Access-Control-Allow-Methods", "GET, HEAD, POST, PUT, DELETE, OPTIONS, TRACE, PATCH");
        // 设置允许的请求头
        response.addHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
    }

}
