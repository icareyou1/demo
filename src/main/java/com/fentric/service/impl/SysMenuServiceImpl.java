package com.fentric.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fentric.domain.ResponseResult;
import com.fentric.domain.vo.RouterMenu;
import com.fentric.domain.vo.RouterMetaMenu;
import com.fentric.domain.vo.TreeSelectMenu;
import com.fentric.mapper.SysMenuMapper;
import com.fentric.mapper.SysRoleMenuMapper;
import com.fentric.pojo.SysMenu;
import com.fentric.pojo.SysRoleMenu;
import com.fentric.service.SysMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fentric.utils.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    SysRoleMenuMapper sysRoleMenuMapper;

    @Override
    public List<RouterMenu> selectMenuByUserId(Long userId) {
        List<SysMenu> menus=null;
        //todo 设置10010为超级管理员,还有一处为   10010LUserDetailsServiceImpl查询权限,  sysMenuMapper.xml中有两处3表示为菜单
        //此处会自动进行拆箱,userId.valueOf()     10010L为基本类型,所以存在自动拆箱
        if (userId==10010L){
            //menus 会查询出所有数据,包括第三层的,但是只做前两层处理即可
            menus= sysMenuMapper.selectMenuAll();
        }else {
            menus=sysMenuMapper.selectMenuByRoleId(SpringUtils.getSysUser().getRoleId());
        }
        //指定0为最顶层   只处理前面两层即可
        return buildMenus(menus,0L);
    }

    //将查询出来的列表树化
    //"data":    [{"name":"Monitor","path":"/monitor","hidden":false,"component":"","meta":{"name":"设备监测","icon":"view"},"children":[]},....]
    private List<RouterMenu> buildMenus(List<SysMenu> sysMenus,Long parentId){
        List<RouterMenu> fatherRouters=new LinkedList<>();
        //建立map
        Map<Long, RouterMenu> map = new HashMap<>();
        List<SysMenu> sonMenus=new ArrayList<>();
        //找出父类,划分子类
        for (SysMenu sysMenu : sysMenus) {
            //如果等于parentId则为顶层   引用类型的比较不能使用==,除非-128-127
            if (Objects.equals(sysMenu.getParentId(),parentId)){
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
    //将sysMenu菜单转成前端路由菜单(配合buildMenus使用)
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

    //给前端返回树形结构
    @Override
    public ResponseResult treeSelectByUserId(Long userId) {
        //1.根据userId查询所有menu
        List<SysMenu> menus=null;
        if (10010L == userId){
            menus= sysMenuMapper.selectMenuAll();
        }else {
            menus=sysMenuMapper.selectMenuByRoleId(SpringUtils.getSysUser().getRoleId());
        }

        //2.返回的数据类型为:"data":[{"id":2000,"label":"设备管理","children":[{"id":2049,"label":"通用物模型","children":[{"id":2050,"label":"通用物模型查询"}....
        //返回数组,  树化函数应该处理三层
        return new ResponseResult<>(200,"角色添加按钮树形菜单操作成功",buildRoleTreeSelectMenus(menus,0L));
    }

    //根据角色id生成修改角色时的回显菜单
    //menus和checkedKeys
    @Override
    public ResponseResult updateRoleShowTreeSelect(Long roleId) {
        //根据登录的userId获取菜单
        Long userId = SpringUtils.getSysUser().getUserId();
        List<SysMenu> menus=null;
        if (10010L == userId){
            menus= sysMenuMapper.selectMenuAll();
        }else {
            menus=sysMenuMapper.selectMenuByRoleId(SpringUtils.getSysUser().getRoleId());
        }
        //修改角色时所用菜单
        List<TreeSelectMenu> roleMenus = buildRoleTreeSelectMenus(menus, 0L);
        //然后根据roleId查询id,也就是具有的权限
        LambdaQueryWrapper<SysRoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(SysRoleMenu::getMenuId);
        queryWrapper.eq(SysRoleMenu::getDeleted,"0");
        queryWrapper.eq(SysRoleMenu::getStatus,"0");
        queryWrapper.eq(SysRoleMenu::getRoleId,roleId);
        List<SysRoleMenu> sysRoleMenus = sysRoleMenuMapper.selectList(queryWrapper);
        //角色所具有的菜单
        List<Long> checkedKeys = new ArrayList<>();
        for (SysRoleMenu sysRoleMenu : sysRoleMenus) {
            Long menuId = sysRoleMenu.getMenuId();
            checkedKeys.add(menuId);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("roleMenus",roleMenus);
        map.put("checkedKeys",checkedKeys);
        return new ResponseResult(200,"修改角色回显菜单查询成功",map);
    }


    //将查询出来的SysMenu树化成  "data":[{"id":2000,"label":"设备管理","children":[{"id":2049,"label":"通用物模型","children":[{"id":2050,"label":"通用物模型查询"}....
    //多一个parentId,前端不使用即可
    private List<TreeSelectMenu> buildRoleTreeSelectMenus(List<SysMenu> sysMenus,Long parentId){
        //建立父类map
        Map<Long, TreeSelectMenu> fatherMap = new HashMap<>();
        //建立子类map
        Map<Long, TreeSelectMenu> sonMap = new HashMap<>();

        //首先找出父类
        Iterator<SysMenu> findFatherByIterator = sysMenus.iterator();

        //第一次遍历,找出父类
        while (findFatherByIterator.hasNext()) {
            SysMenu next = findFatherByIterator.next();
            //如果id为指定id则为父类
            if (Objects.equals(next.getParentId(),parentId)){
                TreeSelectMenu treeSelectMenu = new TreeSelectMenu();
                treeSelectMenu.setId(next.getMenuId());
                treeSelectMenu.setFatherId(0L);
                treeSelectMenu.setLabel(next.getMenuName());
                treeSelectMenu.setChildren(new ArrayList<>());
                //将父类放入map中
                fatherMap.put(next.getMenuId(),treeSelectMenu);
                //将父类从遍历队列踢出
                findFatherByIterator.remove();
            }
        }

        //第二次遍历,再次找出子类
        Iterator<SysMenu> findSonByIterator = sysMenus.iterator();
        while (findSonByIterator.hasNext()) {
            SysMenu next = findSonByIterator.next();
            //如果当前id能从map中找到,则为子类
            TreeSelectMenu tempTreeSelectMenu = fatherMap.get(next.getParentId());
            if (tempTreeSelectMenu!=null){
                TreeSelectMenu treeSelectMenu = new TreeSelectMenu();
                treeSelectMenu.setId(next.getMenuId());
                treeSelectMenu.setFatherId(next.getParentId());
                treeSelectMenu.setLabel(next.getMenuName());
                treeSelectMenu.setChildren(new ArrayList<>());
                //将子类放入map中
                sonMap.put(next.getMenuId(),treeSelectMenu);
                //将子类踢出
                findSonByIterator.remove();
            }
        }

        //第三次遍历,找出孙类封装进子类
        Iterator<SysMenu> findGrandSonByIterator = sysMenus.iterator();
        while (findGrandSonByIterator.hasNext()) {
            SysMenu next = findGrandSonByIterator.next();
            //如果当前id能从sonMap中找到,则为孙类
            TreeSelectMenu tempTreeSelectMenu = sonMap.get(next.getParentId());
            if (tempTreeSelectMenu!=null){
                TreeSelectMenu treeSelectMenu = new TreeSelectMenu();
                treeSelectMenu.setId(next.getMenuId());
                treeSelectMenu.setFatherId(next.getParentId());
                treeSelectMenu.setLabel(next.getMenuName());
                treeSelectMenu.setChildren(new ArrayList<>());
                tempTreeSelectMenu.getChildren().add(treeSelectMenu);
            }
        }
        //将子类map装入父类map
        Set<Long> sonKeys = sonMap.keySet();
        for (Long sonKey : sonKeys) {
            //获取子类
            TreeSelectMenu sonTreeSelectMenu = sonMap.get(sonKey);
            //获取子类的父id
            Long fatherId = sonTreeSelectMenu.getFatherId();

            //根据子类父id获取到父类
            TreeSelectMenu treeSelectMenu = fatherMap.get(fatherId);
            //将子类封装
            treeSelectMenu.getChildren().add(sonTreeSelectMenu);
        }
        //将父类map转换成list
        return fatherMap.values().stream().collect(Collectors.toList());

    }
}
