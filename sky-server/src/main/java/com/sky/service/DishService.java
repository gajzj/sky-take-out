package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;

import java.util.List;

public interface DishService {

    /**
     * 新增菜品和对应的口味
     *
     * @param dishDTO
     */
    public void saveWithFlavor(DishDTO dishDTO);

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 菜品批量删除
     * <p>
     * 在 dish 表中删除菜品基本数据，同时把 dish_flavor 表中数据一起删除
     * <p>
     * 若删除的菜品数据关联某个套餐则删除失败
     * 若要删除套餐关联的菜品数据，先接触关联，再删除
     * @param ids
     */
    void deleteBatch(List<Long> ids);
}