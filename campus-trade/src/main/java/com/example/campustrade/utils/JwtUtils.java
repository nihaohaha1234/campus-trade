package com.example.campustrade.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtUtils {

    private static final String SECRET = "campus-trade-secret-key-campus-trade-secret-key";

    private static final long EXPIRE_TIME = 1000*60*60*24;

    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    //用userId生成token
    public static String generateToken(Long userId){

        Date now = new Date();
        Date expireDate = new Date(now.getTime() + EXPIRE_TIME );

        return Jwts.builder()
                .subject(String.valueOf(userId))//存用户id
                .issuedAt(now)                  //现在时间
                .expiration(expireDate)         //过期时间
                .signWith(KEY)                  //签名密钥
                .compact();
    }

    //解析token 取出userId
    public static Long getUserIdFromToken(String token){
        Claims claims = Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return Long.valueOf(claims.getSubject());
    }
}
