package com.fentric.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fentric.domain.DeviceStatus;
import com.fentric.domain.ResponseResult;
import com.fentric.domain.requestVO.DeviceQueryParams;
import com.fentric.mapper.IotCategoryMapper;
import com.fentric.mapper.IotTagMapper;
import com.fentric.modbus.DeviceDataPool;
import com.fentric.pojo.*;
import com.fentric.mapper.IotDeviceMapper;
import com.fentric.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fentric.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 设备信息表 服务实现类
 * </p>
 *
 * @author zhouqi
 * @since 2022-08-08
 */
@Service
public class IotDeviceServiceImpl extends ServiceImpl<IotDeviceMapper, IotDevice> implements IotDeviceService {
    @Autowired
    IotDeviceMapper iotDeviceMapper;
    @Autowired
    IotWarmService iotWarmService;
    @Autowired
    IotEventService iotEventService;
    @Autowired
    IotOperService iotOperService;
    @Autowired
    IotDeviceTagService iotDeviceTagService;
    @Autowired
    IotCategoryMapper iotCategoryMapper;
    @Autowired
    IotTagMapper iotTagMapper;

    @Override
    public int[] queryDeviceCategoryByReceiveGateWayId(Long receiveGatewayId) {
        LambdaQueryWrapper<IotDevice> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(IotDevice::getDeviceId);
        wrapper.select(IotDevice::getCategoryId);
        wrapper.ge(IotDevice::getDeviceId,receiveGatewayId);
        wrapper.le(IotDevice::getDeviceId,receiveGatewayId+32);
        List<IotDevice> iotDevices = iotDeviceMapper.selectList(wrapper);
        //如果查询出来的设备为null
        if (iotDevices==null){
            return null;
        }
        int[] temp=new int[iotDevices.size()];
        for (int i = 0; i < iotDevices.size(); i++) {
            IotDevice iotDevice = iotDevices.get(i);
            if (iotDevice.getCategoryId()!=null){
                temp[i]= Math.toIntExact(iotDevice.getCategoryId());
            }else {//如果设备类型为null则初始化为0
                temp[i]=0;
            }
        }
        return temp;
    }

    /**
     *   返回给首页设备状态
     *       在线设备 离线设备
     *       今日告警 历史告警
     *       今日事件 历史事件
     *       今日操作 历史操作
     * @return
     */
    @Override
    public ResponseResult getDeviceStatistic() {
        //总设备
        Integer totalDeviceCount= Math.toIntExact(this.count());
        //在线设备
        Integer onlineDeviceCount=0;
        //离线设备
        Integer offlineDeviceCount=0;
        //今日告警
        Integer todayWarmCount=0;
        //历史告警
        Integer totalWarmCount=0;
        //今日事件
        Integer todayEventCount=0;
        //历史事件
        Integer totalEventCount=0;
        //今日操作
        Integer todayOperationCount=0;
        //历史操作
        Integer totalOperationCount=0;

        //1.查询在线情况
        Set<Long> gateWayIds = DeviceDataPool.DeviceStatusMap.keySet();
        if (gateWayIds!=null){
            for (Long gateWayId : gateWayIds) {
                DeviceStatus deviceStatus = DeviceDataPool.DeviceStatusMap.get(gateWayId);
                int[] online = deviceStatus.getOnline();
                //当网关不在线时,直接判定网关和子设备掉线
                if (online[0]!=1){
                    continue;
                }
                for (int i = 0; i < online.length; i++) {
                    //其他0或者2表示不在线(使用绝对值可以更加实时)
                    if (Math.abs(online[i])==1){
                        onlineDeviceCount++;
                    }
                }
            }
        }
        //2.离线设备
        offlineDeviceCount=totalDeviceCount-onlineDeviceCount;
        //3.今日告警数量
        LocalDateTime today_start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime today_end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        /*DateTimeFormatter time = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String todayStart=time.format(today_start);
        String todayEnd = time.format(today_end);*/
        LambdaQueryWrapper<IotWarm> iotWarmQueryWrapper = new LambdaQueryWrapper<>();
        iotWarmQueryWrapper.ge(IotWarm::getCreateTime,today_start);
        iotWarmQueryWrapper.le(IotWarm::getCreateTime,today_end);
        todayWarmCount= Math.toIntExact(iotWarmService.count(iotWarmQueryWrapper));
        //4.历史告警
        totalWarmCount = Math.toIntExact(iotWarmService.count());
        //5.今日事件
        LambdaQueryWrapper<IotEvent> iotEventLambdaQueryWrapper = new LambdaQueryWrapper<>();
        iotEventLambdaQueryWrapper.ge(IotEvent::getCreateTime,today_start);
        iotEventLambdaQueryWrapper.le(IotEvent::getCreateTime,today_end);
        todayEventCount= Math.toIntExact(iotEventService.count(iotEventLambdaQueryWrapper));
        //6.历史事件
        totalEventCount= Math.toIntExact(iotEventService.count());
        //7.今日操作
        LambdaQueryWrapper<IotOper> iotOperLambdaQueryWrapper = new LambdaQueryWrapper<>();
        iotOperLambdaQueryWrapper.ge(IotOper::getCreateTime,today_start);
        iotOperLambdaQueryWrapper.le(IotOper::getCreateTime,today_end);
        todayOperationCount= Math.toIntExact(iotOperService.count(iotOperLambdaQueryWrapper));
        //8.历史操作
        totalOperationCount= Math.toIntExact(iotOperService.count());
        //9.封装数据
        Map<String, Object> map = new HashMap<>();
        map.put("totalDeviceCount",totalDeviceCount);
        map.put("onlineDeviceCount",onlineDeviceCount);
        map.put("offlineDeviceCount",offlineDeviceCount);
        map.put("todayWarmCount",todayWarmCount);
        map.put("totalWarmCount",totalWarmCount);
        map.put("todayEventCount",todayEventCount);
        map.put("totalEventCount",totalEventCount);
        map.put("todayOperationCount",todayOperationCount);
        map.put("totalOperationCount",totalOperationCount);
        return new ResponseResult(200,"查询设备统计成功",map);
    }

