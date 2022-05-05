package com.example.reggie.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.common.R;
import com.example.reggie.dto.DishDto;
import com.example.reggie.entity.*;
import com.example.reggie.mapper.DishMapperr;
import com.example.reggie.service.CategoryService;
import com.example.reggie.service.DishFlavorService;
import com.example.reggie.service.DishService;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Options;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapperr, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Override
    public R<Page<DishDto>> getDishes(int page, int pageSize, String name) {
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name), Dish::getName, name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        page(pageInfo, queryWrapper);
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if(category != null){
                dishDto.setCategoryName(category.getName());
            }
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }

    @Override
    public R<DishDto> getDishById(String id) {
        Dish dish = getById(id);
        if(dish == null)
            return R.error("无此菜品！");
        List<DishFlavor> dishFlavors = dishFlavorService.query().eq("dish_id", id).list();
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        dishDto.setFlavors(dishFlavors);
        return R.success(dishDto);
    }

    @Override
    public R<String> changeState(int state, String[] ids) {
        Dish dish = new Dish();
        dish.setStatus(state);
        for (String id : ids) {
            dish.setId(Long.parseLong(id));
            boolean isSuccess = updateById(dish);
            if(!isSuccess)
                return R.error("修改失败！");
        }
        return R.success("修改成功！");
    }

    @Override
    @Transactional
    public R<String> dishDel(String[] ids) {
        removeByIds(Arrays.asList(ids));
        return R.success("删除成功！");
    }

    @Override
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Transactional
    public R<String> dishAdd(DishDto dishDto) {
        save(dishDto);
        Long id = dishDto.getId();
        List<DishFlavor> list = dishDto.getFlavors();
        list = list.stream().peek((item) -> item.setDishId(id)).collect(Collectors.toList());
        dishFlavorService.saveBatch(list);
        return R.success("添加成功！");
    }

    @Override
    @Transactional
    public R<String> dishUpdate(DishDto dishDto) {
        updateById(dishDto);
        List<DishFlavor> list = dishDto.getFlavors();
        dishFlavorService.updateBatchById(list);
        return R.success("更新成功！");
    }

    @Override
    public R<List<DishDto>> getDishByCategory(String categoryId) {
        List<Dish> dishes = query().eq("category_id", categoryId)
                .eq("status", 1)
                .list();
        List<DishDto> dishes1 = dishes.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long id = item.getId();
            List<DishFlavor> dishFlavors = dishFlavorService.query().eq("dish_id", id).list();
            dishDto.setFlavors(dishFlavors);
            return dishDto;
        }).collect(Collectors.toList());
        return R.success(dishes1);
    }


}
