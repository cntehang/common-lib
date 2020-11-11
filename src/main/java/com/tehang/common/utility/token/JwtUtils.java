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
   * 生成一个新的 JWT Token
   * 该 Token 被使用的时间需要在其 issueAt 时间之后，若服务器之间存在时钟差，有概率出现 The token can't be used before 问题
   * 故对 issueAt 取当前时间的三分钟前，避免这种由于服务器间时钟的微小差距带来的 Token 无法正常使用问题
   */
  public static String createToken(InnerJwtPayload payload, String secret, String issuer, String aud) throws UnsupportedEncodingException {
    Date issueAt = Date.from(new Date().toInstant().minusSeconds(180));
    return doCreateToken(payload, secret, issuer, aud, issueAt);
  }

  private static String doCreateToken(InnerJwtPayload payload, String secret, String issuer, String aud, Date issueAt) throws UnsupportedEncodingException {
    try {

      return JWT.create()
          .withIssuedAt(issueAt)
          .withIssuer(issuer)
          .withAudience(aud)
          .withClaim(CUSTOMER_PAYLOAD, payload.toString())
          .sign(Algorithm.HMAC256(secret));
    } catch (UnsupportedEncodingException exception) {
      LOG.debug("create token failed: {}", exception.getMessage(), exception);
      throw exception;
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
    } catch (UnsupportedEncodingException exception) {
      // ignore
      LOG.debug("decode token failed: {}", exception.getMessage(), exception);
      throw exception;
    }
  }

}
