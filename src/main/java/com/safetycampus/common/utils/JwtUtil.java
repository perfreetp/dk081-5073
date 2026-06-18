package com.safetycampus.common.utils;

import com.safetycampus.common.context.LoginUser;
import com.safetycampus.common.enums.RoleTypeEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expire}")
    private Long expire;

    public String createToken(LoginUser loginUser) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", loginUser.getUserId());
        claims.put("username", loginUser.getUsername());
        claims.put("roleType", loginUser.getRoleType().getCode());
        claims.put("schoolId", loginUser.getSchoolId());
        claims.put("schoolName", loginUser.getSchoolName());
        claims.put("realName", loginUser.getRealName());
        claims.put("phone", loginUser.getPhone());

        Date now = new Date();
        Date expiration = new Date(now.getTime() + expire * 1000);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public LoginUser parseToken(String token) {
        Claims claims = getClaims(token);

        return LoginUser.builder()
                .userId(claims.get("userId", Long.class))
                .username(claims.get("username", String.class))
                .roleType(RoleTypeEnum.getByCode(claims.get("roleType", Integer.class)))
                .schoolId(claims.get("schoolId", Long.class))
                .schoolName(claims.get("schoolName", String.class))
                .realName(claims.get("realName", String.class))
                .phone(claims.get("phone", String.class))
                .build();
    }

    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        Jws<Claims> claimsJws = Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token);
        return claimsJws.getBody();
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
