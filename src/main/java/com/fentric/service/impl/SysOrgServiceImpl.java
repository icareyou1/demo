package com.fentric.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fentric.domain.ResponseResult;
import com.fentric.domain.vo.TreeSelectOrg;
import com.fentric.mapper.SysUserMapper;
import com.fentric.pojo.SysOrg;
import com.fentric.mapper.SysOrgMapper;
import com.fentric.pojo.SysUser;
import com.fentric.service.SysOrgService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 组织部门表(与用户表1对1) 服务实现类
 * </p>
 *
 * @author zhouqi
 * @since 2022-09-01
 */
@Service
public class SysOrgServiceImpl extends ServiceImpl<SysOrgMapper, SysOrg> implements SysOrgService {
    @Autowired
    SysOrgMapper sysOrgMapper;
    @Autowired
    SysUserMapper sysUserMapper;

    //查询组织表
    @Override
    public ResponseResult listOrg(String orgName, String status) {
        //如果传递的为null怎么半?
        LambdaQueryWrapper<SysOrg> queryWrapper = new LambdaQueryWrapper<>();
        //查询不是删除的数据
        queryWrapper.eq(SysOrg::getDeleted,"0");
        //done status不应该进行限制
        if (orgName!=null&&!"".equals(orgName)){
            queryWrapper.like(SysOrg::getOrgName,orgName);
        }
        if (status!=null&&!"".equals(status)){
            queryWrapper.eq(SysOrg::getStatus,status);
        }
        List<SysOrg> sysOrgs = sysOrgMapper.selectList(queryWrapper);
        return new ResponseResult(200,"组织表查询成功",sysOrgs);
    }

    //添加组织表
    @Override
    public ResponseResult addOrg(SysOrg sysOrg) {
        //1.根据组织名和父id 判断此次是否唯一
        LambdaQueryWrapper<SysOrg> queryWrapper = new LambdaQueryWrapper<>();
        //查询不是删除的数据
        queryWrapper.eq(SysOrg::getDeleted,"0");
        queryWrapper.eq(SysOrg::getParentId,sysOrg.getParentId());
        queryWrapper.eq(SysOrg::getOrgName,sysOrg.getOrgName());
        //添加组织只要确保不同即可
        //queryWrapper.ne(SysOrg::getOrgId,sysOrg.getOrgId());
        SysOrg temp = sysOrgMapper.selectOne(queryWrapper);
        //有相同得存在
        if (temp!=null){
            return new ResponseResult(500,"组织项已存在");
        }
        sysOrgMapper.insert(sysOrg);
        return new ResponseResult(200,"添加组织表成功");
    }

    //修改组织表
    @Override
    public ResponseResult updateOrg(SysOrg sysOrg) {
        //修改组织不能为已经存在的
        LambdaQueryWrapper<SysOrg> queryWrapper = new LambdaQueryWrapper<>();
        //查询没有删除的数据
        queryWrapper.eq(SysOrg::getDeleted,"0");
        queryWrapper.eq(SysOrg::getParentId,sysOrg.getParentId());
        queryWrapper.eq(SysOrg::getOrgName,sysOrg.getOrgName());
        //除了本身
        queryWrapper.ne(SysOrg::getOrgId,sysOrg.getOrgId());
        SysOrg temp = sysOrgMapper.selectOne(queryWrapper);
        //有相同得存在
        if (temp!=null){
            return new ResponseResult(500,"组织项已存在");
        }
        //父id和子id不能相同
        if (sysOrg.getOrgId().equals(sysOrg.getParentId())){
            return new ResponseResult(500,"父id不能和子id一样");
        }
        //
        return new ResponseResult(200,"修改组织表成功",sysOrgMapper.updateById(sysOrg));
    }

