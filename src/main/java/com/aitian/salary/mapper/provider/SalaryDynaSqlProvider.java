package com.aitian.salary.mapper.provider;

import com.aitian.salary.model.Employee;
import com.aitian.salary.model.SalaryMain;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

public class SalaryDynaSqlProvider {
    public String findSalarys(final SalaryMain salaryMain){
        return new SQL(){
            {
                SELECT("*");
                FROM("s_employee s");
                LEFT_OUTER_JOIN("s_salary_main a on s.empid = a.emp_id");
                Employee e = salaryMain.getEmployee();
                if(StringUtils.isNotBlank(e.getEmpType())){
                    WHERE("s.emp_type = #{employee.empType}");
                }
                if(StringUtils.isNotBlank(e.getEmpName())){
                    WHERE("s.emp_name like #{employee.empName}");
                }
                if(e.getDepartId()!=null){
                    WHERE("s.depart_id like #{employee.departId}");
                }
                if(StringUtils.isNotBlank(salaryMain.getSalaryDate())){
                    WHERE("a.salary_date = #{salaryDate}");
                }
                if(StringUtils.isNotBlank(salaryMain.getEmpId())){
                    WHERE("s.empid = #{empId}");
                }
            }
        }.toString();
    }
}
