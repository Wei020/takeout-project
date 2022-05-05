package com.example.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.R;
import com.example.reggie.entity.Category;
import com.example.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> categoryAdd(@RequestBody Category category){
        return categoryService.categoryAdd(category);
    }

    @GetMapping("/page")
    public R<Page<Category>> getCategoryPage(int page, int pageSize){
        return categoryService.getCategoryPage(page, pageSize);
    }

    @PutMapping
    public R<String> categoryUpdate(@RequestBody Category category){
        return categoryService.categoryUpdate(category);
    }

    @DeleteMapping
    public R<String> categoryDel(String ids){
        return categoryService.categoryDel(ids);
    }

    @GetMapping("/list")
    public R<List<Category>> getCategoryList(){
        return categoryService.getCategoryList();
    }
}
