package com.fentric.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 设备信息表
 * </p>
 *
 * @author zhouqi
 * @since 2022-08-08
 */
@TableName("iot_device")
public class IotDevice implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 设备id
     */
    @TableId(value = "device_id", type = IdType.AUTO)
    private Long deviceId;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备图片
     */
    private String deviceImage;

    /**
     * 设备地址,自定义或字典
     */
    private String deviceAddress;

    /**
     * 设备激活时间
     */
    private LocalDateTime deviceActiveTime;

    /**
     * 设备ip地址
     */
    private String deviceIp;

    /**
     * 设备信息状态(0正常,1禁用)禁用设备卡应该为灰色
     */
    private String status;

    /**
     * 设备信息是否删除(0存在,1删除)
     */
    private String deleted;

    /**
     * 备注
     */
    private String comment;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 操作人id
     */
    private Long userId;

    /**
     * 设备类型id
     */
    private Long categoryId;

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceImage() {
        return deviceImage;
    }

    public void setDeviceImage(String deviceImage) {
        this.deviceImage = deviceImage;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public LocalDateTime getDeviceActiveTime() {
        return deviceActiveTime;
    }

    public void setDeviceActiveTime(LocalDateTime deviceActiveTime) {
        this.deviceActiveTime = deviceActiveTime;
    }

    public String getDeviceIp() {
        return deviceIp;
    }

    public void setDeviceIp(String deviceIp) {
        this.deviceIp = deviceIp;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public String toString() {
        return "IotDevice{" +
        "deviveId = " + deviceId +
        ", deviceName = " + deviceName +
        ", deviceImage = " + deviceImage +
        ", deviceAddress = " + deviceAddress +
        ", deviceActiveTime = " + deviceActiveTime +
        ", deviceIp = " + deviceIp +
        ", status = " + status +
        ", deleted = " + deleted +
        ", comment = " + comment +
        ", createTime = " + createTime +
        ", updateTime = " + updateTime +
        ", userId = " + userId +
        ", categoryId = " + categoryId +
        "}";
    }
}
