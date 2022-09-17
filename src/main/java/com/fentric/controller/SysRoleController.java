package com.fentric.controller;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fentric.annotation.FentricLogin;
import com.fentric.domain.ResponseResult;
import com.fentric.domain.requestVO.RoleAddOrUpdateParams;
import com.fentric.domain.requestVO.RoleQueryParams;
import com.fentric.pojo.SysRole;
import com.fentric.pojo.SysRoleMenu;
import com.fentric.service.SysRoleMenuService;
import com.fentric.service.SysRoleService;
import com.fentric.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;


/**
 * <p>
 * 角色表(与用户表1对1) 前端控制器
 * </p>
 *
 * @author zhouqi
 * @since 2022-09-01
 */

@RestController
@RequestMapping("/sysRole")
public class SysRoleController {
    @Autowired
    SysRoleService sysRoleService;
    @Autowired
    SysRoleMenuService sysRoleMenuService;

    //查询角色表
    @GetMapping("/listRole")
    @PreAuthorize("@fentric.hasAuthority('system:role:list')")
    public ResponseResult listRole(RoleQueryParams roleQueryParams){
        return sysRoleService.listRole(roleQueryParams);
    }

    //根据角色id更改角色状态
    @PutMapping("/updateRoleStatusByRoleId")
    @PreAuthorize(("@fentric.hasAuthority('system:role:update')"))
    //这里只能用int 或 Integer来接收 ???
    public ResponseResult updateRoleStatusByRoleId(@FentricLogin("roleId")Integer roleId,@FentricLogin("status")String status){
        //参数为null,不会进入方法
        if (roleId==1){
            return new ResponseResult(500,"该角色不能操作");
        }
        if (roleId<=0||!CommonUtils.isValidateStatus(status)){
            return new ResponseResult(500,"修改角色状态,参数不合法");
        }
        LambdaUpdateWrapper<SysRole> updateWrapper = new LambdaUpdateWrapper<>();
        //设置id
        updateWrapper.eq(SysRole::getRoleId,roleId);
        //设置状态
        updateWrapper.set(SysRole::getStatus,status);
        if (sysRoleService.update(updateWrapper)){
            return new ResponseResult(200,"角色状态修改成功");
        }else {
            return new ResponseResult(500,"角色修改状态修改失败");
        }
    }

    //添加角色
    @PostMapping("/addRole")
    @PreAuthorize("@fentric.hasAuthority('system:role:add')")
    //处理list时,使用size判断多少,其他使用null
    public ResponseResult addRole(@RequestBody RoleAddOrUpdateParams roleAddOrUpdateParams){
        //添加角色应该id为null,名字为null或"",状态不为0或1  就返回失败提示
        if (roleAddOrUpdateParams.getRoleId()!=null
                || !CommonUtils.isValidateStatus(roleAddOrUpdateParams.getStatus())
                || CommonUtils.isNullOrEmptyStr(roleAddOrUpdateParams.getRoleName())){
            return new ResponseResult(500,"添加角色,参数不合法");
        }
        //1.添加角色返回,角色id(负数的话,添加失败)
        Long roleId = sysRoleService.addRole(roleAddOrUpdateParams);
        if (roleId<0){
            return new ResponseResult(500,"角色名不允许重复");
        }
        //2.解析menuIds,存入sys_role_menu
        List<Long> menuIds = roleAddOrUpdateParams.getMenuIds();
        List<SysRoleMenu> sysRoleMenus=new ArrayList<>();
        for (Long menuId : menuIds) {
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setRoleId(roleId);
            sysRoleMenu.setMenuId(menuId);
            sysRoleMenus.add(sysRoleMenu);
        }
        //有数据就插入
        if (sysRoleMenus.size()>0){
            sysRoleMenuService.saveBatch(sysRoleMenus);
        }
        return new ResponseResult(200,"添加角色成功");
    }

    //修改角色
    @PutMapping("/updateRole")
    @PreAuthorize("@fentric.hasAuthority('system:role:update')")
    //此处可以复用参数,就不定义新的了
    public ResponseResult updateRole(@RequestBody RoleAddOrUpdateParams roleAddOrUpdateParams){
        //添加角色应该id不能小于0
        if (!CommonUtils.isValidateId(roleAddOrUpdateParams.getRoleId())
                || !CommonUtils.isValidateStatus(roleAddOrUpdateParams.getStatus())
                || CommonUtils.isNullOrEmptyStr(roleAddOrUpdateParams.getRoleName())){
            return new ResponseResult(500,"修改角色,参数不合法");
        }
        //1.修改角色
        SysRole sysRole = new SysRole();
        sysRole.setRoleId(roleAddOrUpdateParams.getRoleId());
        sysRole.setRoleName(roleAddOrUpdateParams.getRoleName());
        sysRole.setStatus(roleAddOrUpdateParams.getStatus());
        sysRole.setComment(roleAddOrUpdateParams.getComment());
        //为null数据,将不会触发更修,但是updateTime会因为内容变化而更新
        sysRoleService.updateById(sysRole);

        //2.修改权限
        /**
         * 传递过来的menuIds,是前端选中的权限
         * 1.涉及到怎么把已有的权限禁用
         * 2.怎么把禁用的权限重新启用
         * 3.修改和删除怎么同时进行
         * 思路:先根据id查询role_menu,将结果集进行遍历,如果在menuIds中设置status状态为0,其他为1;
         * 并同时筛选menuIds,构建插入对象,最后同结果集合并,插入数据库
         */
        List<Long> menuIds = roleAddOrUpdateParams.getMenuIds();
        sysRoleMenuService.updateRoleMenu(menuIds,sysRole.getRoleId());
        return new ResponseResult(200,"修改角色成功");
    }
    //删除角色
    @DeleteMapping("/delRole")
    @PreAuthorize("@fentric.hasAuthority('system:role:delete')")
    public ResponseResult delRole(@RequestParam("roleIds")String roleIds){
        //不规范,在里面处理,  数据范围
        return sysRoleService.delRole(roleIds);
    }

    //根据roleId获取角色
    @GetMapping("/getRoleByRoleId")
    @PreAuthorize("@fentric.hasAuthority('system:role:query')")
    public ResponseResult getRoleByRoleId(@RequestParam("roleId") Long roleId){
        //参数不可能为null,因为有requestParam
        if (roleId<=0){
            return new ResponseResult(500,"获取角色,参数不合法");
        }
        return sysRoleService.getRoleByRoleId(roleId);
    }
}
