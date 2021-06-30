package com.tehang.common.infrastructure.filters;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 用于读取返回的内容.
 */
class FilterServletOutputStream extends ServletOutputStream {

  private transient DataOutputStream output;

  /**
   * 构造函数.
   */
  public FilterServletOutputStream(OutputStream output) {
    super();
    this.output = new DataOutputStream(output);
  }

  /**
   * write.
   */
  @Override
  public void write(int value) throws IOException {
    output.write(value);
  }

  /**
   * write.
   */
  @Override
  public void write(byte[] bytes, int off, int len) throws IOException {
    output.write(bytes, off, len);
  }

  /**
   * write.
   */
  @Override
  public void write(byte[] bytes) throws IOException {
    output.write(bytes);
  }

  /**
   * is ready.
   */
  @Override
  public boolean isReady() {
    return false;
  }

  /**
   * set listener.
   */
  @Override
  public void setWriteListener(WriteListener writeListener) {
    // do nothing.
  }
}
