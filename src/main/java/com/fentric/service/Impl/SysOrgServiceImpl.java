package com.fentric.service.impl;

import com.fentric.pojo.SysOrg;
import com.fentric.mapper.SysOrgMapper;
import com.fentric.service.SysOrgService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 组织部门表(与用户表1对1) 服务实现类
 * </p>
 *
 * @author zhouqi
 * @since 2022-09-01
 */
@Service
public class SysOrgServiceImpl extends ServiceImpl<SysOrgMapper, SysOrg> implements SysOrgService {

}
