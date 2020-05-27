package com.tehang.common.utility.token;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tehang.common.utility.JsonUtils;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 内部系统中，传递的jwt的payload
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InnerJwtPayload implements Serializable {

  private static final long serialVersionUID = -877837145679953510L;

  private Long employeeId;
  private String clientType;
  private boolean admin;
  private Long staffId;
  private String staffName;

  /**
   * 外部链接登录时携带的出差申请单号，可以为null
   */
  private String approvalNo;

  private List<String> roles;

  /**
   * 创建一个新的payload
   *
   * @param employeeId
   * @param clientType
   * @param isAdmin
   * @param staffId
   * @param staffName
   * @return
   */
  public static InnerJwtPayload create(Long employeeId,
                                       String clientType,
                                       boolean isAdmin,
                                       Long staffId,
                                       String staffName
  ) {
    InnerJwtPayload payload = new InnerJwtPayload();
    payload.employeeId = employeeId;
    payload.clientType = clientType;
    payload.admin = isAdmin;
    payload.staffId = staffId;
    payload.staffName = staffName;
    return payload;
  }

  /**
   * 从Json字符串转换为对象
   *
   * @param payload
   * @return
   */
  public static InnerJwtPayload fromJson(String payload) {
    return JsonUtils.toClass(payload, InnerJwtPayload.class);
  }

  @Override
  public String toString() {
    return JsonUtils.toJson(this);
  }

}
