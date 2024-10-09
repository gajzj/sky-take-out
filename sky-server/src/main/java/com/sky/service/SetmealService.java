package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {

    /**
     * 分页查询套餐信息
     * @param setmealPageQueryDTO
     * @return
     */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 批量删除套餐和套餐菜品关联信息
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据 id 查询套餐
     * @param id
     * @return
     */
    SetmealVO getByIdWithSetmealDishes(Long id);

    /**
     * 修改套餐
     * @param setmealDTO
     */
    void updateWithSetmealDishes(SetmealDTO setmealDTO);
}
