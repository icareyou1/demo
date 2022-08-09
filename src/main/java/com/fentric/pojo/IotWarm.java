package com.fentric.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 告警表(借助缓存提高告警性能,如果和缓存中数据不同那就存入数据库,相同就不存入)
 * </p>
 *
 * @author zhouqi
 * @since 2022-08-09
 */
@TableName("iot_warm")
public class IotWarm implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 告警id
     */
    @TableId(value = "warm_id", type = IdType.AUTO)
    private Long warmId;

    /**
     * 告警设备id
     */
    private Long deviceId;

    /**
     * 16进制字符串(003F000C)
     */
    private String d1020to1021;

    private String status;

    private String deleted;

    /**
     * 备注
     */
    private String comment;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    public Long getWarmId() {
        return warmId;
    }

    public void setWarmId(Long warmId) {
        this.warmId = warmId;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public String getd1020to1021() {
        return d1020to1021;
    }

    public void setd1020to1021(String d1020to1021) {
        this.d1020to1021 = d1020to1021;
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
        return "IotWarm{" +
        "warmId = " + warmId +
        ", deviceId = " + deviceId +
        ", d1020to1021 = " + d1020to1021 +
        ", status = " + status +
        ", deleted = " + deleted +
        ", comment = " + comment +
        ", createTime = " + createTime +
        ", updateTime = " + updateTime +
        "}";
    }
}
