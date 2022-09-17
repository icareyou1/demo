package com.fentric.controller;

import com.fentric.domain.ResponseResult;
import com.fentric.pojo.SysOrg;
import com.fentric.service.SysOrgService;
import com.fentric.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * <p>
 * 组织部门表(与用户表1对1) 前端控制器
 * </p>
 *
 * @author zhouqi
 * @since 2022-09-01
 */
@RestController
@RequestMapping("/sysOrg")
public class SysOrgController {
    @Autowired
    SysOrgService sysOrgService;
    //查询组织表
    @GetMapping("/listOrg")
    @PreAuthorize("@fentric.hasAuthority(('system:org:list'))")
    public ResponseResult listOrg(String orgName,String status){
        return sysOrgService.listOrg(orgName,status);
    }
    //根据orgId查询组织
    @GetMapping("/getOrgByOrgId")
    public ResponseResult getOrgByOrgId(@RequestParam("orgId") Long orgId){
        if (orgId<=0){
            return new ResponseResult(500,"查询组织,参数不合法");
        }
        return sysOrgService.getOrgByOrgId(orgId);
    }
    //新增组织表
    @PostMapping ("/addOrg")
    @PreAuthorize("@fentric.hasAuthority(('system:org:add'))")
    public ResponseResult addOrg(@RequestBody SysOrg sysOrg){
        //要求id为null,orgName不能为null,status为0或1  向下继续执行
        if (sysOrg.getOrgId()!=null
                || CommonUtils.isNullOrEmptyStr(sysOrg.getOrgName())
                || !CommonUtils.isValidateStatus(sysOrg.getStatus())){
            return new ResponseResult(500,"新增组织参数不合法");
        }
        return sysOrgService.addOrg(sysOrg);
    }
    //修改组织表
    @PutMapping("/updateOrg")
    @PreAuthorize("@fentric.hasAuthority(('system:org:update'))")
    public ResponseResult updateOrg(@RequestBody SysOrg sysOrg){
        //要求参数id不为null不小于0,orgName不能为null,status为0或1
        if (!CommonUtils.isValidateId(sysOrg.getOrgId())
                || !CommonUtils.isValidateStatus(sysOrg.getStatus())
                || CommonUtils.isNullOrEmptyStr(sysOrg.getOrgName())){
            return new ResponseResult(500,"修改组织参数不合法");
        }
        return sysOrgService.updateOrg(sysOrg);
    }
    //删除组织表
    @DeleteMapping("/deleteOrgByOrgId")
    @PreAuthorize("@fentric.hasAuthority(('system:org:delete'))")
    public ResponseResult deleteOrgByOrgId(@RequestParam("orgId") Long orgId){
        if (orgId<=0){
            return new ResponseResult(500,"删除组织,参数不合法");
        }
        return sysOrgService.deleteOrgByOrgId(orgId);
    }
    @GetMapping("/listOrgExcludeChild")
    //根据id 查询父组织
    public ResponseResult listOrgExcludeChild(@RequestParam("orgId") Long orgId){
        if (orgId<=0){
            return new ResponseResult(500,"删除组织,参数不合法");
        }
        return sysOrgService.listOrgExcludeChild(orgId);
    }
    //查询树形组织列表  [id: label: children:]
    @GetMapping("/orgTreeSelect")
    public ResponseResult orgTreeSelect(){
        return sysOrgService.orgTreeSelect();
    }
}
