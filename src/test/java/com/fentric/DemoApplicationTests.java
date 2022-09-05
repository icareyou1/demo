package com.fentric;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fentric.domain.vo.PageUser;
import com.fentric.mapper.SysMenuMapper;
import com.fentric.mapper.SysUserMapper;
import com.fentric.pojo.SysMenu;
import com.fentric.pojo.SysRole;
import com.fentric.pojo.SysUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Field;
import java.util.List;

@SpringBootTest
class DemoApplicationTests {
    //@Autowired
    //SysRoleMapper sysRoleMapper;
    @Autowired
    SysUserMapper sysUserMapper;
    @Autowired
    SysMenuMapper sysMenuMapper;
    @Test
    void sqlGenerator(){
        Class aClass = SysMenu.class;
        String prefix="";

        StringBuilder sb = new StringBuilder();
        for (Field field : aClass.getDeclaredFields()) {
            String name = field.getName();
            if ("serialVersionUID".equals(name)) continue;
            for (int i = 0; i < name.length(); i++) {
                //如果是大写,就在前面加  _
                if (Character.isUpperCase(name.charAt(i))){
                    name=name.substring(0, i)+"_"+name.substring(i).toLowerCase();
                    break;
                }
            }
            sb.append(prefix+name+",");
        }
        System.out.println(sb);
    }

    @Test
    void contextLoads() {
        List<SysMenu> sysMenus = sysMenuMapper.selectMenuAll();
        for (SysMenu sysMenu : sysMenus) {
            System.out.println(sysMenu);
            System.out.println("0".equals(sysMenu.getStatus())? false : true);
        }
    }

}
