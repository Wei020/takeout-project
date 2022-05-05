package com.example.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie.common.R;
import com.example.reggie.entity.Employee;

import javax.servlet.http.HttpServletRequest;

public interface EmployeeService extends IService<Employee> {
    R<Employee> login(Employee employee, HttpServletRequest request);

    R<String> loginOut(HttpServletRequest request);

    R<String> empAdd(Employee employee);

    R<Page<Employee>> getEmpList(int page, int pageSize, String name);

    R<Employee> getEmpById(String id);

    R<String> empUpdate(Employee employee);
}
