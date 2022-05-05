package com.example.reggie.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.common.CustomException;
import com.example.reggie.common.R;
import com.example.reggie.entity.Category;
import com.example.reggie.entity.Dish;
import com.example.reggie.entity.Setmeal;
import com.example.reggie.mapper.CategoryMapper;
import com.example.reggie.service.CategoryService;
import com.example.reggie.service.DishService;
import com.example.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    @Override
    public R<Page<Category>> getCategoryPage(int page, int pageSize) {
        Page<Category> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSort);
        page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    @Override
    public R<String> categoryUpdate(Category category) {
        updateById(category);
        return R.success("修改成功！");
    }

    @Override
    public R<String> categoryAdd(Category category) {
        save(category);
        return R.success("添加成功！");
    }

    @Override
    public R<String> categoryDel(String ids) {
        List<Dish> dishes = dishService.query().eq("category_id", ids).list();
        if(dishes != null)
            throw new CustomException("该类存在关联菜品，无法删除！");
        List<Setmeal> setmeals = setmealService.query().eq("category_id", ids).list();
        if(setmeals != null)
            throw new CustomException("该类存在关联套餐，无法删除！");
        removeById(ids);
        return R.success("删除成功!");
    }

    @Override
    public R<List<Category>> getCategoryList() {
        List<Category> categories = query()
                .orderByAsc("type")
                .list();
        return R.success(categories);
    }
}
