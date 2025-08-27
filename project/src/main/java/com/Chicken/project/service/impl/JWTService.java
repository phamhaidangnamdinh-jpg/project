package com.Chicken.project.service.impl;

import com.Chicken.project.dto.response.LoginResponse;
import com.Chicken.project.entity.V_User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {
    private static final Logger log = LoggerFactory.getLogger(JWTService.class);
    private String secretKey = "";

    public JWTService() {
        KeyGenerator keyGen = null;
        try {
            keyGen = KeyGenerator.getInstance("HmacSHA256");
            SecretKey sK = keyGen.generateKey();
            secretKey = Base64.getEncoder().encodeToString(sK.getEncoded());
            log.info("Secret key for JWT initialized.");
        } catch (NoSuchAlgorithmException e) {
            log.error("Failed to initialize secret key", e);
            throw new RuntimeException(e);
        }
    }

    public String generateAccessToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "access");
        String token = Jwts.builder()
                .claims()
                .add(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                .and()
                .signWith(getKey())
                .compact();
        log.debug("Access token generated for '{}'", username);
        return token;
    }

    public String generateRefreshToken(String username, String email) {
        Map<String, Object> claims = new HashMap<>();

        String token = Jwts.builder()
                .claim("email", email)
                .claim("type", "refresh")
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(Date.from(Instant.now().plus(Duration.ofDays(30))))
                .signWith(getKey())
                .compact();
        log.debug("Refresh token generated for '{}'", username);
        return token;
    }

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUserName(String token) {
        try {
            String username = extractClaim(token, Claims::getSubject);
            log.debug("Username '{}' extracted from token", username);
            return username;
        } catch (Exception e) {
            log.warn("Failed to extract username from token: {}", e.getMessage());
            throw e;
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            String username = extractUserName(token);
            boolean valid = username.equals(userDetails.getUsername()) && !isTokenExpired(token);

            if (!valid) {
                log.warn("Token validation failed for user '{}'", userDetails.getUsername());
            }
            return valid;
        } catch (Exception e) {
            log.warn("Token validation error: {}", e.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean isRefreshToken(String token) {
        return extractAllClaims(token).get("type").equals("refresh");
    }

    public boolean isValid(String token, V_User user) {
        try {
            Claims claims = extractAllClaims(token);

            Date expiration = claims.getExpiration();
            if (expiration.before(new Date())) {
                log.warn("Refresh token for '{}' is expired at {}", user.getUsername(), expiration);
                return false;
            }
            if (!"refresh".equals(claims.get("type", String.class))) {
                log.warn("Invalid token type '{}' for refresh operation (expected 'refresh')", claims.get("type", String.class));
                return false;
            }
            String tokenUsername = claims.getSubject();
            if (!user.getUsername().equals(tokenUsername)) {
                log.warn("Token username '{}' does not match user '{}'", tokenUsername, user.getUsername());
                return false;
            }
            if (!token.equals(user.getRefreshToken())) {
                log.warn("Refresh token mismatch for user '{}'", user.getUsername());
                return false;
            }

            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid token for user '{}': {}", user.getUsername(), e.getMessage());
            return false;
        }
    }
}
