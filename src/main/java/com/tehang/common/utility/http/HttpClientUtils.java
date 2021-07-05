package com.tehang.common.utility.http;

import com.google.common.io.ByteStreams;
import com.tehang.common.infrastructure.exceptions.SystemErrorException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * HttpClient 工具类.
 */
public final class HttpClientUtils {

  private static final Logger LOG = LoggerFactory.getLogger(HttpClientUtils.class);

  private static final int TRY_LIMIT = 4;

  //从连接池中取连接的超时时间
  private static final int CONNECTION_REQUEST_TIMEOUT = 5 * 1000;

  //连接超时时间
  private static final int CONNECTION_TIMEOUT = 5 * 1000;

  //请求超时时间
  private static final int SOCKET_TIMEOUT = 5 * 60 * 1000;

  //每个路由最大连接数
  private static final int MAX_PER_ROUTE = 100;

  //总的最大连接数
  private static final int MAX_TOTAL = 100;

  // httpclient 对象，注意：这里面其实是一个可复用连接池，而非单个连接
  private static CloseableHttpClient httpClient;

  /**
   * 构造函数.
   */
  private HttpClientUtils() {
    // do nothing
  }

  /**
   * 获取共用的HttpClient.
   */
  public static CloseableHttpClient getSharedClient() {
    synchronized (httpClient) {
      if (httpClient == null) {
        httpClient = HttpClients.custom().setDefaultRequestConfig(getRequestConfig()).setConnectionManager(getConnectionManager())
          .disableAutomaticRetries().build();
      }
    }

    return httpClient;
  }

  /**
   * 获取新的HttpClient。 暂时不允许使用.
   */
  public static CloseableHttpClient getNewClient() {
    return HttpClients.custom().setDefaultRequestConfig(getRequestConfig()).setConnectionManager(getConnectionManager()).disableAutomaticRetries()
      .build();
  }

  /**
   * 发送post请求.
   */
  public static String httpPost(String url, String postData) throws IOException {
    LOG.info("Enter. Request url: {}, postData: {}.", url, postData);

    HttpPost postMethod = getHttpPost(url, postData);

    String result = executePost(getSharedClient(), postMethod);

    LOG.info("Response: {}.", result);

    return result;
  }

  /**
   * Retry when remote returns SC_BAD_GATEWAY. It happens when the proxy server cannot connect to supplier. We set the proxy server's TCP connection
   * timeout to a small number and the HTTP proxy returns SC_BAD_GATEWAY.
   *
   * <p>SC_BAD_GATEWAY may misfire for other errors, hopefully it is not often because retry is not free.
   */
  private static String executePost(CloseableHttpClient closeableHttpClient, HttpPost postMethod) throws IOException {
    String result;

    int tryCount = 1;
    while (true) {

      HttpResponse response = closeableHttpClient.execute(postMethod);
      // release after use, otherwise, retry fails when all connections are used

      int statusCode = response.getStatusLine().getStatusCode();

      if (statusCode == HttpStatus.SC_OK) {
        result = getResult(response.getEntity().getContent());
        break;
      }
      else if (statusCode != HttpStatus.SC_BAD_GATEWAY) {
        LOG.warn("Get data from supplier failed, tried count:{}, error code: {}.", tryCount, statusCode);
        throw new SystemErrorException("Get data from supplier failed,HttpStatus=" + statusCode);
      }

      if (tryCount > TRY_LIMIT) {
        LOG.warn("Get data from supplier failed, tried count:{}, error code: {}.", tryCount, statusCode);
        throw new SystemErrorException("Get data from supplier failed,HttpStatus=" + statusCode);
      }
      tryCount++;
      postMethod.releaseConnection();
    }
    return result;
  }

  /**
   * 获取 HttpPost 请求实体 默认采用"application/json; charset=utf-8"作为content type.
   *
   * @param url      请求地址
   * @param postData 请求数据
   */
  private static HttpPost getHttpPost(String url, String postData) {
    HttpPost httpPost = new HttpPost(url);

    httpPost.addHeader("Content-type", "application/json; charset=utf-8");
    httpPost.setHeader("Accept", "application/json");

    StringEntity se = new StringEntity(postData, Charset.forName("UTF-8"));
    se.setContentEncoding("UTF-8");
    httpPost.setEntity(se);

    return httpPost;
  }

  /**
   * 获取返回结果字符串.
   */
  private static String getResult(InputStream content) throws IOException {
    return new String(ByteStreams.toByteArray(content), Charset.forName("UTF-8"));
  }

  /**
   * 获取request的请求，主要设置相关timeout参数.
   */
  private static RequestConfig getRequestConfig() {
    return RequestConfig.custom().setSocketTimeout(SOCKET_TIMEOUT).setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
      .setConnectTimeout(CONNECTION_TIMEOUT).build();
  }

  /**
   * 获取connection manager对象，这里主要设置maxPerRoute以及maxTotal参数.
   */
  private static HttpClientConnectionManager getConnectionManager() {
    PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();

    cm.setDefaultMaxPerRoute(MAX_PER_ROUTE);
    cm.setMaxTotal(MAX_TOTAL);

    return cm;
  }
}