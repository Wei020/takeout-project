package com.example.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie.common.R;
import com.example.reggie.entity.User;

import javax.servlet.http.HttpSession;
import java.util.Map;

public interface UserService extends IService<User> {
    R<String> getCode(User user, HttpSession session);

    R<User> login(Map map, HttpSession session);

    R<String> loginout(HttpSession session);

}
