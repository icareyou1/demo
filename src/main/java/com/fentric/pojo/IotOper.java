package com.fentric.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 设备操作表(记录用户对哪个设备进行了什么操作,)
 * </p>
 *
 * @author zhouqi
 * @since 2022-09-09
 */
@TableName("iot_oper")
public class IotOper implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 设备操作表id
     */
    @TableId(value = "oper_id", type = IdType.AUTO)
    private Long operId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 操作对象名(和下面配套)
     */
    private String objName;

    /**
     * 操作对象id(如:param_id,device_id,controller_id...)
     */
    private Long objId;

    /**
     * 操作类型:控制,主动查询,配置参数
     */
    private String operType;

    /**
     * 操作成功或失败
     */
    private String operResult;

    /**
     * 操作错误消息
     */
    private String errorMsg;

    private String status;

    private String deleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    public Long getOperId() {
        return operId;
    }

    public void setOperId(Long operId) {
        this.operId = operId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getObjName() {
        return objName;
    }

    public void setObjName(String objName) {
        this.objName = objName;
    }

    public Long getObjId() {
        return objId;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
    }

    public String getOperType() {
        return operType;
    }

    public void setOperType(String operType) {
        this.operType = operType;
    }

    public String getOperResult() {
        return operResult;
    }

    public void setOperResult(String operResult) {
        this.operResult = operResult;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
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
        return "IotOper{" +
        "operId = " + operId +
        ", userId = " + userId +
        ", objName = " + objName +
        ", objId = " + objId +
        ", operType = " + operType +
        ", operResult = " + operResult +
        ", errorMsg = " + errorMsg +
        ", status = " + status +
        ", deleted = " + deleted +
        ", createTime = " + createTime +
        ", updateTime = " + updateTime +
        "}";
    }
}
