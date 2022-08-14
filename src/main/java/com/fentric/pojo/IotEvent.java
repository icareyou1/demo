package com.fentric.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 设备异常事件表(根据事件编号来确定)
 * </p>
 *
 * @author zhouqi
 * @since 2022-08-09
 */
@TableName("iot_event")
@Setter
@Getter
public class IotEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 异常事件id
     */
    @TableId(value = "event_id", type = IdType.AUTO)
    private Long eventId;

    /**
     * 设备id
     */
    private Long deviceId;

    private Integer d1090;

    private Integer d1100;

    private Integer d1101;

    private Integer d1102;

    private Integer d1103;

    private Integer d1104;

    private Integer d1105;

    private Integer d1106;

    private Integer d1107;

    private Integer d1108;

    private Integer d1109;

    private Integer d1110;

    /**
     * 格式为二进制位数
     */
    private String d1120to1365;

    private String status;

    private String deleted;

    /**
     * 备注
     */
    private String comment;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @Override
    public String toString() {
        return "IotEvent{" +
        "eventId = " + eventId +
        ", deviceId = " + deviceId +
        ", d1090 = " + d1090 +
        ", d1100 = " + d1100 +
        ", d1101 = " + d1101 +
        ", d1102 = " + d1102 +
        ", d1103 = " + d1103 +
        ", d1104 = " + d1104 +
        ", d1105 = " + d1105 +
        ", d1106 = " + d1106 +
        ", d1107 = " + d1107 +
        ", d1108 = " + d1108 +
        ", d1109 = " + d1109 +
        ", d1110 = " + d1110 +
        ", d1120to1365 = " + d1120to1365 +
        ", status = " + status +
        ", deleted = " + deleted +
        ", comment = " + comment +
        ", createTime = " + createTime +
        ", updateTime = " + updateTime +
        "}";
    }
}
