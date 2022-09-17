package com.fentric.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fentric.pojo.SysRoleMenu;
import com.fentric.mapper.SysRoleMenuMapper;
import com.fentric.service.SysRoleMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
    @Autowired
    SysRoleMenuMapper sysRoleMenuMapper;

    /**
     * 传递过来的menuIds,是前端选中的权限
     * 1.涉及到怎么把已有的权限禁用
     * 2.怎么把禁用的权限重新启用
     * 3.修改和删除怎么同时进行
     * 思路:先将所有权限进行禁用,然后批量启用权限
     */
    @Override
    //todo 测试,更改menuid会不会发生变化,以及Long ,long等类型,数组
    public void updateRoleMenu(List<Long> menuIds,Long roleId) {
        /**
         * 1.先根据roleId查询所有,
         * 2.根据menuIds进行匹配遍历(menuIds能从结果集找到,就置为status=0;  不能找到就禁用;  如果结果集中没有就新建)
         * 3.最后插入或者更新(如何编写条件sql)
         */
        LambdaQueryWrapper<SysRoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRoleMenu::getDeleted,"0")
                        .eq(SysRoleMenu::getRoleId,roleId);
        //查询出来的结果
        List<SysRoleMenu> sysRoleMenus = sysRoleMenuMapper.selectList(queryWrapper);
        for (SysRoleMenu sysRoleMenu : sysRoleMenus) {
            //临时menuId
            Long menuId = sysRoleMenu.getMenuId();
            //如果包含就将status置为0
            if (menuIds.contains(menuId)) {
                sysRoleMenu.setStatus("0");
                //移除已经处理的menuid
                menuIds.remove(menuId);
            }else {
                sysRoleMenu.setStatus("1");
            }
        }
        //此时的menuIds已经发生变化
        for (Long menuId : menuIds) {
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            //角色菜单id
            sysRoleMenu.setRoleId(roleId);
            sysRoleMenu.setMenuId(menuId);
            sysRoleMenu.setDeleted("0");
            sysRoleMenu.setStatus("0");
            sysRoleMenus.add(sysRoleMenu);
        }
        //剩下的sysRoleMenus则为需要插入或者更新的数据
        if (sysRoleMenus.size()>0){
            //根据主键来判断修改或者更新
            //如果是添加会先根据role_menu_id来查询,然后再更新
            this.saveOrUpdateBatch(sysRoleMenus);
        }
    }
}
