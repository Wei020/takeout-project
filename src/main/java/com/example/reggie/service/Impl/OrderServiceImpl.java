package com.example.reggie.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.common.BaseContext;
import com.example.reggie.common.R;
import com.example.reggie.dto.OrdersDto;
import com.example.reggie.entity.*;
import com.example.reggie.mapper.OrderMapper;
import com.example.reggie.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Override
    public R<Page<Orders>> getOrders(int page, int pageSize, String number, String beginTime, String endTime) {
        return null;
    }

    @Override
    @Transactional
    public R<String> makeOrder(Orders orders) {
        Long currentId = BaseContext.getCurrentId();
        List<ShoppingCart> shoppingCarts = shoppingCartService.query()
                .eq("user_id", currentId).list();
        User user = userService.getById(currentId);
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if(addressBook == null)
            return R.error("地址信息有误！");
        long id = IdWorker.getId();
        AtomicInteger amount = new AtomicInteger(0);
        List<OrderDetail> orderDetails = shoppingCarts.stream().map((item) -> {
            OrderDetail orderDetail = setDetail(id, amount, item);
            return orderDetail;
        }).collect(Collectors.toList());
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);
        setValue(orders, currentId, user, addressBook, amount, id);
        save(orders);
        orderDetailService.saveBatch(orderDetails);
        shoppingCartService.remove(queryWrapper);
        return R.success("下单成功！");
    }

    private OrderDetail setDetail(long id, AtomicInteger amount, ShoppingCart item) {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderId(id);
        orderDetail.setNumber(item.getNumber());
        orderDetail.setDishFlavor(item.getDishFlavor());
        orderDetail.setDishId(item.getDishId());
        orderDetail.setSetmealId(item.getSetmealId());
        orderDetail.setName(item.getName());
        orderDetail.setImage(item.getImage());
        orderDetail.setAmount(item.getAmount());
        amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
        return orderDetail;
    }

    private void setValue(Orders orders, Long currentId, User user, AddressBook addressBook, AtomicInteger amount, long id) {
        orders.setUserId(currentId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setAmount(new BigDecimal(amount.get()));
        orders.setUserName(user.getName());
        orders.setPhone(user.getPhone());
        orders.setNumber(String.valueOf(id));
        orders.setConsignee(addressBook.getConsignee());
        orders.setStatus(2);
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
    }

    @Override
    public R<Page<Orders>> getOrdersByUserId(int page, int pageSize) {
        Long currentId = BaseContext.getCurrentId();
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(Orders::getUserId, currentId);
        queryWrapper.orderByDesc(Orders::getOrderTime);
        page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }
}
