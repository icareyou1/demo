package com.fentric.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fentric.mapper.SysRoleMapper;
import com.fentric.pojo.SysRole;
import com.fentric.service.SysRoleService;
import org.springframework.stereotype.Service;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Override
    public String test(Integer id) {
        System.out.println("----"+id);
        String test = baseMapper.test(id);
        return test;
    }
}
