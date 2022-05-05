package com.example.reggie.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.common.R;
import com.example.reggie.entity.User;
import com.example.reggie.mapper.UserMapper;
import com.example.reggie.service.UserService;
import com.example.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public R<String> getCode(User user, HttpSession session) {
        Integer code = ValidateCodeUtils.generateValidateCode(6);
        session.setAttribute(user.getPhone(), code);
        log.info("验证码：" + code);
        return R.success("验证码发送成功！");
    }

    @Override
    public R<User> login(Map map, HttpSession session) {
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();
        String code1 = session.getAttribute(phone).toString();
        if(code != null && code.equals(code1)){
            User user = query().eq("phone", phone).one();
            if(user == null){
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                save(user);
            }
            session.setAttribute("user",user.getId());
            return R.success(user);
        }
        return R.error("验证码不正确！");
    }

    @Override
    public R<String> loginout(HttpSession session) {
        session.removeAttribute("user");
        return R.success("退出成功！");
    }
}
