package org.mui.utils;

import io.jsonwebtoken.*;
import org.mui.entity.User;

import java.util.Date;
import java.util.UUID;

public class JwtUtil {

    private static long time=1000*60*60*60;
    private static String tokenSignKey="signatureKey";

    public static String createToken(User user){
        JwtBuilder jwtBuilder= Jwts.builder();
        String jwtToken=jwtBuilder
                //header
                .setHeaderParam("typ","JWT")
                .setHeaderParam("alg","Hs256")
                //payload
                .claim("username",user.getUsername())
//                .claim("role","vip1")
//                .setSubject("admin-test")
                .setExpiration(new Date(System.currentTimeMillis()+time))
                .setId(UUID.randomUUID().toString())
                //signature
                .signWith(SignatureAlgorithm.HS256,tokenSignKey)
                //拼接
                .compact();

        return jwtToken;
    }


    public static Boolean checkToken(String token){
        if (token==null){
            System.out.println("后端check:token==null");
            return false;
        }
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
            Claims claims = claimsJws.getBody();
            String username = (String) claims.get("username");
            System.out.println("checkToken成功 | username:"+username);
        } catch (Exception e) {
            System.out.println("后端check:false");
            return false;
        }
        return true;
    }
}
