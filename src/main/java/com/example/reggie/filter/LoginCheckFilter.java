package com.example.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.example.reggie.common.BaseContext;
import com.example.reggie.common.R;
import com.example.reggie.entity.Employee;
import com.example.reggie.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String requestURI = request.getRequestURI();
        String[] urls = new String[]{
            "/employee/login",
            "/backend/**",
            "/front/**",
            "/user/login",
            "/user/sendMsg",
            "/common/**"
        };
        boolean check = check(urls, requestURI);
        if(check){
            filterChain.doFilter(request, response);
            return;
        }
        Long empId = (Long) request.getSession().getAttribute("employee");
        if(empId != null){
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request, response);
            BaseContext.removeCurrentId();
            return;
        }
        Long userId =  (Long) request.getSession().getAttribute("user");
        if(userId != null){
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request, response);
            BaseContext.removeCurrentId();
            return;
        }
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    public boolean check(String[] urls, String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match){
                return true;
            }
        }
        return false;
    }
}