    //删除组织表
    @Override
    public ResponseResult deleteOrgByOrgId(Long orgId) {
        //1.有下级部门不能删除
        LambdaQueryWrapper<SysOrg> sysOrgQueryWrapper = new LambdaQueryWrapper<>();
        sysOrgQueryWrapper.eq(SysOrg::getDeleted,"0");
        sysOrgQueryWrapper.eq(SysOrg::getParentId,orgId);
        List<SysOrg> sysOrgs = sysOrgMapper.selectList(sysOrgQueryWrapper);
        //list里面有数据就不能删除
        if (sysOrgs.size()>0){
            return new ResponseResult(500,"删除失败,存在下级部门");
        }
        //2.部门有用户不能删除
        LambdaQueryWrapper<SysUser> sysUserQueryWrapper = new LambdaQueryWrapper<>();
        sysUserQueryWrapper.eq(SysUser::getDeleted,"0");
        sysUserQueryWrapper.eq(SysUser::getOrgId,orgId);
        List<SysUser> sysUsers = sysUserMapper.selectList(sysUserQueryWrapper);
        //
        if (sysUsers.size()>0){
            return new ResponseResult(500,"删除失败,部门下有用户");
        }
        //3.改变标志deleted为1
        sysOrgMapper.deleteOrgByOrgId(orgId);
        return new ResponseResult(200,"删除成功");
    }

    //根据组织id查询组织
    @Override
    public ResponseResult getOrgByOrgId(Long orgId) {
        LambdaQueryWrapper<SysOrg> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysOrg::getDeleted,"0");
        queryWrapper.eq(SysOrg::getOrgId,orgId);
        return new ResponseResult(200,"根据组织id查询组织成功",sysOrgMapper.selectOne(queryWrapper));
    }

    //根据id查询父组织(只要踢出自己和子类即可)
    @Override
    public ResponseResult listOrgExcludeChild(Long orgId) {
        LambdaQueryWrapper<SysOrg> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysOrg::getDeleted,"0");
        //查询出所有组织
        List<SysOrg> sysOrgs = sysOrgMapper.selectList(queryWrapper);
        //临时id
        List<Long> tempids=new ArrayList<>();
        tempids.add(orgId);
        //如果没有扫描到就返回
        while (true){
            //是否返回标志
            boolean isReturn=true;
            Iterator<SysOrg> iterator = sysOrgs.iterator();
            while (iterator.hasNext()){
                SysOrg next = iterator.next();
                //如果是本身直接踢出
                if (tempids.contains(next.getOrgId())){
                    iterator.remove();
                    isReturn=false;
                }
                //如果父类id在集合中,就踢出并将此id加入集合
                if (tempids.contains(next.getParentId())){
                    tempids.add(next.getOrgId());
                    iterator.remove();
                    isReturn=false;
                }
            }
            if (isReturn){
                return new ResponseResult(200,"根据id筛选自身及子类成功",sysOrgs);
            }
        }
    }
    //查询树形组织列表  (data,label,children)
    @Override
    public ResponseResult orgTreeSelect() {
        LambdaQueryWrapper<SysOrg> queryWrapper = new LambdaQueryWrapper<>();
        //其他地方使用的时候,就要查询启用状态
        queryWrapper.eq(SysOrg::getDeleted,"0")
                        .eq(SysOrg::getStatus,"0");
        List<SysOrg> sysOrgs = sysOrgMapper.selectList(queryWrapper);
        //开始进行树化
        return new ResponseResult(200,"查询树形组织列表成功",streamToTree(sysOrgs, 0L));
    }
    //利用递归解决
    private List<TreeSelectOrg> streamToTree(List<SysOrg> treeList, Long parentId){
        return treeList.stream()
                //返回符合条件的
                .filter(item->{
                    return Objects.equals(item.getParentId(),parentId);
                }).map(item->{
                    TreeSelectOrg treeSelectOrg = new TreeSelectOrg();
                    treeSelectOrg.setOrgId(item.getOrgId());
                    treeSelectOrg.setOrgName(item.getOrgName());
                    treeSelectOrg.setParentId(item.getParentId());
                    treeSelectOrg.setChildren(streamToTree(treeList,item.getOrgId()));
                    return treeSelectOrg;
                }).collect(Collectors.toList());
    }
}
