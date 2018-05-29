package com.aitian.salary.Utils;

import com.aitian.salary.model.Department;
import com.aitian.salary.model.EmployeeType;
import com.aitian.salary.model.SalaryType;
import com.aitian.salary.model.UserType;

import java.util.List;

/**
 * @Author yisicheng 2018-05-29
 * 系统静态变量 和系统初始化数据
 */
public class ConverterSystem {
    //session用户key
    public static final String SESSION_USER_KEY = "salary_login_user";
    //所有用户类型
    public static List<UserType> ALL_USER_TYPE = null;
    //所有员工类型信息
    public static List<EmployeeType> ALL_EMPLOYEE_TYPE = null;
    //所有科室信息
    public static List<Department> ALL_DEPARTMENT = null;
    //所有工资类型信息
    public static List<SalaryType> ALL_SALARY_TYPE = null;


}
