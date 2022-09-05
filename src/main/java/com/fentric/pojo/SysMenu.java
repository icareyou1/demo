package com.fentric.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysMenu implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long menuId;
    private Long parentId;
    private String menuName;
    private String path;
    private String component;
    private String perms;
    private String icon;
    private String status;
    private String deleted;
    private String comment;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
