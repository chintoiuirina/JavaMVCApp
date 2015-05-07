package ro.teamnet.zth.app.service;

import ro.teamnet.zth.api.annotations.Id;
import ro.teamnet.zth.app.dao.EmployeeDao;
import ro.teamnet.zth.app.domain.Employee;

import java.util.List;

/**
 * Created by Irina on 07.05.2015.
 */
public interface EmployeeService {
    List<Employee> findAllEmployees();
    Employee findOneEmployee(Integer Id);

}
