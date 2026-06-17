package com.example.campustrade.component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtils {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expire-time}")
    private Long expireTime;

    private SecretKey getKey(){
       return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    //用userId生成token
    public String generateToken(Long userId){

        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expireTime );

        return Jwts.builder()
                .subject(String.valueOf(userId))//存用户id
                .issuedAt(now)                  //现在时间
                .expiration(expireDate)         //过期时间
                .signWith(getKey())                  //签名密钥
                .compact();
    }

    //解析token 取出userId
    public Long getUserIdFromToken(String token){
        Claims claims = Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return Long.valueOf(claims.getSubject());
    }
}
