package ro.teamnet.zth.app.controller;

import ro.teamnet.zth.api.annotations.MyController;
import ro.teamnet.zth.api.annotations.MyParam;
import ro.teamnet.zth.api.annotations.MyRequestMethod;
import ro.teamnet.zth.app.domain.Employee;
import ro.teamnet.zth.app.service.EmployeeService;
import ro.teamnet.zth.app.service.EmployeeServiceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Irina on 06.05.2015.
 */
@MyController(urlPath = "/employees")
public class EmployeeController {
    @MyRequestMethod(urlPath = "/one", methodType = "GET")
    public Employee getOneEmployee(@MyParam(name ="idEmployee") String idEmployee) {
        EmployeeService employeeService=new EmployeeServiceImpl();
        return employeeService.findOneEmployee(Integer.parseInt(idEmployee));
    }

    @MyRequestMethod(urlPath = "/all", methodType = "GET")
    public List<Employee> getAllEmployees() {

       /*Employee e=new Employee();
        e.setId(11);
        e.setFirstName("Bobby");
        Employee e2=new Employee();
        e2.setId(15);
        e2.setFirstName("Ana");
        List<Employee> list=new ArrayList<>();
        list.add(e);
        list.add(e2);

        return list;
        */
        EmployeeService employeeService=new EmployeeServiceImpl();
        return employeeService.findAllEmployees();


    }
}
