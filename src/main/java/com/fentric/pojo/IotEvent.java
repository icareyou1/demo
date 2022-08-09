package com.fentric.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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

    private Short d1091;

    private Short d1100;

    private Short d1101;

    private Short d1102;

    private Short d1103;

    private Short d1104;

    private Short d1105;

    private Short d1106;

    private Short d1107;

    private Short d1108;

    private Short d1109;

    private Short d1110;

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

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public Short getd1091() {
        return d1091;
    }

    public void setd1091(Short d1091) {
        this.d1091 = d1091;
    }

    public Short getd1100() {
        return d1100;
    }

    public void setd1100(Short d1100) {
        this.d1100 = d1100;
    }

    public Short getd1101() {
        return d1101;
    }

    public void setd1101(Short d1101) {
        this.d1101 = d1101;
    }

    public Short getd1102() {
        return d1102;
    }

    public void setd1102(Short d1102) {
        this.d1102 = d1102;
    }

    public Short getd1103() {
        return d1103;
    }

    public void setd1103(Short d1103) {
        this.d1103 = d1103;
    }

    public Short getd1104() {
        return d1104;
    }

    public void setd1104(Short d1104) {
        this.d1104 = d1104;
    }

    public Short getd1105() {
        return d1105;
    }

    public void setd1105(Short d1105) {
        this.d1105 = d1105;
    }

    public Short getd1106() {
        return d1106;
    }

    public void setd1106(Short d1106) {
        this.d1106 = d1106;
    }

    public Short getd1107() {
        return d1107;
    }

    public void setd1107(Short d1107) {
        this.d1107 = d1107;
    }

    public Short getd1108() {
        return d1108;
    }

    public void setd1108(Short d1108) {
        this.d1108 = d1108;
    }

    public Short getd1109() {
        return d1109;
    }

    public void setd1109(Short d1109) {
        this.d1109 = d1109;
    }

    public Short getd1110() {
        return d1110;
    }

    public void setd1110(Short d1110) {
        this.d1110 = d1110;
    }

    public String getd1120to1365() {
        return d1120to1365;
    }

    public void setd1120to1365(String d1120to1365) {
        this.d1120to1365 = d1120to1365;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "IotEvent{" +
        "eventId = " + eventId +
        ", deviceId = " + deviceId +
        ", d1091 = " + d1091 +
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
