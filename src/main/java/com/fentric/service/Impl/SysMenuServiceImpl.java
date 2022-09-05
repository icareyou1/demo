package com.fentric.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fentric.domain.vo.RouterMenu;
import com.fentric.domain.vo.RouterMetaMenu;
import com.fentric.mapper.SysMenuMapper;
import com.fentric.pojo.LoginUser;
import com.fentric.pojo.SysMenu;
import com.fentric.service.SysMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fentric.utils.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * <p>
 * 菜单权限表 服务实现类
 * </p>
 *
 * @author zhouqi
 * @since 2022-07-22
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {
    @Autowired
    SysMenuMapper sysMenuMapper;

    @Override
    public List<RouterMenu> selectMenuByUserId(Long userId) {
        List<SysMenu> menus=null;
        //设置10010为超级管理员
        if (userId==10010L){
            menus= sysMenuMapper.selectMenuAll();
        }else {
            menus=sysMenuMapper.selectMenuByRoleId(SpringUtils.getSysUser().getRoleId());
        }
        //指定0为最顶层
        return buildMenus(menus,0L);
    }
    //将查询出来的列表树化
    private List<RouterMenu> buildMenus(List<SysMenu> sysMenus,Long parentId){
        List<RouterMenu> fatherRouters=new LinkedList<>();
        //建立map
        Map<Long, RouterMenu> map = new HashMap<>();
        List<SysMenu> sonMenus=new ArrayList<>();
        //找出父类,划分子类
        for (SysMenu sysMenu : sysMenus) {
            //如果等于parentId则为顶层
            if (sysMenu.getParentId()==parentId){
                RouterMenu routerMenu = parseSysMenuToRouterMenu(sysMenu,true);
                //返回不为空的
                if (routerMenu!=null) map.put(sysMenu.getMenuId(),routerMenu);
            }else {
                sonMenus.add(sysMenu);
            }
        }
        //遍历子类
        for (SysMenu sonMenu : sonMenus) {
            Long tempParentId = sonMenu.getParentId();
            RouterMenu tempRouterMenu = map.get(tempParentId);
            //父类在map中存在,才封装
            if (tempRouterMenu!=null){
                RouterMenu routerMenu = parseSysMenuToRouterMenu(sonMenu,false);
                if (routerMenu!=null) {
                    tempRouterMenu.getChildren().add(routerMenu);
                }
            }
        }
        //将map转为list返回
        Set<Long> keys = map.keySet();
        for (Long key : keys) {
            fatherRouters.add(map.get(key));
        }
        return fatherRouters;
    }
    //将sysMenu菜单转成前端路由菜单
    private RouterMenu parseSysMenuToRouterMenu(SysMenu sysMenu,boolean isFather){
        RouterMenu routerMenu = new RouterMenu();
        String path = sysMenu.getPath();
        //必要字段非空才进行操作
        if (!"".equals(path)&&path!=null){
            //首字母大写,封装到RouterMenu中
            String name=firstLetterToUpperCase(path);
            routerMenu.setName(name);
            if (isFather){
                //父类路径加/
                routerMenu.setPath("/"+path);
            }else {
                routerMenu.setPath(path);
            }
            routerMenu.setHidden("0".equals(sysMenu.getStatus())?false:true);
            routerMenu.setComponent(sysMenu.getComponent());
            routerMenu.setMeta(new RouterMetaMenu(sysMenu.getMenuName(),sysMenu.getIcon()));
            routerMenu.setChildren(new ArrayList<>());
            return routerMenu;
        }
        return null;
    }
    //将字符串首字母转大写
    private String firstLetterToUpperCase(String str){
        char[] chars = str.toCharArray();
        chars[0]-=32;
        return String.valueOf(chars);
    }
}
