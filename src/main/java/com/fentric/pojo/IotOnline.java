package com.fentric.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用来记录设备的在线情况,启动前要进行数据库的写入,关闭后要写入数据库
 * </p>
 *
 * @author zhouqi
 * @since 2022-08-15
 */
@TableName("iot_online")
public class IotOnline implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "online_id", type = IdType.AUTO)
    private Long onlineId;

    private Long deviceId;

    /**
     * 1在线，2离线，3网关不在线，4服务器不在
     */
    private String lost;

    private String status;

    private String deleted;

    private String comment;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    public Long getOnlineId() {
        return onlineId;
    }

    public void setOnlineId(Long onlineId) {
        this.onlineId = onlineId;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public String getLost() {
        return lost;
    }

    public void setLost(String lost) {
        this.lost = lost;
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
        return "IotOnline{" +
        "onlineId = " + onlineId +
        ", deviceId = " + deviceId +
        ", lost = " + lost +
        ", status = " + status +
        ", deleted = " + deleted +
        ", comment = " + comment +
        ", createTime = " + createTime +
        ", updateTime = " + updateTime +
        "}";
    }
}
