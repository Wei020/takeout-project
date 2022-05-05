package com.example.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.R;
import com.example.reggie.dto.DishDto;
import com.example.reggie.entity.Dish;
import com.example.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @GetMapping("/page")
    public R<Page<DishDto>> getDishes(int page, int pageSize, String name){
        return dishService.getDishes(page, pageSize, name);
    }

    @GetMapping("/{id}")
    public R<DishDto> getDishById(@PathVariable("id") String id){
        return dishService.getDishById(id);
    }

    @PostMapping("/status/{state}")
    public R<String> changeState(@PathVariable("state") int state, String[] ids){
        return dishService.changeState(state, ids);
    }

    @DeleteMapping
    public R<String> dishDel(String[] ids){
        return dishService.dishDel(ids);
    }

    @PostMapping
    public R<String> dishAdd(@RequestBody DishDto dishDto){
        return dishService.dishAdd(dishDto);
    }

    @PutMapping
    public R<String> dishUpdate(@RequestBody DishDto dishDto){
        return dishService.dishUpdate(dishDto);
    }

    @GetMapping("/list")
    public R<List<DishDto>> getDishByCategory(String categoryId){
        return dishService.getDishByCategory(categoryId);
    }
}
