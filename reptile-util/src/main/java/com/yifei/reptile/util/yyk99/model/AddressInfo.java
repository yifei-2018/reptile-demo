package com.yifei.reptile.util.yyk99.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 地址信息
 *
 * @author luqs
 * @version v1.0
 * @date 2020/8/13 02:02
 */
@Data
public class AddressInfo implements Serializable {
    private static final long serialVersionUID = -2146926691311226666L;
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
