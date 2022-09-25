package com.fentric.domain.requestVO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceQueryParams {
    private Long pageNum;
    private Long pageSize;
    private String deviceName;
    private String deviceAddress;
    private String deviceIp;
    private String status;
    private List<Long> tagIds;
    private Long categoryId;
    //装载时间
    private String beginTime;
    private String endTime;
}
