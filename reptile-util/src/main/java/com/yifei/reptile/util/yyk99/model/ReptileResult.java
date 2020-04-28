package com.yifei.reptile.util.yyk99.model;

import com.yifei.reptile.util.yyk99.constant.ReptileConstant;
import lombok.Data;

/**
 * 爬虫结果
 *
 * @author yifei
 * @date 2020/4/25
 */
@Data
public class ReptileResult<T> {
    /**
     * 响应码
     */
    private String code;
    /**
     * 响应信息
     */
    private String msg;
    /**
     * 响应数据
     */
    private T data;

    public ReptileResult() {
        this.code = ReptileConstant.SUCCESS_CODE;
    }

    public ReptileResult(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ReptileResult(T data) {
        this.code = ReptileConstant.SUCCESS_CODE;
        this.data = data;
    }

    public ReptileResult(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 是否成功
     *
     * @return boolean
     */
    public boolean isSuccess() {
        return ReptileConstant.SUCCESS_CODE.equals(this.code);
    }
}
