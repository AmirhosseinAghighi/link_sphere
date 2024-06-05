package org.linkSphere.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import app.database.schema.User;
import org.linkSphere.util.Logger;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

public class JWT {
    private static final String secret = "R07mLKFemnbURK8k5X8DRvBYGrNZjU60IDs5M2QrYD0=";
    private static SecretKey SECRET_KEY = null;
    private static final long accessTokenValidityInSeconds = 3600 * 1000;
    private static final long refreshTokenValidityInSeconds = 7 * 24 * 60 * 60 * 1000; // 365 days

    public static SecretKey getSigningKey() {
        if (SECRET_KEY == null) {
            byte[] decodedKey = Base64.getDecoder().decode(secret);
            SECRET_KEY = Keys.hmacShaKeyFor(decodedKey);
        }
        return SECRET_KEY;
    }

    public static String generateAccessTokenByUser(long userId) {
        return Jwts.builder()
                .setSubject(Long.toString(userId))
                .signWith(getSigningKey())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenValidityInSeconds))
                .compact();
    }

    public static String generateRefreshTokenByUser(long userId, String username) {
        return Jwts.builder()
                .setSubject(Long.toString(userId))
                .claim("username", username)
                // TODO: add other data's like user role and ...
                .signWith(getSigningKey())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenValidityInSeconds))
                .compact();
    }

    public static Claims parseToken(String token) throws JwtException {
        try {
            return Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            throw e;
        }
    }

    public static boolean verifyToken(String token) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            Logger.getLogger().debug("Token wasn't valid! message: ",  e.getMessage());
            return false;
        }
    }
}
