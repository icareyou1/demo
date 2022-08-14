package com.fentric.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 设备运行状态表(需要定时器定时采集)
 * </p>
 *
 * @author zhouqi
 * @since 2022-08-05
 */
@TableName("iot_run")
public class IotRun implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 运行状态id
     */
    @TableId(value = "run_id", type = IdType.AUTO)
    private Long runId;

    /**
     * 设备id
     */
    private Long deviceId;

    /**
     * 数据格式为: 16位
     */
    private String d1022;

    private Integer d1030;

    private Integer d1031;

    private Integer d1032;

    private Integer d1033;

    private Integer d1034;

    private Integer d1035;

    private Integer d1036;

    private Integer d1037;

    private Integer d1038;

    private Integer d1039;

    private Integer d1040;

    private Integer d1041;

    private Integer d1042;

    private Integer d1043;

    private Integer d1044;

    private Integer d1045;

    private Integer d1046;

    private Integer d1047;

    private Integer d1048;

    private Integer d1049;

    private Integer d1050;

    private Integer d1051;

    private Integer d1052;

    private Integer d1053;

    private Integer d1054;

    private Integer d1055;

    private Integer d1056;

    private Integer d1057;

    private Integer d1058;

    private Integer d1059;

    private Integer d1060;

    private Integer d1061;

    private Integer d1062;

    private Integer d1063;

    private Integer d1064;

    private Integer d1065;

    private Integer d1066;

    private Integer d1067;

    private Integer d1068;

    private Integer d1069;

    private Integer d1070;

    private Integer d1071;

    private Integer d1072;

    private Integer d1073;

    private Integer d1074;

    private Integer d1075;

    private Integer d1076;

    private Integer d1077;

    private Integer d1078;

    private Integer d1079;

    private String status;

    private String deleted;

    /**
     * 备注
     */
    private String comment;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    public Long getRunId() {
        return runId;
    }

    public void setRunId(Long runId) {
        this.runId = runId;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public String getd1022() {
        return d1022;
    }

    public void setd1022(String d1002) {
        this.d1022 = d1002;
    }

    public Integer getd1030() {
        return d1030;
    }

    public void setd1030(Integer d1030) {
        this.d1030 = d1030;
    }

    public Integer getd1031() {
        return d1031;
    }

    public void setd1031(Integer d1031) {
        this.d1031 = d1031;
    }

    public Integer getd1032() {
        return d1032;
    }

    public void setd1032(Integer d1032) {
        this.d1032 = d1032;
    }

    public Integer getd1033() {
        return d1033;
    }

    public void setd1033(Integer d1033) {
        this.d1033 = d1033;
    }

    public Integer getd1034() {
        return d1034;
    }

    public void setd1034(Integer d1034) {
        this.d1034 = d1034;
    }

    public Integer getd1035() {
        return d1035;
    }

    public void setd1035(Integer d1035) {
        this.d1035 = d1035;
    }

    public Integer getd1036() {
        return d1036;
    }

    public void setd1036(Integer d1036) {
        this.d1036 = d1036;
    }

    public Integer getd1037() {
        return d1037;
    }

    public void setd1037(Integer d1037) {
        this.d1037 = d1037;
    }

    public Integer getd1038() {
        return d1038;
    }

    public void setd1038(Integer d1038) {
        this.d1038 = d1038;
    }

    public Integer getd1039() {
        return d1039;
    }

    public void setd1039(Integer d1039) {
        this.d1039 = d1039;
    }

    public Integer getd1040() {
        return d1040;
    }

    public void setd1040(Integer d1040) {
        this.d1040 = d1040;
    }

    public Integer getd1041() {
        return d1041;
    }

    public void setd1041(Integer d1041) {
        this.d1041 = d1041;
    }

    public Integer getd1042() {
        return d1042;
    }

    public void setd1042(Integer d1042) {
        this.d1042 = d1042;
    }

    public Integer getd1043() {
        return d1043;
    }

    public void setd1043(Integer d1043) {
        this.d1043 = d1043;
    }

    public Integer getd1044() {
        return d1044;
    }

    public void setd1044(Integer d1044) {
        this.d1044 = d1044;
    }

    public Integer getd1045() {
        return d1045;
    }

    public void setd1045(Integer d1045) {
        this.d1045 = d1045;
    }

    public Integer getd1046() {
        return d1046;
    }

    public void setd1046(Integer d1046) {
        this.d1046 = d1046;
    }

    public Integer getd1047() {
        return d1047;
    }

    public void setd1047(Integer d1047) {
        this.d1047 = d1047;
    }

    public Integer getd1048() {
        return d1048;
    }

    public void setd1048(Integer d1048) {
        this.d1048 = d1048;
    }

    public Integer getd1049() {
        return d1049;
    }

    public void setd1049(Integer d1049) {
        this.d1049 = d1049;
    }

    public Integer getd1050() {
        return d1050;
    }

    public void setd1050(Integer d1050) {
        this.d1050 = d1050;
    }

    public Integer getd1051() {
        return d1051;
    }

    public void setd1051(Integer d1051) {
        this.d1051 = d1051;
    }

    public Integer getd1052() {
        return d1052;
    }

    public void setd1052(Integer d1052) {
        this.d1052 = d1052;
    }

    public Integer getd1053() {
        return d1053;
    }

    public void setd1053(Integer d1053) {
        this.d1053 = d1053;
    }

    public Integer getd1054() {
        return d1054;
    }

    public void setd1054(Integer d1054) {
        this.d1054 = d1054;
    }

    public Integer getd1055() {
        return d1055;
    }

    public void setd1055(Integer d1055) {
        this.d1055 = d1055;
    }

    public Integer getd1056() {
        return d1056;
    }

    public void setd1056(Integer d1056) {
        this.d1056 = d1056;
    }

    public Integer getd1057() {
        return d1057;
    }

    public void setd1057(Integer d1057) {
        this.d1057 = d1057;
    }

    public Integer getd1058() {
        return d1058;
    }

    public void setd1058(Integer d1058) {
        this.d1058 = d1058;
    }

    public Integer getd1059() {
        return d1059;
    }

    public void setd1059(Integer d1059) {
        this.d1059 = d1059;
    }

    public Integer getd1060() {
        return d1060;
    }

    public void setd1060(Integer d1060) {
        this.d1060 = d1060;
    }

    public Integer getd1061() {
        return d1061;
    }

    public void setd1061(Integer d1061) {
        this.d1061 = d1061;
    }

    public Integer getd1062() {
        return d1062;
    }

    public void setd1062(Integer d1062) {
        this.d1062 = d1062;
    }

    public Integer getd1063() {
        return d1063;
    }

    public void setd1063(Integer d1063) {
        this.d1063 = d1063;
    }

    public Integer getd1064() {
        return d1064;
    }

    public void setd1064(Integer d1064) {
        this.d1064 = d1064;
    }

    public Integer getd1065() {
        return d1065;
    }

    public void setd1065(Integer d1065) {
        this.d1065 = d1065;
    }

    public Integer getd1066() {
        return d1066;
    }

    public void setd1066(Integer d1066) {
        this.d1066 = d1066;
    }

    public Integer getd1067() {
        return d1067;
    }

    public void setd1067(Integer d1067) {
        this.d1067 = d1067;
    }

    public Integer getd1068() {
        return d1068;
    }

    public void setd1068(Integer d1068) {
        this.d1068 = d1068;
    }

    public Integer getd1069() {
        return d1069;
    }

    public void setd1069(Integer d1069) {
        this.d1069 = d1069;
    }

    public Integer getd1070() {
        return d1070;
    }

    public void setd1070(Integer d1070) {
        this.d1070 = d1070;
    }

    public Integer getd1071() {
        return d1071;
    }

    public void setd1071(Integer d1071) {
        this.d1071 = d1071;
    }

    public Integer getd1072() {
        return d1072;
    }

    public void setd1072(Integer d1072) {
        this.d1072 = d1072;
    }

    public Integer getd1073() {
        return d1073;
    }

    public void setd1073(Integer d1073) {
        this.d1073 = d1073;
    }

    public Integer getd1074() {
        return d1074;
    }

    public void setd1074(Integer d1074) {
        this.d1074 = d1074;
    }

    public Integer getd1075() {
        return d1075;
    }

    public void setd1075(Integer d1075) {
        this.d1075 = d1075;
    }

    public Integer getd1076() {
        return d1076;
    }

    public void setd1076(Integer d1076) {
        this.d1076 = d1076;
    }

    public Integer getd1077() {
        return d1077;
    }

    public void setd1077(Integer d1077) {
        this.d1077 = d1077;
    }

    public Integer getd1078() {
        return d1078;
    }

    public void setd1078(Integer d1078) {
        this.d1078 = d1078;
    }

    public Integer getd1079() {
        return d1079;
    }

    public void setd1079(Integer d1079) {
        this.d1079 = d1079;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "IotRun{" +
        "runId = " + runId +
        ", deviceId = " + deviceId +
        ", d1002 = " + d1022 +
        ", d1030 = " + d1030 +
        ", d1031 = " + d1031 +
        ", d1032 = " + d1032 +
        ", d1033 = " + d1033 +
        ", d1034 = " + d1034 +
        ", d1035 = " + d1035 +
        ", d1036 = " + d1036 +
        ", d1037 = " + d1037 +
        ", d1038 = " + d1038 +
        ", d1039 = " + d1039 +
        ", d1040 = " + d1040 +
        ", d1041 = " + d1041 +
        ", d1042 = " + d1042 +
        ", d1043 = " + d1043 +
        ", d1044 = " + d1044 +
        ", d1045 = " + d1045 +
        ", d1046 = " + d1046 +
        ", d1047 = " + d1047 +
        ", d1048 = " + d1048 +
        ", d1049 = " + d1049 +
        ", d1050 = " + d1050 +
        ", d1051 = " + d1051 +
        ", d1052 = " + d1052 +
        ", d1053 = " + d1053 +
        ", d1054 = " + d1054 +
        ", d1055 = " + d1055 +
        ", d1056 = " + d1056 +
        ", d1057 = " + d1057 +
        ", d1058 = " + d1058 +
        ", d1059 = " + d1059 +
        ", d1060 = " + d1060 +
        ", d1061 = " + d1061 +
        ", d1062 = " + d1062 +
        ", d1063 = " + d1063 +
        ", d1064 = " + d1064 +
        ", d1065 = " + d1065 +
        ", d1066 = " + d1066 +
        ", d1067 = " + d1067 +
        ", d1068 = " + d1068 +
        ", d1069 = " + d1069 +
        ", d1070 = " + d1070 +
        ", d1071 = " + d1071 +
        ", d1072 = " + d1072 +
        ", d1073 = " + d1073 +
        ", d1074 = " + d1074 +
        ", d1075 = " + d1075 +
        ", d1076 = " + d1076 +
        ", d1077 = " + d1077 +
        ", d1078 = " + d1078 +
        ", d1079 = " + d1079 +
        ", status = " + status +
        ", deleted = " + deleted +
        ", comment = " + comment +
        ", createTime = " + createTime +
        ", updateTime = " + updateTime +
        "}";
    }
}
