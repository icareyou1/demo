package com.fentric.mapper;

import com.fentric.pojo.SysOrg;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 组织部门表(与用户表1对1) Mapper 接口
 * </p>
 *
 * @author zhouqi
 * @since 2022-09-01
 */
public interface SysOrgMapper extends BaseMapper<SysOrg> {
    void deleteOrgByOrgId(@Param("orgId") Long orgId);
}
