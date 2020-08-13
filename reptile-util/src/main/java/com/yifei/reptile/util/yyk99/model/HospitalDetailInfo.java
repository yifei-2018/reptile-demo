package com.yifei.reptile.util.yyk99.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 医院详情信息
 *
 * @author luqs
 * @version v1.0
 * @date 2020/8/13 00:48
 */
@Data
public class HospitalDetailInfo implements Serializable {
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
     * 别名
     */
    private String alias;
    /**
     * 等级
     */
    private String grade;
    /**
     * 性质
     */
    private String nature;
    /**
     * 电话
     */
    private String tel;
    /**
     * 地址
     */
    private String address;
    /**
     * 备注
     */
    private String remark;
    /**
     * 地址信息
     */
    private AddressInfo addressInfo;
}
