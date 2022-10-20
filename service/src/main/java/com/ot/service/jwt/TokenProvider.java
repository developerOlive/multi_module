package com.ot.service.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/*
    인증토큰 생성 / 리프레시토큰 생성 / 토큰 검증 / 토큰에서 정보 추출 등을 담당합니다.
 */
@Service
public class TokenProvider {
    private String secret = "987654321";

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    public String generateRefreshToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createRefreshToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        Date IssuedAt = new Date(System.currentTimeMillis());
        Date expireDate = new Date(System.currentTimeMillis() + (1000 * 60 * 30));   // 1800000 = 30분

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(IssuedAt)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    private String createRefreshToken(Map<String, Object> claims, String subject) {
        Date IssuedAt = new Date(System.currentTimeMillis());
        Date expireDate = new Date(System.currentTimeMillis() + (1000 * 60 * 300));   // 18000000 = 300분

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(IssuedAt)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        Boolean userCheck = username.equals(userDetails.getUsername());
        Boolean expiredCheck = !isTokenExpired(token);

        return (userCheck && expiredCheck);
    }
    public Boolean validateRefreshToken(String refreshToken) {
        return !isTokenExpired(refreshToken);
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // SecurityContext 에서 사용자 정보 가져오기
    public CustomUser getUserByPrincipal() {
        return (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    // 권한 체크
    public Boolean isCheckRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<String> roles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        return roles.contains(role);
    }
}
