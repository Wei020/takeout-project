package com.example.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie.common.R;
import com.example.reggie.entity.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {
    R<Page<Category>> getCategoryPage(int page, int pageSize);

    R<String> categoryUpdate(Category category);

    R<String> categoryAdd(Category category);

    R<String> categoryDel(String ids);

    R<List<Category>> getCategoryList();
}
