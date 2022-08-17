package com.fentric.modbus;

import com.fentric.domain.Modbus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class FentricModule {
    protected Modbus modbus;
    public abstract void handleWarmModule();
    public abstract void handleRunModule();
    public abstract void handldEventModule();
    //获取告警部分数据
    protected String getWarmData(){
        return modbus.getData().substring(0,8);
    }
    //获取运行数据
    protected String getRunData(){
        return modbus.getData().substring(8,modbus.getData().length()-4);
    }
    //获取事件id
    protected String getEventIdData(){
        return modbus.getData().substring(modbus.getData().length()-4);
    }
}
