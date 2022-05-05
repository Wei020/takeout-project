package com.example.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.R;
import com.example.reggie.dto.OrdersDto;
import com.example.reggie.entity.Employee;
import com.example.reggie.entity.Orders;
import com.example.reggie.service.OrderService;
import com.sun.org.apache.xpath.internal.operations.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/page")
    public R<Page<Orders>> getOrders(int page, int pageSize, String number, String beginTime, String endTime){
        return orderService.getOrders(page, pageSize, number, beginTime, endTime);
    }

    @GetMapping("/userPage")
    public R<Page<Orders>> getOrdersByUserId(int page, int pageSize){
        return orderService.getOrdersByUserId(page, pageSize);
    }

    @PostMapping("/submit")
    public R<String> makeOrder(@RequestBody Orders orders){
        return orderService.makeOrder(orders);
    }
}
