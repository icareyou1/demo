package com.fentric.service;

import com.fentric.domain.vo.CategorySelectShow;
import com.fentric.pojo.IotCategory;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 设备类型表(参数部分为了拓展性,尽量走协议格式) 服务类
 * </p>
 *
 * @author zhouqi
 * @since 2022-09-20
 */
public interface IotCategoryService extends IService<IotCategory> {
    //搜索栏部分的设备类型下拉菜单
    List<CategorySelectShow> getDeviceCategoryForSearch();
}
