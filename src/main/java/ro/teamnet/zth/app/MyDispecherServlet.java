package ro.teamnet.zth.app;

import ro.teamnet.zth.api.annotations.MyController;
import ro.teamnet.zth.api.annotations.MyRequestMethod;
import ro.teamnet.zth.app.controller.DepartmentController;
import ro.teamnet.zth.app.controller.EmployeeController;
import ro.teamnet.zth.fmk.AnnotationScanUtils;
import ro.teamnet.zth.fmk.MethodAttributes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by Irina on 06.05.2015.
 */
public class MyDispecherServlet extends HttpServlet {
    HashMap<String, MethodAttributes> allowedMethods = new HashMap<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        dispatchReply(req, resp);
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        dispatchReply(req, resp);
    }

    private void dispatchReply(HttpServletRequest req, HttpServletResponse resp) {
        System.out.println("zi frumoasa");
        Object r = dispatch(req, resp);
        try {
            reply(r, req, resp);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Object dispatch(HttpServletRequest req, HttpServletResponse resp) {
        String pathInfo = req.getPathInfo();
        if (pathInfo.startsWith("/employees")) {
            EmployeeController ec = new EmployeeController();
            String allEmployee = ec.getAllEmployees();
            return allEmployee;
        } else if (pathInfo.startsWith("/departments")) {
            DepartmentController dp = new DepartmentController();
            return dp.getAllDepartments();
        }
        return "Hello!";


    }

    private void reply(Object r, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();
        out.printf(r.toString());
    }

    @Override
    public void init() throws ServletException {
        try {
            Iterable<Class> classes = AnnotationScanUtils.getClasses("ro.teamnet.zth.app.controller");
            for (Class aClass : classes) {
                getAllowedRequestMethods(classes);

            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(allowedMethods);
    }

    private HashMap<String, MethodAttributes> getAllowedRequestMethods(Iterable<Class> classes) {
        for (Class controller : classes) {
            if (controller.isAnnotationPresent(MyController.class)) {
                MyController myCtrlAnnotation = (MyController) controller.getAnnotation(MyController.class);
                String controllerUrlPath = myCtrlAnnotation.urlPath();

                Method[] methods = controller.getMethods();
                for (Method m : methods) {
                    if (m.isAnnotationPresent(MyRequestMethod.class)) {
                        MyRequestMethod annotation = m.getAnnotation(MyRequestMethod.class);
                        String key = controllerUrlPath + annotation.urlPath();

                        MethodAttributes methodAttributes = new MethodAttributes();
                        methodAttributes.setControllerClass(controller.getName());
                        methodAttributes.setMethodName(m.getName());
                        methodAttributes.setMethodType(annotation.methodType());
                        allowedMethods.put(key, methodAttributes);
                    }


                }
            }

        }
        return allowedMethods;

    }
}
