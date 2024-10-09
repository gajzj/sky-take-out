package com.sky.controller.admin;

import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
@Api(tags = "套餐相关接口")
@Slf4j
public class SetmealController {

    @Autowired
    SetmealService setmealService;

    /**
     * 分页查询套餐
     * @param setmealPageQueryDTO
     * @return 分页查询结果
     */
    @GetMapping("/page")
    @ApiOperation("分页查询套餐")
    public Result<PageResult> page(@ModelAttribute SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("分页查询套餐: {}", setmealPageQueryDTO);
        PageResult result = setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(result);
    }

    /**
     * 批量删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("批量删除套餐")
    public Result<String> delete(@RequestParam List<Long> ids) {
        log.info("根据id批量删除套餐: {}", ids);
        setmealService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 根据 id 查询套餐
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐")
    public Result<SetmealVO> getById(@PathVariable Long id) {
        log.info("根据id查询套餐: {}", id);
        SetmealVO setmealVO = setmealService.getByIdWithSetmealDishes(id);
        return Result.success(setmealVO);
    }

    /**
     *  修改套餐
     * @param setmealDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改套餐")
    public Result<String> update(@RequestBody SetmealDTO setmealDTO) {
        log.info("修改套餐: id={}, setmealDishes={}", setmealDTO.getId(), setmealDTO.getSetmealDishes());
        setmealService.updateWithSetmealDishes(setmealDTO);
        return Result.success();
    }

    /**
     * 套餐起售、停售
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    public Result<String> startOrStop(@PathVariable Integer status, @RequestParam Long id) {
        log.info("设置套餐状态: {} -> {}", StatusConstant.negate(status), status);
        setmealService.startOrStop(status, id);
        return Result.success();
    }
}
