package com.fentric.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fentric.pojo.SysOrg;
import com.fentric.pojo.SysRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageUser implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;
    /**
     * 用户账号
     */
    private String userName;
    /**
     * 密码(给前端页面时不进行封装即可)
     */
    private String password;
    /**
     * 用户昵称
     */
    private String nickName;
    /**
     * 用户性别（0男 1女 2未知）
     */
    private String gender;
    /**
     * 手机号码
     */
    private String phoneNumber;
    /**
     * 用户邮箱
     */
    private String email;
    /**
     * 头像地址
     */
    private String avatar;
    /**
     * 帐号状态（0正常 1停用）
     */
    private String status;
    /**
     * 是否删除(0存在1,删除)
     */
    private String deleted;
    /**
     * 备注
     */
    private String comment;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    /**
     * 组织ID
     */
    private Long orgId;
    /**
     *  角色ID
     */
    private Long roleId;
    /**
     * 组织对象
     */
    private SysOrg sysOrg;
    /**
     * 角色对象
     */
    private SysRole sysRole;
}
