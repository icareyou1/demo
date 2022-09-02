package com.fentric.service.impl;

import com.fentric.pojo.SysRole;
import com.fentric.mapper.SysRoleMapper;
import com.fentric.service.SysRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色表(与用户表1对1) 服务实现类
 * </p>
 *
 * @author zhouqi
 * @since 2022-09-01
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

}
