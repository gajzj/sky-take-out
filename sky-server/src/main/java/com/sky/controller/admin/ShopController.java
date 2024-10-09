package com.sky.controller.admin;

import com.sky.constant.StatusConstant;
import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/shop")
@Api("店铺操作接口")
@Slf4j
public class ShopController {

    public static final String KEY = "SHOP_STATUS";

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 设置营业状况
     * @param status
     * @return
     */
    @PutMapping("/{status}")
    @ApiOperation("设置营业状况")
    public Result<String> setStatus(@PathVariable Integer status) {
        log.info("设置店铺的营业状态: {} -> {}",
                StatusConstant.negate(status).equals(StatusConstant.ENABLE) ? "营业中" : "打烊中",
                status.equals(StatusConstant.ENABLE) ? "营业中" : "打烊中");
        redisTemplate.opsForValue().set(KEY, status);
        return Result.success();
    }



}
