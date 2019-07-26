package com.leyou.common.vo;

import com.leyou.common.enums.ExceptionEnum;
import lombok.Data;

/**
 *  @author: xiepanpan
 *  @Date: 2019/7/27 0:02
 *  @Description: 异常类结果对象
 */
@Data
public class ExceptionResult {

    /**
     * 状态码
     */
    private int status;
    /**
     * 信息
     */
    private String message;
    /**
     * 时间戳
     */
    private Long timestamp;

    public ExceptionResult (ExceptionEnum exceptionEnum) {
        this.status = exceptionEnum.getCode();
        this.message = exceptionEnum.getMsg();
        this.timestamp = System.currentTimeMillis();
    }
}
