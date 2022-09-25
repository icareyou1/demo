package com.fentric.service;

import com.fentric.domain.ResponseResult;
import com.fentric.domain.requestVO.TagQueryParams;
import com.fentric.domain.vo.TagShow;
import com.fentric.pojo.IotTag;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 标签表(与设备表多对多) 服务类
 * </p>
 *
 * @author zhouqi
 * @since 2022-09-18
 */
public interface IotTagService extends IService<IotTag> {
    //查询标签列表
    ResponseResult listTag(TagQueryParams tagQueryParams);
    //校验添加标签参数
    boolean validateAddTag(IotTag iotTag);
    //修改标签参数是否合法
    boolean validateUpdateTag(IotTag iotTag);
    //获取设备管理左侧标签栏
    List<TagShow> getTagForTree();
}
