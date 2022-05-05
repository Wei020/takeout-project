package com.example.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie.common.R;
import com.example.reggie.dto.SetmealDto;
import com.example.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    R<Page<SetmealDto>> getMeals(int page, int pageSize, String name);

    R<String> changeState(int state, String[] ids);

    R<SetmealDto> getSetMealById(String id);

    R<String> setmealUpdate(SetmealDto setmealDto);

    R<String> setmealAdd(SetmealDto setmealDto);

    R<String> setmealDel(String[] ids);

    R<List<SetmealDto>> getSetMealList(String categoryId);

}
