package com.tehang.common.utility.log.operationlog;

import com.tehang.common.utility.JsonUtils;
import lombok.Data;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * 用户操作日志。用来记录用户对系统的重要操作，并提供菜单供业务人员查询。
 */
@Data
public class UserOperationLog implements Serializable {

  private static final long serialVersionUID = -1231234537057856201L;
  
  /**
   * 日志唯一标识，uuid
   */
  private String id;

  /**
   * 操作人id, employeeId/staffId
   */
  private String operatorId;

  /**
   * 操作人姓名, employeeName/staffName
   */
  private String operatorName;

  /**
   * 操作人所在公司id
   */
  private String operatorCorpId;

  /**
   * 操作人所在公司名称
   */
  private String operatorCorpName;

  /**
   * 操作人类型： Employee/Staff/System
   */
  private LogOperatorType operatorType;

  /**
   * 是否客服代操作?
   */
  private boolean fromAdmin;

  /**
   * 代操作的客服姓名
   */
  private String staffName;

  /**
   * 服务名，eg: tmc-services
   */
  private String svc;

  /**
   * 操作来源: IOS/Android/H5/Web等
   */
  private String operateSource;

  /**
   * 操作时间, 北京时间，格式：2021-12-24 13:55:45.000
   */
  private String createTime;

  /**
   * 操作对象id, 如果公司id，订单id等
   */
  private String objectId;

  /**
   * 操作对象名称，如公司名称，订单号等
   */
  private String objectName;

  /**
   * 操作名称，如预订机票，修改公司授信等
   */
  private String operationName;

  /**
   * 操作描述：对操作的简单描述
   */
  private String operationDesc;

  /**
   * 用户操作变更的属性集合
   */
  private List<ChangedAttribute> attributes;
  
  // ----------- 方法 ---------------

  public UserOperationLog() {
    this.id = UUID.randomUUID().toString();
    this.createTime = currentTime();
  }

  /**
   * 获取北京时区的当前时间, 格式为: yyyy-MM-dd HH:mm:ss.SSS
   */
  private static String currentTime() {
    var bjTimeZone = DateTimeZone.forID("+08:00");
    return DateTime.now().toDateTime(bjTimeZone).toString("yyyy-MM-dd HH:mm:ss.SSS");
  }

  @Override
  public String toString() {
    return JsonUtils.toJson(this);
  }
}
