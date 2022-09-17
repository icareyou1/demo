package com.fentric.controller;

import com.fentric.domain.ResponseResult;
import com.fentric.service.SysMenuService;
import com.fentric.utils.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 菜单权限表 前端控制器
 * </p>
 *
 * @author zhouqi
 * @since 2022-07-22
 */
@RestController
@RequestMapping("/sysMenu")
public class SysMenuController {
    @Autowired
    SysMenuService sysMenuService;

    //获取菜单下拉树列表(返回什么结果的数据)
    @GetMapping("/treeSelect")
    public ResponseResult treeSelect(){
        //1.根据userId查询所有menu
        //2.返回的数据类型为:"data":[{"id":2000,"label":"设备管理","children":[{"id":2049,"label":"通用物模型","children":[{"id":2050,"label":"通用物模型查询"}....
        //此处多了parentId
        return sysMenuService.treeSelectByUserId(SpringUtils.getSysUser().getUserId());
    }

    //获取修改角色时,回显的数据
    @GetMapping("/updateRoleShowTreeSelect")
    public ResponseResult updateRoleShowTreeSelect(@RequestParam("roleId") Long roleId){
        return sysMenuService.updateRoleShowTreeSelect(roleId);
    }
}
