package com.aitian.salary.service;

import com.aitian.salary.mapper.EmployeeMapper;
import com.aitian.salary.model.Employee;
import com.aitian.salary.model.ImportEmpInfo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;


public interface EmployeeService {


    public PageInfo<Employee> findEmployee(String empId, String empName,String empType, Integer page, Integer pageSize);

    public Employee queryEmpForUser(String empId);

    public void deleteEmp(String empId);

    public List<Employee> queryEmpAndUser(String empId);

    public ImportEmpInfo importEmp(InputStream in, String fileName) throws Exception;

    //员工修改加载数据，有点重复了
    public List<Employee> getEmp(String empId);

    public void modifyEmp(Employee employee);

    public XSSFWorkbook exportEmpModel(String exportType);
}
