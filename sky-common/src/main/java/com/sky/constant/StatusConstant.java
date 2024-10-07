package com.sky.constant;

import ch.qos.logback.classic.spi.IThrowableProxy;

import java.util.Objects;

/**
 * 状态常量，启用或者禁用
 */
public class StatusConstant {

    //启用
    public static final Integer ENABLE = 1;

    //禁用
    public static final Integer DISABLE = 0;

    // 获得相反状态
    public static Integer negate(Integer status) {
        // 防腐
        if (!status.equals(ENABLE) && !status.equals(DISABLE)) {
            throw new IllegalArgumentException("请传入正确的状态值");
        }
        return Objects.equals(status, DISABLE) ? ENABLE : DISABLE;
    }
}
