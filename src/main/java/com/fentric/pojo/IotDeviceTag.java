package com.fentric.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 设备标签表(属于多对多关系)
 * </p>
 *
 * @author zhouqi
 * @since 2022-09-18
 */
@TableName("iot_device_tag")
public class IotDeviceTag implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 设备标签表id
     */
    @TableId(value = "device_tag_id", type = IdType.AUTO)
    private Long deviceTagId;

    /**
     * 设备表id
     */
    private Long deviceId;

    /**
     * 标签表id
     */
    private Long tagId;

    /**
     * 本条权限是否被禁用(0没有,1禁用)
     */
    private String status;

    /**
     * 本条权限是否被删除(0没有,1删除)
     */
    private String deleted;

    /**
     * 备注
     */
    private String comment;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    public Long getDeviceTagId() {
        return deviceTagId;
    }

    public void setDeviceTagId(Long deviceTagId) {
        this.deviceTagId = deviceTagId;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
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
        return "IotDeviceTag{" +
        "deviceTagId = " + deviceTagId +
        ", deviceId = " + deviceId +
        ", tagId = " + tagId +
        ", status = " + status +
        ", deleted = " + deleted +
        ", comment = " + comment +
        ", createTime = " + createTime +
        ", updateTime = " + updateTime +
        "}";
    }
}
