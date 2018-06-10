package com.aitian.salary.service.impl;

import com.aitian.salary.Utils.ConverterSystem;
import com.aitian.salary.Utils.ReadExcel;
import com.aitian.salary.mapper.EmployeeMapper;
import com.aitian.salary.mapper.SalaryMapper;
import com.aitian.salary.mapper.SalaryTypeEmpMapper;
import com.aitian.salary.model.Employee;
import com.aitian.salary.model.SalaryMain;
import com.aitian.salary.model.SalaryTypeEmp;
import com.aitian.salary.service.SalaryService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.InputStream;
import java.util.List;

@Service(value = "salaryService")
public class SalaryServiceImpl implements SalaryService {

    @Autowired
    private SalaryMapper salaryMapper;

    @Autowired
    private SalaryTypeEmpMapper salaryTypeEmpMapper;

    @Autowired
    private EmployeeMapper employeeEmpMapper;

    @Transactional
    @Override
    public int[] batchImport(InputStream inputStream, String fileName) {

        //创建处理EXCEL
        ReadExcel readExcel=new ReadExcel();
        //解析excel，获取信息集合。
        int[] arr = readExcel.checkExcel(inputStream, fileName);
        List<SalaryMain> salaryList = readExcel.getExcelInfo();

        if(salaryList != null){
            salaryMapper.insertList(salaryList);
        }

       return arr;
    }

    @Override
    public PageInfo<SalaryMain> findSalarys(String empName, String empId, String salaryDate, Integer departId, Integer empType, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        SalaryMain salaryMain = new SalaryMain();
        salaryMain.setEmpId(empId);
        salaryMain.setEmployee(new Employee());
        if(empName != null && StringUtils.isNotBlank(empName)){
            salaryMain.getEmployee().setEmpName("%"+empName+"%");
        }
        salaryMain.getEmployee().setDepartId(departId);
        if(empType!=null) salaryMain.getEmployee().setEmpType(empType+"");
        if(salaryDate!=null){
            if(salaryDate.matches("[0-9][0-9][0-9][0-9]-[0-9][0-9]")){
                salaryMain.setSalaryDate(salaryDate);
            }else if(salaryDate.startsWith("-")){
                salaryMain.setSalaryDate("%"+salaryDate);
            }else if(salaryDate.endsWith("-")){
                salaryMain.setSalaryDate(salaryDate+"%");
            }
        }

        List<SalaryMain> salaryList = salaryMapper.findSalarys(salaryMain);

        salaryList.forEach(salary -> {
            if(ConverterSystem.ALL_EMPLOYEE_TYPE.containsKey(Integer.parseInt(salary.getEmpType()))){
                salary.setEmpType(ConverterSystem.ALL_EMPLOYEE_TYPE.get(Integer.parseInt(salary.getEmpType())).getTypeName());
            }
            if(ConverterSystem.ALL_DEPARTMENT.containsKey(Integer.parseInt(salary.getDepartId()))){
                salary.setDepartId(ConverterSystem.ALL_DEPARTMENT.get(Integer.parseInt(salary.getDepartId())).getDepartName());
            }
        });
        PageInfo<SalaryMain> pageInfo = new PageInfo<>(salaryList);
        return  pageInfo;
    }

    @Override
    public SalaryMain findSalary(String empId, String salaryDate) {
        SalaryMain salaryMain = new SalaryMain();
        salaryMain.setEmpId(empId);
        salaryMain.setSalaryDate(salaryDate);
        List<SalaryMain> salaryList = salaryMapper.select(salaryMain);

        salaryList.forEach(salary -> {
            Employee emp = employeeEmpMapper.selectByPrimaryKey(salary.getEmpId());
            if(emp != null){
                if(ConverterSystem.ALL_EMPLOYEE_TYPE.containsKey(Integer.parseInt(emp.getEmpType()))){
                    emp.setEmpTypeStr(ConverterSystem.ALL_EMPLOYEE_TYPE.get(Integer.parseInt(emp.getEmpType())).getTypeName());
                }
                if(ConverterSystem.ALL_DEPARTMENT.containsKey(emp.getDepartId())){
                    emp.setDepartIdStr(ConverterSystem.ALL_DEPARTMENT.get(emp.getDepartId()).getDepartName());
                }
                salary.setEmployee(emp);
                Example example = new Example(SalaryTypeEmp.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("salaryId",salary.getSalaryId());
                salary.setSalaryTypeEmpList(salaryTypeEmpMapper.selectByExample(example));
            }else {
                salaryList.remove(salary);
            }
        });
        return  salaryList.size()>0?salaryList.get(0):null;
    }

    @Override
    public SalaryMain findSalaryByPk(Integer salaryId) {
        SalaryMain salary = salaryMapper.selectByPrimaryKey(salaryId);

        Employee emp = employeeEmpMapper.selectByPrimaryKey(salary.getEmpId());
        if(emp != null) {
            if (ConverterSystem.ALL_EMPLOYEE_TYPE.containsKey(Integer.parseInt(emp.getEmpType()))) {
                emp.setEmpTypeStr(ConverterSystem.ALL_EMPLOYEE_TYPE.get(Integer.parseInt(emp.getEmpType())).getTypeName());
            }
            if (ConverterSystem.ALL_DEPARTMENT.containsKey(emp.getDepartId())) {
                emp.setDepartIdStr(ConverterSystem.ALL_DEPARTMENT.get(emp.getDepartId()).getDepartName());
            }
            salary.setEmployee(emp);
            Example example = new Example(SalaryTypeEmp.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("salaryId", salary.getSalaryId());
            salary.setSalaryTypeEmpList(salaryTypeEmpMapper.selectByExample(example));
        }
        return  salary;
    }

    @Transactional
    @Override
    public void addSalaryList(SalaryMain salaryMain) {
        salaryMapper.insert(salaryMain);
        salaryMain.getSalaryTypeEmpList().forEach(salaryTypeEmp -> {
            salaryTypeEmp.setSalaryId(salaryMain.getSalaryId());
        });
        salaryTypeEmpMapper.insertList(salaryMain.getSalaryTypeEmpList());
    }

    @Transactional
    @Override
    public void deleteSalary(Integer salaryId) {
        Example example = new Example(SalaryTypeEmp.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("salaryId",salaryId);
        salaryTypeEmpMapper.deleteByExample(example);
        salaryMapper.deleteByPrimaryKey(salaryId);
    }

    @Transactional
    @Override
    public void updateSalarys(SalaryMain salaryMain) {
        Example example = new Example(SalaryTypeEmp.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("salaryId",salaryMain.getSalaryId());
        salaryTypeEmpMapper.deleteByExample(example);
        salaryMain.getSalaryTypeEmpList().forEach(salaryTypeEmp -> {
            salaryTypeEmp.setSalaryId(salaryMain.getSalaryId());
        });
        salaryTypeEmpMapper.insertList(salaryMain.getSalaryTypeEmpList());
        salaryMapper.updateByPrimaryKey(salaryMain);
    }

    @Override
    public List<Employee> getEmployees(String empName, String departId) {
        PageHelper.startPage(1, 10);
        Example example = new Example(Employee.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("departId", departId);
        criteria.andLike("empName","%"+empName+"%");
        return employeeEmpMapper.selectByExample(example);
    }
}
