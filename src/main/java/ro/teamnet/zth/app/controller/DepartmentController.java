package ro.teamnet.zth.app.controller;

import ro.teamnet.zth.api.annotations.MyController;
import ro.teamnet.zth.api.annotations.MyRequestMethod;
import ro.teamnet.zth.app.domain.Department;
import ro.teamnet.zth.app.domain.Employee;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Irina on 06.05.2015.
 */
@MyController(urlPath = "/departments")
public class DepartmentController {
    @MyRequestMethod(urlPath = "/all", methodType = "GET")
    public List<Department> getAllDepartments() {
        Department a=new Department();
        a.setId(11);
        a.setLocations(123);
        Department b=new Department();
        b.setId(15);
        b.setLocations(156);
        List<Department> list2=new ArrayList<>();
        list2.add(a);
        list2.add(b);

        return list2;

    }
}

