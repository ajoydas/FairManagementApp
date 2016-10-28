package ajoy.com.fairmanagementapp.task;

import android.os.AsyncTask;

import java.util.ArrayList;

import ajoy.com.fairmanagementapp.callbacks.EmployeeLoadedListener;
import ajoy.com.fairmanagementapp.extras.FairUtils;
import ajoy.com.fairmanagementapp.objects.Employee;

/**
 * Created by ajoy on 5/31/16.
 */
public class TaskLoadEmployees extends AsyncTask<Void,Void,ArrayList<Employee>> {
    private EmployeeLoadedListener myComponent;
    String fair_db;
    String stallname;
    String query;

    public TaskLoadEmployees(EmployeeLoadedListener myComponent, String fair_db, String stallname, String query) {
        this.myComponent = myComponent;
        this.fair_db = fair_db;
        this.stallname = stallname;
        this.query = query;
    }

    @Override
    protected ArrayList<Employee> doInBackground(Void... params) {

        ArrayList<Employee> listProducts = FairUtils.loadEmployees(fair_db,stallname,query);
        return listProducts;
    }

    @Override
    protected void onPostExecute(ArrayList<Employee> listEmployees) {
        //if (myComponent != null) {
            myComponent.onEmployeeLoaded(listEmployees);
        //}
    }
}
