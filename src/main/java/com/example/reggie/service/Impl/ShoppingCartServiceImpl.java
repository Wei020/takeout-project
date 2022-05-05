package com.example.reggie.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.common.BaseContext;
import com.example.reggie.common.R;
import com.example.reggie.entity.ShoppingCart;
import com.example.reggie.mapper.ShoppingCartMapper;
import com.example.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
    @Override
    public R<List<ShoppingCart>> getShoppingCartList() {
        List<ShoppingCart> list = list();
        return R.success(list);
    }

    @Override
    public R<String> dishSelect(ShoppingCart shoppingCart) {
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
        shoppingCart.setCreateTime(LocalDateTime.now());
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);
        if(dishId != null){
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        }else{
            queryWrapper.eq(ShoppingCart::getDishId, shoppingCart.getSetmealId());
        }
        ShoppingCart one = getOne(queryWrapper);
        if(one == null){
            boolean isSuccess = save(shoppingCart);
            if(!isSuccess)
                return R.error("添加失败！");
        }else{
            one.setNumber(one.getNumber() + 1);
            boolean isSuccess = updateById(one);
            if(!isSuccess)
                return R.error("添加失败！");
        }
        return R.success("添加成功!");
    }

    @Override
    public R<String> cartClean() {
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);
        boolean isSuccess = remove(queryWrapper);
        if(!isSuccess)
            return R.error("清除失败！");
        return R.success("清除成功！");
    }

    @Override
    @Transactional
    public R<String> dishSub(Map<String, String> map) {
        String dishId = map.get("dishId");
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        if(dishId != null){
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
            dishReduce(queryWrapper);
        }else{
            String setmealId = map.get("setmealId");
            queryWrapper.eq(ShoppingCart::getSetmealId, setmealId);
            dishReduce(queryWrapper);
        }
        return R.success("");
    }

    private void dishReduce(LambdaQueryWrapper<ShoppingCart> queryWrapper) {
        ShoppingCart one = getOne(queryWrapper);
        Integer number = one.getNumber();
        if (number > 1) {
            one.setNumber(number - 1);
            updateById(one);
        } else {
            remove(queryWrapper);
        }
    }
}
