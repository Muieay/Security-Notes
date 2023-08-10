package org.mui.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtils {
    @Value("${spring.security.jwt.key}")
    private String key;

    @Value("${spring.security.jwt.expires}")
    private int expires;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 创建jwt-token
     * @param details
     * @param id
     * @param name
     * @return
     */
    public String creatToken(UserDetails details, int id, String name){
        //指定签名算法
        Algorithm algorithm=Algorithm.HMAC256(key);
        return JWT.create()
                .withJWTId(UUID.randomUUID().toString()) //该jwt-id用于在用户退出后删除token
                .withClaim("id",id)
                .withClaim("name",name)
                .withClaim("authority",details.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .withExpiresAt(this.expiresTime())
                .withIssuedAt(new Date())
                .sign(algorithm);
    }

    /**
     * 解析Jwt令牌
     * @param headerToken 请求头中携带的令牌
     * @return DecodedJWT
     */
    public DecodedJWT resolveJwt(String headerToken){
        //校验并转换请求头中的Token令牌
        String token = this.convertToken(headerToken);
        if (token==null) return null;
        //指定签名算法
        Algorithm algorithm = Algorithm.HMAC256(key);
        //声明校验器
        JWTVerifier verifier = JWT.require(algorithm).build();

        try{
            //校验令牌
            DecodedJWT verify = verifier.verify(token);
            //检查令牌是否过期
            if (this.isInvalidToken(verify.getId()))
                return null;
            Date expiresAt = verify.getExpiresAt();
            return new Date().after(expiresAt) ? null :verify;
        }catch (JWTVerificationException e){
            return null;
        }
    }

    /**
     * 将jwt对象中的内容封装为UserDetails
     * @param jwt 已解析的Jwt对象
     * @return UserDetails
     */
    public UserDetails toUser(DecodedJWT jwt) {
        /**
         * 得到用户对象
         * {authority=[], name="zhangs", id=1, exp=1692173399, iat=1691568599}
         */
        Map<String, Claim> claims = jwt.getClaims();
        return User
                .withUsername(claims.get("name").asString())
                .password("******")
                .authorities(claims.get("authority").asArray(String.class))
                .build();
    }

    /**
     * 将jwt对象中的用户ID提取出来
     * @param jwt 已解析的Jwt对象
     * @return 用户ID
     */
    public Integer toId(DecodedJWT jwt) {
        Map<String, Claim> claims = jwt.getClaims();
        return claims.get("id").asInt();
    }

    /**
     * 校验并转换请求头中的Token令牌
     * @param headerToken 请求头中的Token
     * @return 转换后的令牌
     */
    private String convertToken(String headerToken) {
        if(headerToken == null || !headerToken.startsWith("Bearer "))
            return null;
        return headerToken.substring(7);
    }

    /**
     * 根据配置快速计算过期时间
     * @return
     */
    public Date expiresTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR,expires*24);
        return calendar.getTime();
    }

    /**
     * 让指定Jwt令牌失效
     * @param headerToken 请求头中携带的令牌
     * @return 是否操作成功
     */
    public boolean invalidateJwt(String headerToken){
        String token = this.convertToken(headerToken);
        Algorithm algorithm = Algorithm.HMAC256(key);
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        try{
            DecodedJWT verify = jwtVerifier.verify(token);
            String id = verify.getId();
            Date expiresAt = verify.getExpiresAt();
            return deleteToken(id,expiresAt);
        }catch (JWTVerificationException e){
            return false;
        }
    }

    /**
     * 判断令牌是否过期，未过期则加入Redis黑名单
     * @param uuid
     * @param time
     * @return
     */
    private boolean deleteToken(String uuid,Date time){
        if (isInvalidToken(uuid)) return false;
        Date date = new Date();
        long expire = Math.max(time.getTime() - date.getTime(), 0);
        //加入Redis黑名单
        redisTemplate.opsForValue().set(Const.JWT_BLACK_LIST+uuid,"",expire,TimeUnit.MICROSECONDS);
        return true;
    }
    /**
     * 验证Token是否被列入Redis黑名单(判断令牌是否过期)
     * @param uuid 令牌ID
     * @return 是否操作成功
     */
    private boolean isInvalidToken(String uuid){
        return Boolean.TRUE.equals(redisTemplate.hasKey(Const.JWT_BLACK_LIST+uuid));
    }

}
