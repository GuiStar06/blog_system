package com.guistar.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.guistar.entity.utils.Const;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtils {


    @Value("${spring.security.oauth2.resourceserver.jwt.key}")
    private static String key;

    @Value("${spring.security.oauth2.resourceserver.jwt.expire}")
    private int expire;

    @Resource
    StringRedisTemplate template;

    private static final Algorithm algorithm = Algorithm.HMAC256(key);


    public Date expireTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR,expire);
        return calendar.getTime();
    }
    public String createJwt(long userId,UserDetails user, String username){
        Date expireTime = expireTime();
        return JWT.create().withJWTId(UUID.randomUUID().toString())
                .withClaim("username",username)
                .withClaim("id",userId)
                .withClaim("authorities",user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .withIssuedAt(new Date())
                .withExpiresAt(expireTime)
                .sign(algorithm);
    }
    public DecodedJWT resolveJwt(String headerToken){
        String token = convertToToken(headerToken);
        if(token == null) return null;
        JWTVerifier verifier = JWT.require(algorithm).build();
        try{
            DecodedJWT jwt = verifier.verify(token);
            if(isInvalidToken(jwt.getId())) return null;
            Map<String, Claim> claims = jwt.getClaims();
            return new Date().after(jwt.getExpiresAt()) ? jwt : null;
        }catch (JWTVerificationException e){
            return null;
        }
    }

    public boolean invalidateJwt(String headerToken){
        String token = convertToToken(headerToken);
        if(token == null) return false;
        JWTVerifier verifier = JWT.require(algorithm).build();
        try{
            DecodedJWT jwt = verifier.verify(token);
            if(isInvalidToken(jwt.getId())) return false;
            return deleteJwt(jwt.getId(),jwt.getExpiresAt());
        }catch (JWTVerificationException e){
            return false;
        }
    }
    public boolean deleteJwt(String uuid,Date expire){
        if(isInvalidToken(uuid)) return false;
        Date now = new Date();
        long expireTime = Math.max(0,expire.getTime() - now.getTime());
        template.opsForValue().set(Const.JWT_BLACK_LIST + uuid,"", expireTime,TimeUnit.MILLISECONDS);
        return true;
    }
    public String convertToToken(String headerToken){
        if(headerToken == null || !headerToken.startsWith("Bearer ")) return null;
        return headerToken.substring(7);
    }

    public boolean isInvalidToken(String uuid){
        return template.hasKey(Const.JWT_BLACK_LIST + uuid);
    }

    public UserDetails toUser(DecodedJWT jwt){
        Map<String,Claim> claims = jwt.getClaims();
        return User.withUsername(claims.get("username").asString())
                .password("******")
                .authorities(claims.get("authorities").asArray(String.class))
                .build();
    }

    public Integer toId(DecodedJWT jwt) {
        Map<String,Claim> claims = jwt.getClaims();
        return claims.get("id").asInt();
    }
}
