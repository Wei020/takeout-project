package com.example.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie.common.R;
import com.example.reggie.dto.DishDto;
import com.example.reggie.entity.ShoppingCart;

import java.util.List;
import java.util.Map;

public interface ShoppingCartService extends IService<ShoppingCart> {

    R<List<ShoppingCart>> getShoppingCartList();

    R<String> dishSelect(ShoppingCart shoppingCart);

    R<String> cartClean();

    R<String> dishSub(Map<String, String> map);
}
