package com.example.reggie.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.common.R;
import com.example.reggie.entity.Employee;
import com.example.reggie.mapper.EmployeeMapper;
import com.example.reggie.service.EmployeeService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;


@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    @Override
    public R<Employee> login(Employee employee, HttpServletRequest request) {
        String pwd = employee.getPassword();
        pwd = DigestUtils.md5DigestAsHex(pwd.getBytes());
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = getOne(queryWrapper);
        if(emp == null)
            return R.error("用户不存在！");
        if(!pwd.equals(emp.getPassword()))
            return R.error("密码错误！");
        if(emp.getStatus() == 0)
            return R.error("账户已禁用！");
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    @Override
    public R<String> loginOut(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("退出成功!");
    }

    @Override
    public R<Page<Employee>> getEmpList(int nowPage, int pageSize, String name) {
        Page<Employee> pageInfo = new Page<>(nowPage, pageSize);
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
//        List<Employee> employeeList = query().last("limit " + page + " , " + pageSize).list();
        queryWrapper.orderByAsc(Employee::getId);
        page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    @Override
    public R<Employee> getEmpById(String id) {
        Employee employee = query().eq("id", id).one();
        if(employee == null)
            return R.error("无此员工！");
        return R.success(employee);
    }

    @Override
    public R<String> empUpdate(Employee employee) {
        String username = query().eq("id", employee.getId()).one().getUsername();
        if(("admin").equals(username))
            return R.error("权限不足！");
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));
        updateById(employee);
        return R.success("修改成功！");
    }
    @Override
    public R<String> empAdd(Employee employee) {
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
        save(employee);
        return R.success("添加成功！");
    }
}
