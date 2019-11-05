package com.tehang.common.utility.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * JWT 工具类
 */
public final class JwtUtils {

  private static final Logger LOG = LoggerFactory.getLogger(JwtUtils.class);
  private static final String CUSTOMER_PAYLOAD = "CustomerPayload";

  /**
   * constructor
   */
  private JwtUtils() {
    // do nothing
  }

  /**
   * 生成一个新的token
   *
   * @return
   */
  public static String createToken(InnerJwtPayload payload, String secret, String issuer, String aud) throws UnsupportedEncodingException {

    try {

      return JWT.create()
          .withIssuedAt(new Date())
          .withIssuer(issuer)
          .withAudience(aud)
          .withClaim(CUSTOMER_PAYLOAD, payload.toString())
          .sign(Algorithm.HMAC256(secret));
    } catch (UnsupportedEncodingException e) {
      LOG.debug("create token failed.", e.getMessage());
      throw e;
    }
  }

  /**
   * 获得token中的payload，同时验证token是否合法，如果不合法，则返回相关错误信息
   *
   * @return
   */
  public static InnerJwtPayload getPayload(String token, String secret) throws UnsupportedEncodingException {
    try {
      DecodedJWT tokenDecoded = JWT.require(Algorithm.HMAC256(secret)).build().verify(token);
      tokenDecoded.getPayload();
      InnerJwtPayload payload = InnerJwtPayload.fromJson(tokenDecoded.getClaim(CUSTOMER_PAYLOAD).asString());
      return payload;
    } catch (UnsupportedEncodingException e) {
      // ignore
      LOG.debug("decode token failed.", e.getMessage());
      throw e;
    }
  }

}
