package com.example.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie.common.R;
import com.example.reggie.dto.DishDto;
import com.example.reggie.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    R<Page<DishDto>> getDishes(int page, int pageSize, String name);

    R<DishDto> getDishById(String id);

    R<String> changeState(int state, String[] ids);

    R<String> dishDel(String[] ids);

    R<String> dishAdd(DishDto dishDto);

    R<String> dishUpdate(DishDto dishDto);

    R<List<DishDto>> getDishByCategory(String categoryId);
}
