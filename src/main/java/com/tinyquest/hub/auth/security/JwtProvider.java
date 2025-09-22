package com.tinyquest.hub.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {

    private final Key key;

    public JwtProvider(
        @Value("${jwt.secret:YourSuperSecretKeyForDevelopmentEnvironmentWhichIsLongEnough}") String secretKey
    ) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30; // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7; // 7일

    public String generateToken(Long id, String username) {

        // UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .subject(username)
                .claim("uid", id)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key) // SignatureAlgorithm 지정 없이 키만 사용
                .compact();
    }

    public String generateRefreshToken(Long id, String username) {

        // UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .subject(username)
                .claim("uid", id)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key) // SignatureAlgorithm 지정 없이 키만 사용
                .compact();
    }

    // **JWT 토큰에서 사용자명 추출**
    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    public <T> T getValueFromTokenByKey(String token, String key, Class<T> requiredType) {
        return parseClaims(token).get(key, requiredType);
    }

    // **JWT 토큰 검증**
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // **토큰의 클레임 정보 파싱**
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


//    public Authentication getAuthentication(String token) {
//        String username = getUsernameFromToken(token);
//        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
//        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
//    }

//    public String getUsernameFromToken(String token) {
//        Claims claims = Jwts.parserBuilder()
//                .setSigningKey(key)
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//        return claims.getSubject();
//    }
//
//    public boolean validateToken(String token) {
//        try {
//            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
//            return true;
//        } catch (Exception ex) {
//            // In a real app, you'd want to log these exceptions.
//            // e.g., MalformedJwtException, ExpiredJwtException, UnsupportedJwtException, IllegalArgumentException
//        }
//        return false;
//    }
}
