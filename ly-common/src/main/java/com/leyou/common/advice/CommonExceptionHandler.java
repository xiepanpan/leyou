package com.leyou.common.advice;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LeYouException;
import com.leyou.common.vo.ExceptionResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 默认拦截所有异常
 */
@ControllerAdvice
public class CommonExceptionHandler {

    @ExceptionHandler(LeYouException.class)
    public ResponseEntity<ExceptionResult> handleException(LeYouException e) {
        ExceptionEnum exceptionEnum = e.getExceptionEnum();
        return ResponseEntity.status(exceptionEnum.getCode()).body(new ExceptionResult(e.getExceptionEnum()));
    }
}
