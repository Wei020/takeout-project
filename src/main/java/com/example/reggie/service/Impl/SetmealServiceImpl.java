package com.example.reggie.service.Impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.common.R;
import com.example.reggie.dto.DishDto;
import com.example.reggie.dto.SetmealDto;
import com.example.reggie.entity.*;
import com.example.reggie.mapper.SetmealMapper;
import com.example.reggie.service.CategoryService;
import com.example.reggie.service.SetmealDishService;
import com.example.reggie.service.SetmealService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public R<Page<SetmealDto>> getMeals(int page, int pageSize, String name) {
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name), Setmeal::getName, name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        page(pageInfo, queryWrapper);
        BeanUtils.copyProperties(pageInfo, setmealDtoPage, "records");
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if(category != null){
                setmealDto.setCategoryName(category.getName());
            }
            return setmealDto;
        }).collect(Collectors.toList());
        setmealDtoPage.setRecords(list);
        return R.success(setmealDtoPage);
    }

    @Override
    @Transactional
    public R<String> changeState(int state, String[] ids) {
        Setmeal setmeal = new Setmeal();
        setmeal.setStatus(state);
        for (String id : ids) {
            setmeal.setId(Long.parseLong(id));
            boolean isSuccess = updateById(setmeal);
            if(!isSuccess)
                return R.error("修改失败！");
        }
        return R.success("修改成功！");
    }


    @Override
    public R<SetmealDto> getSetMealById(String id) {
        Setmeal setmeal = getById(id);
        if(setmeal == null)
            return R.error("无此套餐！");
        List<SetmealDish> setmealDishes = setmealDishService.query().eq("setmeal_id", id).list();
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, setmealDto);
        setmealDto.setSetmealDishes(setmealDishes);
        return R.success(setmealDto);
    }

    @Override
    public R<String> setmealUpdate(SetmealDto setmealDto) {
        updateById(setmealDto);
        List<SetmealDish> list = setmealDto.getSetmealDishes();
        setmealDishService.updateBatchById(list);
        stringRedisTemplate.delete("setmeal_" + setmealDto.getCategoryId());
        return R.success("更新成功！");
    }

    @Override
    @Transactional
    public R<String> setmealAdd(SetmealDto setmealDto) {
        save(setmealDto);
        Long id = setmealDto.getId();
        List<SetmealDish> list = setmealDto.getSetmealDishes();
        list = list.stream().peek((item) -> item.setSetmealId(id)).collect(Collectors.toList());
        setmealDishService.saveBatch(list);
        stringRedisTemplate.delete("setmeal_" + setmealDto.getCategoryId());
        return R.success("添加成功！");
    }

    @Override
    @Transactional
    public R<String> setmealDel(String[] ids) {
        Integer count = query().in("id", ids)
                .eq("status", 1)
                .count();
        if(count > 0)
            return R.error("套餐处于启售状态，无法删除！");
        removeByIds(Arrays.asList(ids));
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);
        for (String id : ids) {
            Setmeal one = query().eq("id", id).one();
            stringRedisTemplate.delete("setmeal_" + one.getCategoryId());
        }
        setmealDishService.remove(lambdaQueryWrapper);
        return R.success("删除成功！");
    }

    @Override
    public R<List<SetmealDto>> getSetMealList(String categoryId) {
        List<SetmealDto> setmealDtos = null;
        String key = "setmeal_" + categoryId;
        String s = stringRedisTemplate.opsForValue().get(key);
        if(s != null){
            setmealDtos = JSONUtil.toList(s, SetmealDto.class);
            return R.success(setmealDtos);
        }
        List<Setmeal> setmeals = query().eq("category_id", categoryId).list();
        setmealDtos = setmeals.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Category category = categoryService.query().eq("id", categoryId).one();
            setmealDto.setCategoryName(category.getName());
            return setmealDto;
        }).collect(Collectors.toList());
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(setmealDtos), 60, TimeUnit.MINUTES);
        return R.success(setmealDtos);
    }
}