    //获取搜索栏设备IP下拉菜单
    @Override
    public Set<String> getDeviceIpForSearch() {
        //从device表获取,查询状态和是否删除
        LambdaQueryWrapper<IotDevice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(IotDevice::getDeleted,"0");
        queryWrapper.eq(IotDevice::getStatus,"0");
        //此处只查询一个,不加此句当有deviceIp为null时会导致封装失败
        queryWrapper.isNotNull(IotDevice::getDeviceIp);
        queryWrapper.select(IotDevice::getDeviceIp);
        List<IotDevice> list = this.list(queryWrapper);
        Set<String> strings = new HashSet<>();
        list.forEach(item->{
            strings.add(item.getDeviceIp());
        });
        return strings;
    }

    //返回设备列表
    @Override
    public ResponseResult listDevice(DeviceQueryParams deviceQueryParams) {
        /**
         * pageNum   设置默认为1
         * pageSize   设置默认为10
         *
         * tagIds 先查询标签下的设备Id然后查询这个集合中数据
         */
        //查询未被删除的,所有状态数据
        LambdaQueryWrapper<IotDevice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(IotDevice::getDeleted,"0");
        //在前端处处,选中再取消,会让选项变为""
        if (!CommonUtils.isNullOrEmptyStr(deviceQueryParams.getDeviceName())){
            queryWrapper.like(IotDevice::getDeviceName,deviceQueryParams.getDeviceName());
        }
        if(!CommonUtils.isNullOrEmptyStr(deviceQueryParams.getDeviceAddress())){
            queryWrapper.like(IotDevice::getDeviceAddress,deviceQueryParams.getDeviceAddress());
        }
        if (!CommonUtils.isNullOrEmptyStr(deviceQueryParams.getDeviceIp())){
            queryWrapper.eq(IotDevice::getDeviceIp,deviceQueryParams.getDeviceIp());
        }
        if (!CommonUtils.isNullOrEmptyStr(deviceQueryParams.getStatus())){
            queryWrapper.eq(IotDevice::getStatus,deviceQueryParams.getStatus());
        }
        //设备类型id号(不需要判定有效性)
        if (!CommonUtils.isNullOrEmptyStr(deviceQueryParams.getCategoryId())){
            queryWrapper.eq(IotDevice::getCategoryId,deviceQueryParams.getCategoryId());
        }
        /*---------------------处理tagIds---------------------------*/
        //处理tagids,会自动进行转换(前端传递为"",机器端可能为null)
        if (deviceQueryParams.getTagIds()!=null&&deviceQueryParams.getTagIds().size()>0){
            List<Long> deviceIds = new ArrayList<>();
            List<Long> tagIds = deviceQueryParams.getTagIds();
            LambdaQueryWrapper<IotDeviceTag> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(IotDeviceTag::getDeleted,"0");
            wrapper.eq(IotDeviceTag::getStatus,"0");
            wrapper.in(IotDeviceTag::getTagId,tagIds);
            wrapper.select(IotDeviceTag::getDeviceId);
            //查询不会返回不会为null,[]
            List<IotDeviceTag> list = iotDeviceTagService.list(wrapper);
            list.forEach(item->{
                deviceIds.add(item.getDeviceId());
            });
            //如果没有查询到,说明标签下没有设备,直接查询即可
            if (deviceIds.size()>0){
                queryWrapper.in(IotDevice::getDeviceId,deviceIds);
            }else {
                Map<String, Object> map = new HashMap<>();
                map.put("rows",null);
                map.put("total",0);
                return new ResponseResult(200,"获取设备列表成功",map);
            }
        }
        /*------------------------------------------------*/
        //done 传入字符串不对???
        try {
            if (!CommonUtils.isNullOrEmptyStr(deviceQueryParams.getBeginTime())){
                queryWrapper.ge(IotDevice::getCreateTime,LocalDateTime.of(LocalDate.parse(deviceQueryParams.getBeginTime()),LocalTime.MIN));
            }
            if (!CommonUtils.isNullOrEmptyStr(deviceQueryParams.getEndTime())){
                queryWrapper.le(IotDevice::getCreateTime,LocalDateTime.of(LocalDate.parse(deviceQueryParams.getEndTime()),LocalTime.MAX));
            }
        } catch (Exception e) {
            return new ResponseResult(500,"日期格式出错");
        }
        //设置Page
        if (deviceQueryParams.getPageNum()==null||deviceQueryParams.getPageNum()<=0){
            deviceQueryParams.setPageNum(1L);
        }
        if (deviceQueryParams.getPageSize()==null||deviceQueryParams.getPageSize()<=0){
            deviceQueryParams.setPageSize(10L);
        }
        //3.利用this.page分页查询
        Page<IotDevice> page = new Page<>();
        //设置起始页
        page.setCurrent(deviceQueryParams.getPageNum());
        //设置每页数量
        page.setSize(deviceQueryParams.getPageSize());
        page=this.page(page,queryWrapper);
        //获取查询数据
        List<IotDevice> rows = page.getRecords();
        Map<String, Object> map = new HashMap<>();
        map.put("rows",rows);
        map.put("total",page.getTotal());
        //前端最多接受18位Long数字,这里会失真  解决方案:转为String
        return new ResponseResult(200,"获取设备列表成功",map);
    }

