package com.fentric.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

/**
 * <p>
 * 告警表(借助缓存提高告警性能,如果和缓存中数据不同那就存入数据库,相同就不存入) 前端控制器
 * </p>
 *
 * @author zhouqi
 * @since 2022-08-09
 */
@Controller
@RequestMapping("/iotWarm")
public class IotWarmController {

}
