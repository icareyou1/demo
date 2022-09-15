package com.fentric.service.impl;

import com.fentric.pojo.SysRoleMenu;
import com.fentric.mapper.SysRoleMenuMapper;
import com.fentric.service.SysRoleMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色菜单权限表(属于多多对多关系) 服务实现类
 * </p>
 *
 * @author zhouqi
 * @since 2022-09-14
 */
@Service
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu> implements SysRoleMenuService {

}
