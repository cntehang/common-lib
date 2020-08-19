package com.tehang.common.infrastructure.filters;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * http response wrapper.
 */
public class ResponseWrapper extends HttpServletResponseWrapper {

  private transient ByteArrayOutputStream output;
  private transient FilterServletOutputStream filterOutput;

  /**
   * constructor.
   *
   * @param response
   */
  public ResponseWrapper(HttpServletResponse response) {
    super(response);
    output = new ByteArrayOutputStream();
  }

  /**
   * getDeptById output stream
   *
   * @return
   * @throws IOException
   */
  @Override
  public ServletOutputStream getOutputStream() throws IOException {
    if (filterOutput == null) {
      filterOutput = new FilterServletOutputStream(output);
    }
    return filterOutput;
  }

  /**
   * getDeptById data stream.
   *
   * @return
   */
  public byte[] getDataStream() {
    return output.toByteArray();
  }
}

