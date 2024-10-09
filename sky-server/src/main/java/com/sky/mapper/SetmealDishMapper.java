package com.sky.mapper;

import com.sky.entity.Dish;
import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品 id 查询对应的套餐 id
     * @param dishIds
     * @return
     */
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

    /**
     * 根据套餐 id 批量删除对应套餐菜品记录
     * @param setmealIds
     */
    void deleteBySetmealIds(List<Long> setmealIds);

    /**
     * 根据套餐 id 获取记录
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{setmealId}")
    List<SetmealDish> getBySetmealId(Long setmealId);

    /**
     * 新增套餐菜品关联数据
     * @param setmealDish
     */
    @Insert("insert into setmeal_dish (setmeal_id, dish_id, name, price, copies) " +
            "values (#{setmealId}, #{dishId}, #{name}, #{price}, #{copies})")
    void insert(SetmealDish setmealDish);
}
