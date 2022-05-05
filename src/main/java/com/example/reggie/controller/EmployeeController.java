package com.example.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.R;
import com.example.reggie.entity.Employee;
import com.example.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping
    public R<String> empAdd(@RequestBody Employee employee){
        return employeeService.empAdd(employee);
    }

    @PutMapping
    public R<String> empUpdate(@RequestBody Employee employee){
        return employeeService.empUpdate(employee);
    }

    @PostMapping("/login")
    public R<Employee> login(@RequestBody Employee employee, HttpServletRequest request){
        return employeeService.login(employee, request);
    }

    @PostMapping("/logout")
    public R<String> loginOut(HttpServletRequest request){
        return employeeService.loginOut(request);
    }

    @GetMapping("/page")
    public R<Page<Employee>> getEmpList(int page, int pageSize, String name){
        return employeeService.getEmpList(page, pageSize, name);
    }

    @GetMapping("/{id}")
    public R<Employee> getEmpById(@PathVariable("id") String id){
        return employeeService.getEmpById(id);
    }
}
