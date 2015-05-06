package ro.teamnet.zth.app.controller;

import ro.teamnet.zth.api.annotations.MyController;
import ro.teamnet.zth.api.annotations.MyRequestMethod;

/**
 * Created by Irina on 06.05.2015.
 */
@MyController(urlPath = "/employees")
public class EmployeeController {
    @MyRequestMethod(urlPath = "/one", methodType = "GET")
    public String getOneEmployee() {
        return "1employee";
    }

    @MyRequestMethod(urlPath = "/all", methodType = "GET")
    public String getAllEmployees() {
        return "allEmployees";
    }
}
