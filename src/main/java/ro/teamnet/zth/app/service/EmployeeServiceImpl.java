package ro.teamnet.zth.app.service;

import ro.teamnet.zth.app.dao.EmployeeDao;
import ro.teamnet.zth.app.domain.Employee;

import java.util.List;

/**
 * Created by Irina on 07.05.2015.
 */
public class EmployeeServiceImpl implements EmployeeService {


    @Override
    public List<Employee> findAllEmployees() {
        EmployeeDao employeeDao= new EmployeeDao();
        return employeeDao.getAllEmployees();

    }

    @Override
    public Employee findOneEmployee(Integer Id) {
        EmployeeDao employeeDao= new EmployeeDao();
        return employeeDao.getEmployeeById(Id);
    }
}
