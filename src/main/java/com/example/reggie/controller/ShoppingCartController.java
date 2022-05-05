package com.example.reggie.controller;

import com.example.reggie.common.R;
import com.example.reggie.dto.DishDto;
import com.example.reggie.entity.ShoppingCart;
import com.example.reggie.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping("/list")
    public R<List<ShoppingCart>> getShoppingCartList(){
        return shoppingCartService.getShoppingCartList();
    }

    @PostMapping("/add")
    public R<String> dishSelect(@RequestBody ShoppingCart shoppingCart){
        return shoppingCartService.dishSelect(shoppingCart);
    }

    @PostMapping("/sub")
    public R<String> dishSub(@RequestBody Map<String, String> map){
        return shoppingCartService.dishSub(map);
    }

    @DeleteMapping("/clean")
    public R<String> cartClean(){
        return shoppingCartService.cartClean();
    }
}
