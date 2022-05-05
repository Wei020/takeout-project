package com.example.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie.common.R;
import com.example.reggie.dto.OrdersDto;
import com.example.reggie.entity.Orders;


public interface OrderService extends IService<Orders> {
    R<Page<Orders>> getOrders(int page, int pageSize, String number, String beginTime, String endTime);

    R<String> makeOrder(Orders orders);

    R<Page<Orders>> getOrdersByUserId(int page, int pageSize);
}
