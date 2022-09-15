package com.fentric.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 角色菜单权限表(属于多多对多关系)
 * </p>
 *
 * @author zhouqi
 * @since 2022-09-14
 */
@TableName("sys_role_menu")
public class SysRoleMenu implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色菜单表id
     */
    @TableId(value = "role_menu_id", type = IdType.AUTO)
    private Long roleMenuId;

    /**
     * 用户表id
     */
    private Long roleId;

    /**
     * 菜单表id
     */
    private Long menuId;

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

    public Long getRoleMenuId() {
        return roleMenuId;
    }

    public void setRoleMenuId(Long roleMenuId) {
        this.roleMenuId = roleMenuId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
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
        return "SysRoleMenu{" +
        "roleMenuId = " + roleMenuId +
        ", roleId = " + roleId +
        ", menuId = " + menuId +
        ", status = " + status +
        ", deleted = " + deleted +
        ", comment = " + comment +
        ", createTime = " + createTime +
        ", updateTime = " + updateTime +
        "}";
    }
}
