package com.fentric.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fentric.domain.vo.CategorySelectShow;
import com.fentric.pojo.IotCategory;
import com.fentric.mapper.IotCategoryMapper;
import com.fentric.service.IotCategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 设备类型表(参数部分为了拓展性,尽量走协议格式) 服务实现类
 * </p>
 *
 * @author zhouqi
 * @since 2022-09-20
 */
@Service
public class IotCategoryServiceImpl extends ServiceImpl<IotCategoryMapper, IotCategory> implements IotCategoryService {

    //搜索部分的设备类型下拉菜单
    @Override
    public List<CategorySelectShow> getDeviceCategoryForSearch() {
        //返回categoryId和categoryName
        LambdaQueryWrapper<IotCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(IotCategory::getDeleted,"0");
        queryWrapper.eq(IotCategory::getStatus,"0");
        //查询非空的类型名字
        queryWrapper.isNotNull(IotCategory::getCategoryName);
        queryWrapper.select(IotCategory::getCategoryId,IotCategory::getCategoryName);
        List<IotCategory> list = this.list(queryWrapper);
        ArrayList<CategorySelectShow> categorySelectShows = new ArrayList<>();
        list.forEach(item->{
            //如果为空遍历下一个
            CategorySelectShow categorySelectShow = new CategorySelectShow(item.getCategoryId(), item.getCategoryName());
            categorySelectShows.add(categorySelectShow);
        });
        return categorySelectShows;
    }
}
