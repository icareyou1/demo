package com.fentric.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fentric.domain.ResponseResult;
import com.fentric.domain.requestVO.UserQueryParams;
import com.fentric.domain.vo.PageUser;
import com.fentric.exception.UsernamePasswordException;
import com.fentric.exception.UsernamePasswordMissMatchException;
import com.fentric.exception.CaptchaExpireException;
import com.fentric.exception.CaptchaMissMatchException;
import com.fentric.mapper.SysOrgMapper;
import com.fentric.pojo.LoginUser;
import com.fentric.pojo.SysOrg;
import com.fentric.pojo.SysUser;
import com.fentric.mapper.SysUserMapper;
import com.fentric.service.SysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fentric.utils.CommonUtils;
import com.fentric.utils.JwtUtils;
import com.fentric.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author zhouqi
 * @since 2022-07-20
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    //springsecurity认证器
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private RedisCache redisCache;
    @Value("${redis.expireTime}")
    private int expireTime;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SysOrgMapper sysOrgMapper;

    @Override
    public ResponseResult login(SysUser sysUser,String code,String uuid) {
        //验证码处理部分,会自动捕获异常返回json,有效性只有一次
        validateCaptcha(code,uuid);
        //利用authenticationManager的authenticate进行认证
        Authentication authentication= null;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(sysUser.getUserName(),sysUser.getPassword()));
        } catch (AuthenticationException e) {
            //认证没有通过,会有异常拦截器进行处理
            if (e instanceof BadCredentialsException){
                //done 用户名或密码不正确
                throw new UsernamePasswordMissMatchException();
            }else{
                //done 认证处理中的其他异常
                throw new UsernamePasswordException("用户名和密码匹配中的其他异常,例如数据库查询异常");
            }
        }
        //认证通过,使用userid生成token返回给前端
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        //刷新redis中的用户,并且将LoginUser的userId封装进jwt中
        return refreshAndTokenLoginUser(loginUser);
    }
    /**
     *    刷新redis中的用户,并将其useId封装进jwt中
     * @param loginUser
     * @return
     */
    private ResponseResult refreshAndTokenLoginUser(LoginUser loginUser){
        String userId = loginUser.getSysUser().getUserId().toString();
        //认证后对象存入redis中
        String redisKey="login:"+userId;
        //todo 目前redis一个月
        if (expireTime>0){
            redisCache.setCacheObject(redisKey,loginUser,expireTime, TimeUnit.MINUTES);
        }else{
            //否则永久存储
            redisCache.setCacheObject(redisKey,loginUser);
        }
        //userid作为前后端发送的key
        String token = JwtUtils.createToken(userId);
        //jwt封装进responseResult中
        Map<String, String> map = new HashMap<>();
        map.put("token",token);
        return new ResponseResult(200,"登录成功",map);
    }

    /**
     * 从securityContextHolder获取id,删除redis中的缓存
     * @return
     */
    @Override
    public ResponseResult logout() {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        String userId = loginUser.getSysUser().getUserId().toString();
        //从redis中删除
        String redisKey="login:"+userId;
        redisCache.deleteObject(redisKey);
        return new ResponseResult(200,"注销成功");
    }

    @Override
    public ResponseResult getUserInfo() {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //用户id
        Long userId = loginUser.getSysUser().getUserId();
        //获取pageUser(如果这里查询不对将500)
        PageUser pageUser = null;
        pageUser = sysUserMapper.selectPageUserByUserId(userId);
        //获取角色
        String roleName = pageUser.getSysRole().getRoleName();
        //获取权限
        Set<String> permissions = loginUser.getPermissions();

        Map<String, Object> map = new HashMap<>();
        map.put("pageUser",pageUser);
        map.put("roleName",roleName);
        map.put("permissions",permissions);
        return new ResponseResult(200,"操作成功",map);
    }

    //查询用户列表
    @Override
    public ResponseResult listUser(UserQueryParams userQueryParams) {
        //1.设置查询lambda
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getDeleted,"0");
        //不查询密码字段
        //2.封装查询SysUser的参数
        if (userQueryParams.getUserName()!=null&&!"".equals(userQueryParams.getUserName())){
            queryWrapper.like(SysUser::getUserName,userQueryParams.getUserName());
        }
        if (userQueryParams.getPhoneNumber()!=null&&!"".equals(userQueryParams.getPhoneNumber())){
            queryWrapper.like(SysUser::getPhoneNumber,userQueryParams.getPhoneNumber());
        }
        if (userQueryParams.getGender()!=null&&!"".equals(userQueryParams.getGender())){
            queryWrapper.eq(SysUser::getGender,userQueryParams.getGender());
        }
        if (userQueryParams.getStatus()!=null&&!"".equals(userQueryParams.getStatus())){
            queryWrapper.eq(SysUser::getStatus,userQueryParams.getStatus());
        }
        //done 应该要能够查询出子部门
        if (userQueryParams.getOrgId()!=null&&!"".equals(userQueryParams.getOrgId())){
            //构建当前orgId的所有子部门集合
            LambdaQueryWrapper<SysOrg> wrapper = new LambdaQueryWrapper<>();
            //部门删除前,其下不能有用户   筛选部门的状态没有意义
            wrapper.eq(SysOrg::getDeleted,"0");
            List<SysOrg> sysOrgs = sysOrgMapper.selectList(wrapper);
            //筛选当前orgId及其子组织
            List<Long> orgIds = streamTreeOrgIds(sysOrgs, userQueryParams.getOrgId());
            //将本身加入
            orgIds.add(userQueryParams.getOrgId());
            System.out.println("--"+orgIds);
            //至少一个本身所以不用判空
            queryWrapper.in(SysUser::getOrgId,orgIds);
        }

        if (userQueryParams.getBeginTime()!=null&&!"".equals(userQueryParams.getBeginTime())){
            queryWrapper.ge(SysUser::getCreateTime, LocalDateTime.of(LocalDate.parse(userQueryParams.getBeginTime()), LocalTime.MIN));
        }
        if (userQueryParams.getEndTime()!=null&&!"".equals(userQueryParams.getEndTime())){
            queryWrapper.le(SysUser::getCreateTime,LocalDateTime.of(LocalDate.parse(userQueryParams.getEndTime()),LocalTime.MAX));
        }
        //设置Page
        if (userQueryParams.getPageNum()==null||userQueryParams.getPageNum()<=0){
            userQueryParams.setPageNum(1L);
        }
        if (userQueryParams.getPageSize()==null||userQueryParams.getPageSize()<=0){
            userQueryParams.setPageSize(10L);
        }
        //3.利用this.page分页查询
        Page<SysUser> page = new Page<>();
        //设置起始页
        page.setCurrent(userQueryParams.getPageNum());
        //设置每页数量
        page.setSize(userQueryParams.getPageSize());
        page=this.page(page,queryWrapper);

        //4.查询出org,封装入sysUser中
        List<SysUser> rows = page.getRecords();
        List<PageUser> pageUsers = new ArrayList<>();

        //封装org进入sysUser
        for (SysUser row : rows) {
            PageUser pageUser = new PageUser();
            //如果orgId为null,就不封装
            if (row.getOrgId()!=null&&!"".equals(row.getOrgId())){
                LambdaQueryWrapper<SysOrg> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(SysOrg::getDeleted,"0");
                wrapper.eq(SysOrg::getOrgId,row.getOrgId());
                //查询为null也可以封装
                SysOrg sysOrg = sysOrgMapper.selectOne(wrapper);
                //封装对象
                pageUser.setSysOrg(sysOrg);
            }
            pageUser.setUserId(row.getUserId());
            pageUser.setUserName(row.getUserName());
            pageUser.setNickName(row.getNickName());
            pageUser.setGender(row.getGender());
            pageUser.setPhoneNumber(row.getPhoneNumber());
            pageUser.setEmail(row.getEmail());
            pageUser.setAvatar(row.getAvatar());
            pageUser.setStatus(row.getStatus());
            pageUser.setComment(row.getComment());
            pageUser.setCreateTime(row.getCreateTime());
            pageUser.setUpdateTime(row.getUpdateTime());
            pageUser.setOrgId(row.getOrgId());
            pageUser.setRoleId(row.getRoleId());
            pageUser.setDeleted("0");
            //添加上面封装
            pageUsers.add(pageUser);
        }
        //获取总页数
        long total = page.getTotal();
        Map<String, Object> map = new HashMap<>();
        map.put("rows",pageUsers);
        map.put("total",total);

        return new ResponseResult(200,"查询用户列表成功",map);
    }

    //验证添加用户是否有效
    @Override
    public boolean validateAddUser(SysUser sysUser) {
        /**
         * 用户昵称非空,非空字符串
         * 组织null或>0
         * 手机号null或11位
         * 邮箱null或@
         * 用户名length>=2
         * 密码 length>=5
         * 用户性别: null 0 1
         * 状态 0 1
         * 角色 null 或 >0
         */
        //不同于修改字段,所以当以下字段有值出现,定位为非法参数
        if (sysUser.getUserId()!=null||
                sysUser.getCreateTime()!=null||
                sysUser.getUpdateTime()!=null||
                sysUser.getAvatar()!=null||
                sysUser.getDeleted()!=null){
            return false;
        }
        //如果是空或空串返回false
        if (CommonUtils.isNullOrEmptyStr(sysUser.getNickName())){
            return false;
        }
        //邮箱验证,可以为null
        if (sysUser.getEmail()!=null&&!checkEmail(sysUser.getEmail())){
            return false;
        }
        //手机验证,可以为null
        if (sysUser.getPhoneNumber()!=null&&!checkPhone(sysUser.getPhoneNumber())){
            return false;
        }
        //用户名大于2
        if (sysUser.getUserName()!=null&&sysUser.getUserName().length()<2){
            return false;
        }
        //密码大于4
        if (sysUser.getPassword()!=null&&sysUser.getPassword().length()<4){
            return false;
        }
        //性别  可以使用这函数
        if (sysUser.getGender()!=null&&!CommonUtils.isValidateStatus(sysUser.getGender())){
            return false;
        }
        //状态
        if (!CommonUtils.isValidateStatus(sysUser.getStatus())){
            return false;
        }
        //组织id<=0
        if (sysUser.getOrgId()!=null&&sysUser.getOrgId()<=0) return false;
        //角色为null 或要大于0
        if (sysUser.getRoleId()!=null&&sysUser.getRoleId()<=0){
            return false;
        }
        return true;
    }
    //修改用户校验
    @Override
    public boolean validateUpdateUser(SysUser sysUser) {
        //初始化一些无关参数(mybatis-plus策略忽略null)
        //此处这样做是因为,因为前端获取值的时候,可能会有这些值的获取
        sysUser.setUpdateTime(null);
        sysUser.setCreateTime(null);
        sysUser.setAvatar(null);
        sysUser.setPassword(null);
        sysUser.setDeleted(null);
        sysUser.setUserName(null);
        /*if (sysUser.getCreateTime()!=null||
                sysUser.getUpdateTime()!=null||
                sysUser.getAvatar()!=null||
                sysUser.getPassword()!=null||
                sysUser.getDeleted()!=null||
                sysUser.getUserName()!=null){
            return false;
        }*/
        //校验userIdsysUser
        if (!CommonUtils.isValidateId(sysUser.getUserId())){
            return false;
        }
        //校验nickName
        if (CommonUtils.isNullOrEmptyStr(sysUser.getNickName())){
            return false;
        }
        //邮箱验证,可以为null
        if (sysUser.getEmail()!=null&&!checkEmail(sysUser.getEmail())){
            return false;
        }
        //手机验证,可以为null
        if (sysUser.getPhoneNumber()!=null&&!checkPhone(sysUser.getPhoneNumber())){
            return false;
        }
        //性别  可以使用这函数
        if (sysUser.getGender()!=null&&!CommonUtils.isValidateStatus(sysUser.getGender())){
            return false;
        }
        //状态
        if (!CommonUtils.isValidateStatus(sysUser.getStatus())){
            return false;
        }
        //组织id<=0
        if (sysUser.getOrgId()!=null&&sysUser.getOrgId()<=0) return false;
        //角色为null 或要大于0
        if (sysUser.getRoleId()!=null&&sysUser.getRoleId()<=0){
            return false;
        }
        return true;
    }

    /**
     * 验证校验码
     * @param code
     * @param uuid
     */
    private void validateCaptcha(String code,String uuid){
        //验证码校验,捕获验证码异常后会自动返回前端
        //验证码只能使用一次
        String redisKeyCode="code:"+uuid;
        String captcha=redisCache.getCacheObject(redisKeyCode);
        redisCache.deleteObject(redisKeyCode);
        /**
         * 验证码抛出异常,能给前端返回json的原因是
         * 定义了全局的异常捕获器,能够将捕获到的异常进行封装,然后返回给前端
         */
        //抛出验证码过期异常
        if (captcha==null){
            throw new CaptchaExpireException();
        }
        //抛出验证码错误异常
        if(!code.equalsIgnoreCase(captcha)){
            throw new CaptchaMissMatchException();
        }
    }

    //找出部门及子部门id集合
    private List<Long> streamTreeOrgIds(List<SysOrg> sysOrgs,Long parentId){
        List<ArrayList<Long>> collects = sysOrgs.stream()
                .filter(item -> {
                    return Objects.equals(item.getParentId(), parentId);
                }).map(item -> {
                    ArrayList<Long> orgIds = new ArrayList<>();
                    orgIds.add(item.getOrgId());
                    orgIds.addAll(streamTreeOrgIds(sysOrgs, item.getOrgId()));
                    return orgIds;
                }).collect(Collectors.toList());
        ArrayList<Long> orgIds = new ArrayList<>();
        for (ArrayList<Long> collect : collects) {
            orgIds.addAll(collect);
        }
        return orgIds;
    }

    //验证手机号
    public static boolean checkPhone(String phone){
        //以1开头,中间两个可匹配 最后八个数字
        Pattern p = Pattern.compile("^1(3\\d|4[5-9]|5[0-35-9]|6[2567]|7[0-8]|8\\d|9[0-35-9])\\d{8}$");
        if(p.matcher(phone).matches()){
            return true;
        }
        return false;
    }
    //验证邮箱
    public static boolean checkEmail(String email) {
        String regEx1 = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern p;
        Matcher m;
        p = Pattern.compile(regEx1);
        m = p.matcher(email);
        if (m.matches()){
            return true;
        }
        return false;
    }
}
