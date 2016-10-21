package ajoy.com.fairmanagementapp.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import ajoy.com.fairmanagementapp.activities.ActivityFair;
import ajoy.com.fairmanagementapp.activities.ActivitySeller;
import ajoy.com.fairmanagementapp.adapters.AdapterEmployees;
import ajoy.com.fairmanagementapp.callbacks.EmployeeLoadedListener;
import ajoy.com.fairmanagementapp.extras.AsyncResponse;
import ajoy.com.fairmanagementapp.logging.L;
import ajoy.com.fairmanagementapp.application.R;
import ajoy.com.fairmanagementapp.objects.Employee;
import ajoy.com.fairmanagementapp.task.TaskLoadEmployees;

/**
 * Created by ajoy on 5/27/16.
 */
public class FragmentEmployees extends Fragment implements AsyncResponse,View.OnClickListener,EmployeeLoadedListener, SwipeRefreshLayout.OnRefreshListener{
    private static SearchView searchView;
    private static RadioGroup radioGroup;

    private static final String STATE_STALL_PRODUCTS = "states_stall_employee";
    protected ArrayList<Employee> mListEmployees;
    private AdapterEmployees mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private RecyclerView mRecyclerEmployees;
    private TextView mTextError;

    private String search;
    private int option;

    public static FragmentEmployees newInstance(String param1, String param2) {
        FragmentEmployees fragment = new FragmentEmployees();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View layout = inflater.inflate(R.layout.fragment_employees, container, false);
        //L.t(getActivity(),"Inside stall Employees!!!");
        searchView= (SearchView) layout.findViewById(R.id.searchView);
        option=1;

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search = query;
                searchResult(search);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search=newText;
                return false;
            }
        });


        mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipeProducts);
        mSwipeRefreshLayout.setOnRefreshListener(this);


        mRecyclerEmployees = (RecyclerView) layout.findViewById(R.id.listProducts);
        //set the layout manager before trying to display data
        mRecyclerEmployees.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new AdapterEmployees(getActivity());
        mRecyclerEmployees.setAdapter(mAdapter);

        if (savedInstanceState != null) {
            mListEmployees = savedInstanceState.getParcelableArrayList(STATE_STALL_PRODUCTS);
        } else {
            new TaskLoadEmployees(this,ActivityFair.fair.getDb_name(), ActivitySeller.stall.getStall(),null).execute();
        }

        mAdapter.setEmployees(mListEmployees);

        return layout;
    }


    private void searchResult(String s)
    {
        new TaskLoadEmployees(this,ActivityFair.fair.getDb_name(), ActivitySeller.stall.getStall(),search).execute();
    }

    @Override
    public void onStart() {
        super.onStart();
        onRefresh();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerEmployees.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), mRecyclerEmployees, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                editDialogShow(position);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }
    boolean isImageFitToScreen=false;
    Dialog editDialog;
    //Employee Details editDialog
    private void editDialogShow(final int position) {
        editDialog = new Dialog(getActivity());
        editDialog.setTitle("Employee Details");
        editDialog.setContentView(R.layout.dialog_employees_details);
        final TextView employeeName = (TextView) editDialog.findViewById(R.id.employeename);
        final TextView employeedescription = (TextView) editDialog.findViewById(R.id.employeedescription);
        final TextView employeecontactno = (TextView) editDialog.findViewById(R.id.employeecontactno);
        final TextView employeeposition = (TextView) editDialog.findViewById(R.id.employeeposition);
        final TextView employeesalary = (TextView) editDialog.findViewById(R.id.employeesalary);

        employeeName.setText(mListEmployees.get(position).getName());
        employeedescription.setText(mListEmployees.get(position).getDescription());
        employeecontactno.setText(mListEmployees.get(position).getContact_no());
        employeeposition.setText(mListEmployees.get(position).getPosition());
        employeesalary.setText(mListEmployees.get(position).getSalary());

        System.out.println("Salary  " + mListEmployees.get(position).getSalary());

        editDialog.show();
        Button bcancel = (Button) editDialog.findViewById(R.id.bcancel);
        Button bdelete = (Button) editDialog.findViewById(R.id.bdelete);
        final Button bedit = (Button) editDialog.findViewById(R.id.bedit);

        bcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                L.t(getActivity(), "Request Canceled");
                editDialog.cancel();
            }
        });

        bdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Delete Employee!");
                builder.setMessage("Are you sure you want to remove the Employee Information?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delete(position);
                        dialog.cancel();
                        editDialog.cancel();
                        onRefresh();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });

        bedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editDialog.cancel();
                editEmployeeShow(position);
            }
        });
    }

    Dialog editEmployeeDialog;

    private void editEmployeeShow(final int position) {
        editEmployeeDialog = new Dialog(getActivity());
        editEmployeeDialog.setTitle("Edit Employee");
        editEmployeeDialog.setContentView(R.layout.dialog_add_employee);
        final EditText employeeName = (EditText) editEmployeeDialog.findViewById(R.id.addemployeename);
        final EditText employeedescription = (EditText) editEmployeeDialog.findViewById(R.id.addemployeedescription);
        final EditText employeecontactno = (EditText) editEmployeeDialog.findViewById(R.id.addemployeecontactno);
        final EditText employeeposition = (EditText) editEmployeeDialog.findViewById(R.id.addemployeeposition);
        final EditText employeesalary = (EditText) editEmployeeDialog.findViewById(R.id.addemployeesalary);

        employeeName.setText(mListEmployees.get(position).getName());
        employeedescription.setText(mListEmployees.get(position).getDescription());
        employeecontactno.setText(mListEmployees.get(position).getContact_no());
        employeeposition.setText(mListEmployees.get(position).getPosition());
        employeesalary.setText(mListEmployees.get(position).getSalary());


        editEmployeeDialog.show();
        Button bcancel = (Button) editEmployeeDialog.findViewById(R.id.bcancel);
        final Button bsave = (Button) editEmployeeDialog.findViewById(R.id.bsave);

        bcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                L.t(getActivity(), "Request Canceled");
                editEmployeeDialog.cancel();
            }
        });

        bsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temp = new Employee();

                temp.setId(mListEmployees.get(position).getId());
                temp.setStall(mListEmployees.get(position).getStall());
                temp.setName(employeeName.getText().toString());
                temp.setDescription(employeedescription.getText().toString());
                temp.setContact_no(employeecontactno.getText().toString());
                temp.setPosition(employeeposition.getText().toString());
                temp.setSalary(employeesalary.getText().toString());

                if (temp.getName().equals("") || temp.getName() == null) {
                    L.t(getActivity(), "Name can't be empty!");
                } else {
                    editEmployee(temp);
                }
            }
        });
    }

    private void editEmployee(Employee temp) {


        class editEmployee extends AsyncTask<Void, Void, Boolean> {

            public editEmployee(Employee temp) {
                this.temp = temp;
            }

            private Employee temp;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }


            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                if(result)
                {
                    L.t(getActivity(), "Saved Successfully");
                    editEmployeeDialog.cancel();
                    onRefresh();
                } else {
                    L.t(getActivity(), "Connection Error");
                }
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    URL loadProductUrl = new URL("http://buetian14.com/fairmanagementapp/updateEmployee.php");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) loadProductUrl.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    String db =ActivityFair.fair.getDb_name()+"_employees";
                    String data = URLEncoder.encode("db_table", "UTF-8") + "=" + URLEncoder.encode(db, "UTF-8") + "&" +
                            URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(temp.getId()), "UTF-8") + "&" +
                            URLEncoder.encode("stall", "UTF-8") + "=" + URLEncoder.encode(ActivitySeller.stall.getStall(), "UTF-8") + "&" +
                            URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(temp.getName(), "UTF-8") + "&" +
                            URLEncoder.encode("description", "UTF-8") + "=" + URLEncoder.encode(temp.getDescription(), "UTF-8") + "&" +
                            URLEncoder.encode("contact_no", "UTF-8") + "=" + URLEncoder.encode(temp.getContact_no(), "UTF-8") + "&" +
                            URLEncoder.encode("position", "UTF-8") + "=" + URLEncoder.encode(temp.getPosition(), "UTF-8") + "&" +
                            URLEncoder.encode("salary", "UTF-8") + "=" + URLEncoder.encode(temp.getSalary(), "UTF-8");
                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();

                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String line = "";
                    String response = "";
                    if ((line = bufferedReader.readLine()) != null) {
                        System.out.println(line);
                        response += line;
                        inputStream.close();
                        bufferedReader.close();
                        httpURLConnection.disconnect();
                        if (response.contains("Success")) {
                            return true;
                        }
                    }
                    else
                    {
                        inputStream.close();
                        bufferedReader.close();
                        httpURLConnection.disconnect();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
                return false;
            }
        }

        editEmployee ui = new editEmployee(temp);
        ui.execute();
    }






    private void delete(int pos){
        class Delete extends AsyncTask<Void,Void,Integer> {

            ProgressDialog loading;
            int pos;

            public Delete(int pos) {
                this.pos = pos;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(getActivity(), "Removing Employee", "Please wait...",true,true);
            }

            @Override
            protected void onPostExecute(Integer value) {
                super.onPostExecute(value);
                loading.dismiss();
                if(value==1)
                {
                    L.t(getActivity(),"Deleted Successfully");
                }
                else
                {
                    L.t(getActivity(),"Removing Failed!Please Check Connection and Refresh the List!");
                }
            }

            @Override
            protected Integer doInBackground(Void... params) {

                Integer result=0;
                try {
                    URL loadProductUrl = new URL("http://buetian14.com/fairmanagementapp/deleteEmployee.php");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) loadProductUrl.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    String db =ActivityFair.fair.getDb_name()+"_employees";
                    String data = URLEncoder.encode("db_table", "UTF-8") + "=" + URLEncoder.encode(db, "UTF-8") + "&" +
                            URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(String.valueOf( mListEmployees.get(pos).getId()), "UTF-8");

                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();

                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String line = "";
                    String response = "";
                    if ((line = bufferedReader.readLine()) != null) {
                        System.out.println(line);
                        response += line;
                        inputStream.close();
                        bufferedReader.close();
                        httpURLConnection.disconnect();
                        if (response.contains("Success")) {
                            result = 1;
                        }
                    }
                    else
                    {
                        inputStream.close();
                        bufferedReader.close();
                        httpURLConnection.disconnect();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return  result;

            }
        }

        Delete ui = new Delete(pos);
        ui.execute();
    }


    Dialog addDialog;
    Employee temp = null;
    private void addDialogShow() {
        addDialog = new Dialog(getActivity());
        addDialog.setTitle("Add Employee");
        addDialog.setContentView(R.layout.dialog_add_employee);
        final EditText employeeName = (EditText) addDialog.findViewById(R.id.addemployeename);
        final EditText employeedescription = (EditText) addDialog.findViewById(R.id.addemployeedescription);
        final EditText employeecontactno = (EditText) addDialog.findViewById(R.id.addemployeecontactno);
        final EditText employeeposition = (EditText) addDialog.findViewById(R.id.addemployeeposition);
        final EditText employeesalary = (EditText) addDialog.findViewById(R.id.addemployeesalary);


        addDialog.show();
        Button bcancel = (Button) addDialog.findViewById(R.id.bcancel);
        final Button bsave = (Button) addDialog.findViewById(R.id.bsave);

        bcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                L.t(getActivity(), "Request Canceled");
                addDialog.cancel();
            }
        });

        bsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //* Employee
                temp = new Employee();

                temp.setId(0);
                temp.setStall("");
                temp.setName(employeeName.getText().toString());
                temp.setDescription(employeedescription.getText().toString());
                temp.setContact_no(employeecontactno.getText().toString());
                temp.setPosition(employeeposition.getText().toString());
                temp.setSalary(employeesalary.getText().toString());

                if (temp.getName().equals("") || temp.getName() == null) {
                    L.t(getActivity(), "Name can't be empty!");
                } else {
                    addEmployee(temp);
                }
            }
        });
    }

    private void addEmployee(Employee temp) {


        class AddEmployee extends AsyncTask<Void, Void, Boolean> {
            public AsyncResponse delegate = null;

            public AddEmployee(Employee temp) {
                this.temp = temp;
            }

            private Employee temp;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }


            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                delegate.processFinish(result);
            }

            @Override
            protected Boolean doInBackground(Void... params) {

                Integer result = 0;
                try {
                    URL loadProductUrl = new URL("http://buetian14.com/fairmanagementapp/addEmployee.php");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) loadProductUrl.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    String db =ActivityFair.fair.getDb_name()+"_employees";
                    String data = URLEncoder.encode("db_table", "UTF-8") + "=" + URLEncoder.encode(db, "UTF-8") + "&" +
                            URLEncoder.encode("stall", "UTF-8") + "=" + URLEncoder.encode(ActivitySeller.stall.getStall(), "UTF-8") + "&" +
                            URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(temp.getName(), "UTF-8") + "&" +
                            URLEncoder.encode("description", "UTF-8") + "=" + URLEncoder.encode(temp.getDescription(), "UTF-8") + "&" +
                            URLEncoder.encode("contact_no", "UTF-8") + "=" + URLEncoder.encode(temp.getContact_no(), "UTF-8") + "&" +
                            URLEncoder.encode("position", "UTF-8") + "=" + URLEncoder.encode(temp.getPosition(), "UTF-8") + "&" +
                            URLEncoder.encode("salary", "UTF-8") + "=" + URLEncoder.encode(temp.getSalary(), "UTF-8");
                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();

                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String line = "";
                    String response = "";
                    if ((line = bufferedReader.readLine()) != null) {
                        System.out.println(line);
                        response += line;
                        inputStream.close();
                        bufferedReader.close();
                        httpURLConnection.disconnect();
                        if (response.contains("Success")) {
                            return true;
                        }
                    }
                    else
                    {
                        inputStream.close();
                        bufferedReader.close();
                        httpURLConnection.disconnect();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
                return false;
            }
        }

        AddEmployee ui = new AddEmployee(temp);
        ui.delegate = this;
        ui.execute();
    }

    @Override
    public void processFinish(Boolean output) {
        if (output) {
            L.t(getActivity(), "Saved Successfully");
            addDialog.cancel();
            onRefresh();
        } else {
            L.t(getActivity(), "Connection Error");
        }


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //save the movie list to a parcelable prior to rotation or configuration change
        outState.putParcelableArrayList(STATE_STALL_PRODUCTS, mListEmployees);
    }


    @Override
    public void onEmployeeLoaded(ArrayList<Employee> listEmployees) {

        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        mListEmployees = listEmployees;
        mAdapter.setEmployees(listEmployees);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        System.out.println("On Activity Created Called");
        Button baddproducts = (Button) getActivity().findViewById(R.id.baddproductss);
        baddproducts.setOnClickListener(this);
        Button bsearchproduct = (Button) getActivity().findViewById(R.id.bsearchproductss);
        bsearchproduct.setOnClickListener(this);
    }

    @Override
    public void onRefresh() {
        L.t(getActivity(), "Refreshing.....");
        new TaskLoadEmployees(this,ActivityFair.fair.getDb_name(), ActivitySeller.stall.getStall(),null).execute();

    }

    //Click listener for Add Button
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.baddproductss)
        {
            System.out.println("Add Button");
            addDialogShow();
        }

        if(v.getId()==R.id.bsearchproductss)
        {
            System.out.println("Search Button");

            InputMethodManager inputManager = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);

            searchResult(search);
        }

    }

    public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }

    }
}
