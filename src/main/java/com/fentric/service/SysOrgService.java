package com.fentric.service;

import com.fentric.domain.ResponseResult;
import com.fentric.pojo.SysOrg;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 组织部门表(与用户表1对1) 服务类
 * </p>
 *
 * @author zhouqi
 * @since 2022-09-01
 */
public interface SysOrgService extends IService<SysOrg> {
    //查询组织表
    ResponseResult listOrg(String orgName, String status);
    //添加组织表
    ResponseResult addOrg(SysOrg sysOrg);
    //修改组织表
    ResponseResult updateOrg(SysOrg sysOrg);
    //删除组织表
    ResponseResult deleteOrgByOrgId(Long orgId);
    //根据组织id查询组织
    ResponseResult getOrgByOrgId(Long orgId);
    //根据id  查询父组织
    ResponseResult listOrgExcludeChild(Long orgId);
    //查询左侧组织树形表
    ResponseResult orgTreeSelect();
}
