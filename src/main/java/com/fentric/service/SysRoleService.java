package com.fentric.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fentric.pojo.SysRole;

public interface SysRoleService extends IService<SysRole> {

    String test(Integer id);
}
