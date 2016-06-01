package ajoy.com.fairmanagementapp.callbacks;

import java.util.ArrayList;

import ajoy.com.fairmanagementapp.pojo.Employee;
import ajoy.com.fairmanagementapp.pojo.Product;

/**
 * Created by ajoy on 5/31/16.
 */
public interface EmployeeLoadedListener {
    public void onEmployeeLoaded(ArrayList<Employee> listEmployees);
}
