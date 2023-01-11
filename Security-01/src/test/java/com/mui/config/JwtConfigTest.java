package com.mui.config;

import io.jsonwebtoken.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.UUID;

@SpringBootTest
public class JwtConfigTest {

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

        //String token="xx.xxx.xx";
        String token="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6IlRvbSIsInJvbGUiOiJhZG1pbiIsInN1YiI6ImFkbWluLXRlc3QiLCJleHAiOjE2NzMzODcxMjgsImp0aSI6IjExNTgxY2Y2LTAwNmQtNDA5ZC1iMzQ3LWE5ZmRiZDY0MzU3MiJ9.p2TOXAHEvna-JFSgEDR8-p9hNkNksONUoBA6vHfh6sI";

        JwtParser jwtParser=Jwts.parser();
        Jws<Claims> claimsJws = jwtParser.setSigningKey(tokenSignKey).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();

        System.out.println(claims.get("username"));
        System.out.println(claims.getId());
    }

}