    //校验添加设备的时候,参数是否合法
    @Override
    public boolean validateAddDevice(IotDevice iotDevice) {
        /**
         *
         * 1.deviceId  首先应该为合法id,   如果是网关Id,不应该有重复id, 如果是子设备,应该能查询到网关id,并且本次id不重复
         * 2.devicName  不为空和空串
         * 3.deviceAddress 直接插入即可
         * 4.ip地址合法即可
         * 5.合法status
         * 6.comment  直接插入即可
         * 7.类别  首先id应该合法  在category查询到才准许差入
         * 8.tagIds  首先进行解析,失败则参数不合法    能从标签表查询到同等数量则成功
         * 9.其余为null
         */
        if (!CommonUtils.isValidateId(iotDevice.getDeviceId())||
                CommonUtils.isNullOrEmptyStr(iotDevice.getDeviceName())||
                !CommonUtils.isValidateStatus(iotDevice.getStatus())||
                !CommonUtils.checkIp(iotDevice.getDeviceIp())||
                !CommonUtils.everyoneIsNull(iotDevice.getDeviceImage(),
                        iotDevice.getDeviceActiveTime(),
                        iotDevice.getDeleted(),
                        iotDevice.getCreateTime(),
                        iotDevice.getUpdateTime(),
                        iotDevice.getUserId()
                )
        ) return false;

        //当id合法后,判断内容
        Long deviceId = iotDevice.getDeviceId();
        if (deviceId%100>32) return false;
        //查询区间所有设备信息
        LambdaQueryWrapper<IotDevice> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(IotDevice::getDeviceId,deviceId/100*100);
        wrapper.le(IotDevice::getDeviceId,deviceId/100*100+32);
        //设备表不允许删除设备,只能停用(所以不要设置deleted)
        List<IotDevice> iotDevices = iotDeviceMapper.selectList(wrapper);
        //判断查询出来的列表是否有网关
        boolean hasGateWay=false;
        boolean hasDevice=false;
        for (IotDevice device : iotDevices) {
            //有网关设备
            if (device.getDeviceId()%100==0) hasGateWay=true;
            if (device.getDeviceId()==deviceId) hasDevice=true;
        }
        //添加设备为网关,且网关已经存在
        if (deviceId%100==0&&hasGateWay){
            return false;
        }
        //添加设备为子设备情况
        if (deviceId%100!=0){
            //没有网关,直接false
            if (!hasGateWay) return false;
            //有网关,且id重复返回false
            if (hasGateWay&&hasDevice) return false;
        }

        //判断类别
        if (!CommonUtils.isValidateId(iotDevice.getCategoryId())){
            return false;
        }else {
            //要求categoryId没有删除和禁用
            LambdaQueryWrapper<IotCategory> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(IotCategory::getDeleted,"0");
            queryWrapper.eq(IotCategory::getStatus,"0");
            queryWrapper.eq(IotCategory::getCategoryId,iotDevice.getCategoryId());
            IotCategory iotCategory = iotCategoryMapper.selectOne(queryWrapper);
            if (iotCategory==null) return false;
        }
        //判断tagIds
        try {
            //""会解析出null   "{}"报错   "[]"不为null
            List<Long> tagIds = JSON.parseArray(iotDevice.getTagIds(),Long.class);
            //如果id找不到返回false
            if (tagIds!=null&&tagIds.size()>0){
                LambdaQueryWrapper<IotTag> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(IotTag::getDeleted,"0");
                queryWrapper.eq(IotTag::getStatus,"0");
                queryWrapper.in(IotTag::getTagId,tagIds);
                List<IotTag> iotTags = iotTagMapper.selectList(queryWrapper);
                //查询出来的数量不一致就返回false,可以不用管deleted或status
                if (iotTags==null||iotTags.size()!=tagIds.size()) return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    //好像和增加一样的逻辑就行
    @Override
    public boolean validateUpdateDevice(IotDevice iotDevice) {
        //先进行预处理,因为是修改
        iotDevice.setDeviceImage(null);
        iotDevice.setDeviceActiveTime(null);
        iotDevice.setDeleted(null);
        iotDevice.setCreateTime(null);
        iotDevice.setUpdateTime(null);
        iotDevice.setUserId(null);
        //正式检查
        if (!CommonUtils.isValidateId(iotDevice.getDeviceId())||
                CommonUtils.isNullOrEmptyStr(iotDevice.getDeviceName())||
                !CommonUtils.isValidateStatus(iotDevice.getStatus())||
                !CommonUtils.checkIp(iotDevice.getDeviceIp())||
                !CommonUtils.everyoneIsNull(iotDevice.getDeviceImage(),
                        iotDevice.getDeviceActiveTime(),
                        iotDevice.getDeleted(),
                        iotDevice.getCreateTime(),
                        iotDevice.getUpdateTime(),
                        iotDevice.getUserId()
                )
        ) return false;
        //详细的id判断交给更新即可

        //判断类别
        if (!CommonUtils.isValidateId(iotDevice.getCategoryId())){
            return false;
        }else {
            //要求categoryId没有删除和禁用
            LambdaQueryWrapper<IotCategory> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(IotCategory::getDeleted,"0");
            queryWrapper.eq(IotCategory::getStatus,"0");
            queryWrapper.eq(IotCategory::getCategoryId,iotDevice.getCategoryId());
            IotCategory iotCategory = iotCategoryMapper.selectOne(queryWrapper);
            if (iotCategory==null) return false;
        }
        //判断tagIds
        try {
            //""会解析出null   "{}"报错   "[]"不为null
            List<Long> tagIds = JSON.parseArray(iotDevice.getTagIds(),Long.class);
            //如果id找不到返回false
            if (tagIds!=null&&tagIds.size()>0){
                LambdaQueryWrapper<IotTag> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(IotTag::getDeleted,"0");
                queryWrapper.eq(IotTag::getStatus,"0");
                queryWrapper.in(IotTag::getTagId,tagIds);
                List<IotTag> iotTags = iotTagMapper.selectList(queryWrapper);
                //查询出来的数量不一致就返回false,可以不用管deleted或status
                if (iotTags==null||iotTags.size()!=tagIds.size()) return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    //新增设备
    @Transactional
    @Override
    public void addDevice(IotDevice iotDevice) {
        if (iotDevice.getTagIds()==null||"".equals(iotDevice.getTagIds())) iotDevice.setTagIds("[]");
        iotDeviceMapper.insert(iotDevice);
        insertDeviceTagByStringAndDeviceId(iotDevice.getTagIds(),iotDevice.getDeviceId());
    }

    @Transactional
    @Override
    public void delDeviceByDeviceIds(List<Long> list) {
        //删除用户
        iotDeviceMapper.deleteBatchIds(list);
        //删除标签表
        LambdaQueryWrapper<IotDeviceTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(IotDeviceTag::getDeviceId,list);
        iotDeviceTagService.remove(queryWrapper);
    }

    //修改设备,对标签处理会复杂一点
    @Transactional
    @Override
    public void updateDevice(IotDevice iotDevice) {
        if (iotDevice.getTagIds()==null||"".equals(iotDevice.getTagIds())) iotDevice.setTagIds("[]");
        System.out.println("@@"+iotDevice.getTagIds().length());
        iotDeviceMapper.updateById(iotDevice);
        updateDeviceTagByStringAndDeviceId(iotDevice.getTagIds(),iotDevice.getDeviceId());
    }

    //私有方法,供新增设备和修改设备使用
    private void insertDeviceTagByStringAndDeviceId(String tagIds,Long deviceId){
        //经过了参数检查不会失败
        List<Long> ids = JSON.parseArray(tagIds, Long.class);
        //批量插入的集合
        List<IotDeviceTag> iotDeviceTags = new ArrayList<>();
        for (Long id : ids) {
            //构建
            IotDeviceTag iotDeviceTag = new IotDeviceTag();
            iotDeviceTag.setDeviceId(deviceId);
            iotDeviceTag.setTagId(id);
            iotDeviceTags.add(iotDeviceTag);
        }
        iotDeviceTagService.saveBatch(iotDeviceTags);

    }
    //done 私有方法,处理修改标签问题
    private void updateDeviceTagByStringAndDeviceId(String ids,Long deviceId){
        //获取当前要存入数据库中的数据ID
        List<Long> tagIds = JSON.parseArray(ids, Long.class);
        /**
         * 获取当前设备所有标签
         */
        LambdaQueryWrapper<IotDeviceTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(IotDeviceTag::getDeleted,"0");
        //状态01都查询
        queryWrapper.eq(IotDeviceTag::getDeviceId,deviceId);
        //最后还是要插入回去
        List<IotDeviceTag> list = iotDeviceTagService.list(queryWrapper);
        //先获取数据库中存的id
        List<Long> collect = list.stream().map(item -> {
            return item.getTagId();
        }).collect(Collectors.toList());
        //禁用所有标签
        for (IotDeviceTag iotDeviceTag : list) {
            iotDeviceTag.setStatus("1");
            iotDeviceTag.setDeleted("1");
        }

        //遍历即将插入的
        if (tagIds!=null){
            for (Long tagId : tagIds) {
                //如果里面有
                if (collect.contains(tagId)) {
                    //就启用
                    //禁用所有标签
                    for (IotDeviceTag iotDeviceTag : list) {
                        if (tagId==iotDeviceTag.getTagId()){
                            iotDeviceTag.setStatus("0");
                            iotDeviceTag.setDeleted("0");
                        }
                    }
                }else {
                    //新建一个插入集合
                    IotDeviceTag iotDeviceTag = new IotDeviceTag();
                    iotDeviceTag.setDeviceId(deviceId);
                    iotDeviceTag.setTagId(tagId);
                    //插入集合
                    list.add(iotDeviceTag);
                }
            }
        }
        iotDeviceTagService.saveOrUpdateBatch(list);
    }
}
