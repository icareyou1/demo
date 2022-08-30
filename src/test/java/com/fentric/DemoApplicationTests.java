package com.fentric;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fentric.mapper.SysMenuMapper;
import com.fentric.mapper.SysRoleMapper;
import com.fentric.mapper.SysUserMapper;
import com.fentric.pojo.LoginUser;
import com.fentric.pojo.SysRole;
import com.fentric.pojo.SysUser;
import com.fentric.utils.JwtUtils;
import com.google.code.kaptcha.Producer;
import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.base.Captcha;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

@SpringBootTest
class DemoApplicationTests {
    @Autowired
    SysRoleMapper sysRoleMapper;
    @Autowired
    SysUserMapper sysUserMapper;
    @Autowired
    SysMenuMapper sysMenuMapper;
    @Autowired
    Captcha captcha;

    @Test
    void contextLoads() {
        QueryWrapper<SysRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("role_id","role_name");
        queryWrapper.eq("role_id",1);
        queryWrapper.or();
        queryWrapper.eq("role_id",5);
        List<SysRole> list = sysRoleMapper.selectList(queryWrapper);
        System.out.println(list);
    }
    @Test
    public void testSelectList(){
        QueryWrapper<SysRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(SysRole.class, new Predicate<TableFieldInfo>() {
            @Override
            public boolean test(TableFieldInfo tableFieldInfo) {
                //不查询role_id字段
                return !"role_name".equals(tableFieldInfo.getColumn())&&!"role_key".equals(tableFieldInfo.getColumn());
            }
        });
        //lambda
        //queryWrapper.select(SysRole.class, (tableFieldInfo)-> !"role_key".equals(tableFieldInfo.getColumn()));
        List<SysRole> sysRoles = sysRoleMapper.selectList(queryWrapper);
        System.out.println(sysRoles);
    }
    @Test
    public void lambdaTest(){
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRole::getRoleId,1);
        SysRole sysRole = sysRoleMapper.findSysRoleById(queryWrapper);
        System.out.println(sysRole);
    }
    @Test
    public void paginationTest(){
        LambdaQueryWrapper<SysRole> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRole::getStatus,"0");
        IPage page=new Page();
        //设置每页
        page.setSize(2);
        //查询哪一页
        page.setCurrent(2);
        sysRoleMapper.selectPage(page, queryWrapper);
        System.out.println(page.getRecords());
    }
    /*@Test
    public void test() throws IOException {
        String text = producer.createText();
        BufferedImage image = producer.createImage(text);
        File file = new File("image.jpg");
        ImageIO.write(image,"jpg",file);

    }*/
    @Test
    public void captcha() throws FileNotFoundException {
        //获取运算公式
        ArithmeticCaptcha captcha = (ArithmeticCaptcha) this.captcha;
        String arithmeticString = captcha.getArithmeticString();
        //运算结果
        String text = captcha.text();
        System.out.println(arithmeticString);
        System.out.println(text);
        File file = new File("image.jpg");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        captcha.out(fileOutputStream);
        System.out.println(captcha.toBase64());
    }
    @Test
    public void test1(){
        System.out.println(JwtUtils.parseToken("eyJhbGciOiJIUzI1NiJ9.eyJsb2dpblVzZXJfa2V5IjoiMTAwMTAiLCJleHAiOjE2NjQxOTE4Njh9.vPiz-fHomII3NRiu6rhvWN0GqyHK4WiKzEwhk6D56d8"));
    }

}
