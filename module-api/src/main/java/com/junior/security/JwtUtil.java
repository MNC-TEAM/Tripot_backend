package com.junior.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.junior.dto.jwt.LoginCreateJwtDto;
import com.junior.exception.JwtErrorException;
import com.junior.exception.StatusCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    // JWT에서는 String 키를 사용하는 방식에서 SecretKey라는 객체를 키로 사용하는 방식으로 변경됨.
    private final SecretKey secretKey;

    public JwtUtil(@Value("${spring.jwt.secret}") String secret) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public Long getId(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("id", Long.class);
    }

    public String getUsername(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    //access or refresh
    public String getCategory(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }

    public Boolean isExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    public String createJwt(LoginCreateJwtDto loginCreateJwtDto, String category) {

        Date requestDate = Timestamp.valueOf(loginCreateJwtDto.requestTimeMs());
        Date expireDate = Timestamp.valueOf(loginCreateJwtDto.requestTimeMs());


        if (category.equals("access")) {
            expireDate = Timestamp.valueOf(loginCreateJwtDto.requestTimeMs().plusMinutes(1));
        } else if (category.equals("refresh")) {
            expireDate = Timestamp.valueOf(loginCreateJwtDto.requestTimeMs().plusMonths(6));
        }

        return Jwts.builder()
                .claim("category", category)
                .claim("id", loginCreateJwtDto.id())
                .claim("username", loginCreateJwtDto.username())
                .claim("role", loginCreateJwtDto.role())
                .issuedAt(requestDate)
                .expiration(expireDate)
                .signWith(secretKey)
                .compact();
    }


    /**
     * JWT의 헤더를 꺼내는 기능
     * @param token
     * @return JWT's header
     * @throws JsonProcessingException
     */
    public Map<String, String> parseHeaders(String token) throws JsonProcessingException {

        //JWT를 .을 기준으로 header, payload, signature 분리 -> 그 중 header 선택
        String header = token.split("\\.")[0];

        //header를 디코딩 및 매핑하여 리턴
        return new ObjectMapper().readValue(decodeHeader(header), Map.class);
    }

    /**
     * JWT를 Base64 디코딩
     * @param token
     * @return
     */
    private String decodeHeader(String token) {
        return new String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8);
    }


    /**
     * 토큰과 PK를 사용하여 Claim 리턴
     * @param token
     * @param publicKey
     * @return
     */
    public Claims getTokenClaims(String token, PublicKey publicKey) {
        try {
            return Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (MalformedJwtException e) {
            throw new JwtErrorException(StatusCode.INVALID_TOKEN);
        } catch (ExpiredJwtException e) {
            throw new JwtErrorException(StatusCode.EXPIRED_ACCESS_TOKEN);
        }
    }
}