package com.springgboot.refactor.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.springgboot.refactor.infrastructure.config.jwt.JwtProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Calendar;
import java.util.Date;

/**
 * Jwt(token生成工具)
 *
 * @author Elliot
 */
@Slf4j
@AllArgsConstructor
public class JwtUtil {

    private static Algorithm alg;
    private static JWTVerifier verifier;
    private static JwtProperties JWT_PROPERTIES;

    public static void init(JwtProperties jwtProperties) {
        JWT_PROPERTIES = jwtProperties;
        alg = Algorithm.HMAC384(JWT_PROPERTIES.getSecret());
        verifier = JWT
                .require(alg)
                .withIssuer(JWT_PROPERTIES.getIssuer())
                .build();
    }

    /**
     * 生成签名
     *
     * @param claim 用户名
     * @return token字符串
     */
    public static String createToken(String claim) {
        Calendar nowTime = Calendar.getInstance();
        //120分钟有效期
        nowTime.add(Calendar.MINUTE, 120);
        Date expiresDate = nowTime.getTime();
        return JWT.create()
                .withIssuer(JWT_PROPERTIES.getIssuer())
                .withClaim(
                        JWT_PROPERTIES.getParamKey(),
                        claim
                )
                .withIssuedAt(new Date())
                .withExpiresAt(expiresDate)
                .withClaim(
                        JWT_PROPERTIES.getParamDate(),
                        Calendar.getInstance().getTimeInMillis()
                )
                .sign(alg);
    }

    /**
     * 校验token是否正确
     *
     * @param token token字符串
     * @return true｜false
     */
    public static Boolean verify(String token) {
        try {
            return verifier.verify(token) != null;
        } catch (Exception e) {
            log.error("token 认证失败", e);
        }
        return false;
    }

    /**
     * 获取token中的信息
     *
     * @param token token字符串
     * @return token中的信息
     */
    public static String getClaim(String token) {
        return verifier
                .verify(token)
                .getClaim(JWT_PROPERTIES.getParamKey())
                .asString();
    }

}
