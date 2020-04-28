package com.yifei.reptile.util.yyk99.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 医院信息
 *
 * @author yifei
 * @date 2020/4/25
 */
@Data
public class HospitalInfo implements Serializable {
    private static final long serialVersionUID = -2146926691311226666L;
    /**
     * 医院名称
     */
    private String name;
    /**
     * 医院详情url
     */
    private String detailUrl;
    /**
     * 省名
     */
    private String provinceName;
    /**
     * 市（直辖市）名
     */
    private String cityName;
    /**
     * 区名
     */
    private String districtName;
    /**
     * 街道名
     */
    private String streetName;
}
