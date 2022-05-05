package com.example.reggie.controller;

import com.example.reggie.common.R;
import com.example.reggie.entity.User;
import com.example.reggie.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        return userService.getCode(user, session);
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        return userService.login(map, session);
    }

    @PostMapping("/loginout")
    public R<String> loginout(HttpSession session){
        return userService.loginout(session);
    }
}
