package com.fentric.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonitorDeviceTable {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long deviceId;
    private String deviceName;
    private String tagIds;
}
