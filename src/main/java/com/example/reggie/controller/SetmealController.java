package com.example.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.R;
import com.example.reggie.dto.DishDto;
import com.example.reggie.dto.SetmealDto;
import com.example.reggie.entity.Setmeal;
import com.example.reggie.entity.SetmealDish;
import com.example.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @GetMapping("/page")
    public R<Page<SetmealDto>> getDishes(int page, int pageSize, String name){
        return setmealService.getMeals(page, pageSize, name);
    }

    @PostMapping("/status/{state}")
    public R<String> changeState(@PathVariable("state") int state, String[] ids){
        return setmealService.changeState(state, ids);
    }

    @DeleteMapping
    public R<String> setmealDel(String[] ids){
        return setmealService.setmealDel(ids);
    }

    @GetMapping("/list")
    public R<List<SetmealDto>> getSetMealList(String categoryId){
        return setmealService.getSetMealList(categoryId);
    }


    @GetMapping("/{id}")
    public R<SetmealDto> getSetMealById(@PathVariable("id") String id){
        return setmealService.getSetMealById(id);
    }

    @PutMapping
    public R<String> setmealUpdate(@RequestBody SetmealDto setmealDto){
        return setmealService.setmealUpdate(setmealDto);
    }

    @PostMapping
    public R<String> setmealAdd(@RequestBody SetmealDto setmealDto){
        return setmealService.setmealAdd(setmealDto);
    }


}
