package com.fentric.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 设备类型表(参数部分为了拓展性,尽量走协议格式)
 * </p>
 *
 * @author zhouqi
 * @since 2022-09-20
 */
@TableName("iot_category")
public class IotCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 设备类型id
     */
    @TableId(value = "category_id", type = IdType.AUTO)
    private Long categoryId;

    /**
     * 设备类型名(设备读取信息)
     */
    private String categoryName;

    /**
     * 设备模型型号(10字节)
     */
    private String categoryModuleName;

    /**
     * 软件版本(0-999)
     */
    private Short categorySoftewareVersion;

    /**
     * 文档地址
     */
    private String categoryDocumentUrl;

    /**
     * 从站地址号
     */
    private Short d2000;

    /**
     * 波特率 0:2400 1:4800 2:9600
     */
    private Short d2001;

    /**
     * 工作模式:0本地监测,1报警联动
     */
    private Short d2002;

    /**
     * 0三相四线1三相三线2单相
     */
    private Short d2003;

    private Short d2004;

    private Short d2005;

    private Short d2006;

    private Short d2007;

    private Short d2008;

    private Short d2009;

    private Short d2011;

    private Short d2012;

    private Short d2013;

    private Short d2014;

    private Short d2015;

    private Short d2016;

    private Short d2017;

    private Short d2018;

    private Short d2019;

    private Short d2020;

    private Short d2021;

    private Short d2022;

    private Short d2023;

    private Short d2024;

    private Short d2025;

    private Short d2026;

    private Short d2027;

    private Short d2028;

    private Short d2029;

    private Short d2030;

    private Short d2031;

    private Short d2032;

    /**
     * 事件编号(0-65535)
     */
    private Short d2080;

    private String status;

    private String deleted;

    /**
     * 备注
     */
    private String comment;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryModuleName() {
        return categoryModuleName;
    }

    public void setCategoryModuleName(String categoryModuleName) {
        this.categoryModuleName = categoryModuleName;
    }

    public Short getCategorySoftewareVersion() {
        return categorySoftewareVersion;
    }

    public void setCategorySoftewareVersion(Short categorySoftewareVersion) {
        this.categorySoftewareVersion = categorySoftewareVersion;
    }

    public String getCategoryDocumentUrl() {
        return categoryDocumentUrl;
    }

    public void setCategoryDocumentUrl(String categoryDocumentUrl) {
        this.categoryDocumentUrl = categoryDocumentUrl;
    }

    public Short getd2000() {
        return d2000;
    }

    public void setd2000(Short d2000) {
        this.d2000 = d2000;
    }

    public Short getd2001() {
        return d2001;
    }

    public void setd2001(Short d2001) {
        this.d2001 = d2001;
    }

    public Short getd2002() {
        return d2002;
    }

    public void setd2002(Short d2002) {
        this.d2002 = d2002;
    }

    public Short getd2003() {
        return d2003;
    }

    public void setd2003(Short d2003) {
        this.d2003 = d2003;
    }

    public Short getd2004() {
        return d2004;
    }

    public void setd2004(Short d2004) {
        this.d2004 = d2004;
    }

    public Short getd2005() {
        return d2005;
    }

    public void setd2005(Short d2005) {
        this.d2005 = d2005;
    }

    public Short getd2006() {
        return d2006;
    }

    public void setd2006(Short d2006) {
        this.d2006 = d2006;
    }

    public Short getd2007() {
        return d2007;
    }

    public void setd2007(Short d2007) {
        this.d2007 = d2007;
    }

    public Short getd2008() {
        return d2008;
    }

    public void setd2008(Short d2008) {
        this.d2008 = d2008;
    }

    public Short getd2009() {
        return d2009;
    }

    public void setd2009(Short d2009) {
        this.d2009 = d2009;
    }

    public Short getd2011() {
        return d2011;
    }

    public void setd2011(Short d2011) {
        this.d2011 = d2011;
    }

    public Short getd2012() {
        return d2012;
    }

    public void setd2012(Short d2012) {
        this.d2012 = d2012;
    }

    public Short getd2013() {
        return d2013;
    }

    public void setd2013(Short d2013) {
        this.d2013 = d2013;
    }

    public Short getd2014() {
        return d2014;
    }

    public void setd2014(Short d2014) {
        this.d2014 = d2014;
    }

    public Short getd2015() {
        return d2015;
    }

    public void setd2015(Short d2015) {
        this.d2015 = d2015;
    }

    public Short getd2016() {
        return d2016;
    }

    public void setd2016(Short d2016) {
        this.d2016 = d2016;
    }

    public Short getd2017() {
        return d2017;
    }

    public void setd2017(Short d2017) {
        this.d2017 = d2017;
    }

    public Short getd2018() {
        return d2018;
    }

    public void setd2018(Short d2018) {
        this.d2018 = d2018;
    }

    public Short getd2019() {
        return d2019;
    }

    public void setd2019(Short d2019) {
        this.d2019 = d2019;
    }

    public Short getd2020() {
        return d2020;
    }

    public void setd2020(Short d2020) {
        this.d2020 = d2020;
    }

    public Short getd2021() {
        return d2021;
    }

    public void setd2021(Short d2021) {
        this.d2021 = d2021;
    }

    public Short getd2022() {
        return d2022;
    }

    public void setd2022(Short d2022) {
        this.d2022 = d2022;
    }

    public Short getd2023() {
        return d2023;
    }

    public void setd2023(Short d2023) {
        this.d2023 = d2023;
    }

    public Short getd2024() {
        return d2024;
    }

    public void setd2024(Short d2024) {
        this.d2024 = d2024;
    }

    public Short getd2025() {
        return d2025;
    }

    public void setd2025(Short d2025) {
        this.d2025 = d2025;
    }

    public Short getd2026() {
        return d2026;
    }

    public void setd2026(Short d2026) {
        this.d2026 = d2026;
    }

    public Short getd2027() {
        return d2027;
    }

    public void setd2027(Short d2027) {
        this.d2027 = d2027;
    }

    public Short getd2028() {
        return d2028;
    }

    public void setd2028(Short d2028) {
        this.d2028 = d2028;
    }

    public Short getd2029() {
        return d2029;
    }

    public void setd2029(Short d2029) {
        this.d2029 = d2029;
    }

    public Short getd2030() {
        return d2030;
    }

    public void setd2030(Short d2030) {
        this.d2030 = d2030;
    }

    public Short getd2031() {
        return d2031;
    }

    public void setd2031(Short d2031) {
        this.d2031 = d2031;
    }

    public Short getd2032() {
        return d2032;
    }

    public void setd2032(Short d2032) {
        this.d2032 = d2032;
    }

    public Short getd2080() {
        return d2080;
    }

    public void setd2080(Short d2080) {
        this.d2080 = d2080;
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
        return "IotCategory{" +
        "categoryId = " + categoryId +
        ", categoryName = " + categoryName +
        ", categoryModuleName = " + categoryModuleName +
        ", categorySoftewareVersion = " + categorySoftewareVersion +
        ", categoryDocumentUrl = " + categoryDocumentUrl +
        ", d2000 = " + d2000 +
        ", d2001 = " + d2001 +
        ", d2002 = " + d2002 +
        ", d2003 = " + d2003 +
        ", d2004 = " + d2004 +
        ", d2005 = " + d2005 +
        ", d2006 = " + d2006 +
        ", d2007 = " + d2007 +
        ", d2008 = " + d2008 +
        ", d2009 = " + d2009 +
        ", d2011 = " + d2011 +
        ", d2012 = " + d2012 +
        ", d2013 = " + d2013 +
        ", d2014 = " + d2014 +
        ", d2015 = " + d2015 +
        ", d2016 = " + d2016 +
        ", d2017 = " + d2017 +
        ", d2018 = " + d2018 +
        ", d2019 = " + d2019 +
        ", d2020 = " + d2020 +
        ", d2021 = " + d2021 +
        ", d2022 = " + d2022 +
        ", d2023 = " + d2023 +
        ", d2024 = " + d2024 +
        ", d2025 = " + d2025 +
        ", d2026 = " + d2026 +
        ", d2027 = " + d2027 +
        ", d2028 = " + d2028 +
        ", d2029 = " + d2029 +
        ", d2030 = " + d2030 +
        ", d2031 = " + d2031 +
        ", d2032 = " + d2032 +
        ", d2080 = " + d2080 +
        ", status = " + status +
        ", deleted = " + deleted +
        ", comment = " + comment +
        ", createTime = " + createTime +
        ", updateTime = " + updateTime +
        "}";
    }
}
