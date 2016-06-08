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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import ajoy.com.fairmanagementapp.activities.ActivityFair;
import ajoy.com.fairmanagementapp.activities.ActivitySeller;
import ajoy.com.fairmanagementapp.adapters.AdapterEmployees;
import ajoy.com.fairmanagementapp.adapters.AdapterSells;
import ajoy.com.fairmanagementapp.callbacks.EmployeeLoadedListener;
import ajoy.com.fairmanagementapp.callbacks.SellLoadedListener;
import ajoy.com.fairmanagementapp.extras.AsyncResponse;
import ajoy.com.fairmanagementapp.logging.L;
import ajoy.com.fairmanagementapp.materialtest.R;
import ajoy.com.fairmanagementapp.pojo.Employee;
import ajoy.com.fairmanagementapp.pojo.Sell;
import ajoy.com.fairmanagementapp.task.TaskLoadEmployees;
import ajoy.com.fairmanagementapp.task.TaskLoadSells;

/**
 * Created by ajoy on 6/2/16.
 */
public class FragmentSells extends Fragment implements AsyncResponse, View.OnClickListener, SellLoadedListener, SwipeRefreshLayout.OnRefreshListener {
    private static SearchView searchView;
    private static RadioGroup radioGroup;

    private static final String STATE_STALL_PRODUCTS = "states_sells";
    protected ArrayList<Sell> mListSells;
    private AdapterSells mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private RecyclerView mRecyclerSells;
    private TextView mTextError;

    private String search;
    private int option;

    public static FragmentSells newInstance(String param1, String param2) {
        FragmentSells fragment = new FragmentSells();
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
        final View layout = inflater.inflate(R.layout.fragment_sells, container, false);
        //L.t(getActivity(), "Inside stall Sells!!!");
        searchView = (SearchView) layout.findViewById(R.id.searchView);
        option = 1;

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search = query;
                searchResult(search);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search = newText;
                return false;
            }
        });


        mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipeProducts);
        mSwipeRefreshLayout.setOnRefreshListener(this);


        mRecyclerSells = (RecyclerView) layout.findViewById(R.id.listProducts);
        //set the layout manager before trying to display data
        mRecyclerSells.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new AdapterSells(getActivity());
        mRecyclerSells.setAdapter(mAdapter);

        if (savedInstanceState != null) {
            mListSells = savedInstanceState.getParcelableArrayList(STATE_STALL_PRODUCTS);
        } else {
            new TaskLoadSells(this, ActivityFair.fair.getDb_name(), ActivitySeller.stall.getStall(), null).execute();
        }

        mAdapter.setSells(mListSells);

        return layout;
    }


    private void searchResult(String s) {
        new TaskLoadSells(this, ActivityFair.fair.getDb_name(), ActivitySeller.stall.getStall(), search).execute();
    }

    @Override
    public void onStart() {
        super.onStart();
        onRefresh();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerSells.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), mRecyclerSells, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                editDialogShow(position);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    boolean isImageFitToScreen = false;
    Dialog editDialog;

    //Employee Details editDialog
    private void editDialogShow(final int position) {
        editDialog = new Dialog(getActivity());
        editDialog.setTitle("Sell Details");
        editDialog.setContentView(R.layout.dialog_sell_details);
        final TextView product = (TextView) editDialog.findViewById(R.id.productname);
        final TextView employee = (TextView) editDialog.findViewById(R.id.employeename);
        final TextView date = (TextView) editDialog.findViewById(R.id.selldate);
        final TextView time = (TextView) editDialog.findViewById(R.id.selltime);
        final TextView price = (TextView) editDialog.findViewById(R.id.sellprice);
        final TextView description = (TextView) editDialog.findViewById(R.id.selldescription);

        product.setText(mListSells.get(position).getProduct_name());
        employee.setText(mListSells.get(position).getEmployee_name());
        date.setText(mListSells.get(position).getDate());
        time.setText(mListSells.get(position).getTime());
        price.setText(mListSells.get(position).getPrice());
        description.setText(mListSells.get(position).getDescription());

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
                builder.setTitle("Delete Sell!");
                builder.setMessage("Are you sure you want to remove the Sell Information?");
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
                editSellShow(position);
            }
        });
    }

    Dialog editSellDialog;

    private void editSellShow(final int position) {
        editSellDialog = new Dialog(getActivity());
        editSellDialog.setTitle("Edit Sell");
        editSellDialog.setContentView(R.layout.dialog_add_sell);
        final EditText product = (EditText) editSellDialog.findViewById(R.id.addsellproduct);
        final EditText employee = (EditText) editSellDialog.findViewById(R.id.addsellemployee);
        final EditText date = (EditText) editSellDialog.findViewById(R.id.addselldate);
        final EditText time = (EditText) editSellDialog.findViewById(R.id.addselltime);
        final EditText price = (EditText) editSellDialog.findViewById(R.id.addsellprice);
        final EditText description = (EditText) editSellDialog.findViewById(R.id.addselldescription);

        product.setText(mListSells.get(position).getProduct_name());
        employee.setText(mListSells.get(position).getEmployee_name());
        date.setText(mListSells.get(position).getDate());
        time.setText(mListSells.get(position).getTime());
        price.setText(mListSells.get(position).getPrice());
        description.setText(mListSells.get(position).getDescription());

        editSellDialog.show();
        Button bcancel = (Button) editSellDialog.findViewById(R.id.bcancel);
        final Button bsave = (Button) editSellDialog.findViewById(R.id.bsave);

        bcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                L.t(getActivity(), "Request Canceled");
                editSellDialog.cancel();
            }
        });

        bsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temp = new Sell();

                temp.setId(mListSells.get(position).getId());
                temp.setStall(mListSells.get(position).getStall());
                temp.setProduct_name(product.getText().toString());
                temp.setEmployee_name(employee.getText().toString());
                temp.setDate(date.getText().toString());
                temp.setTime(time.getText().toString());
                temp.setPrice(price.getText().toString());
                temp.setDescription(description.getText().toString());

                if (temp.getProduct_name().equals("") || temp.getProduct_name() == null) {
                    L.t(getActivity(), "Product Name can't be empty!");
                } else {
                    editSell(temp);
                }
            }
        });
    }

    private void editSell(Sell temp) {


        class editSell extends AsyncTask<Void, Void, Boolean> {
            private Sell temp;
            public editSell(Sell temp) {
                this.temp = temp;
            }



            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }


            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                if (result) {
                    L.t(getActivity(), "Saved Successfully");
                    editSellDialog.cancel();
                    onRefresh();
                } else {
                    L.t(getActivity(), "Connection Error");
                }
            }

            @Override
            protected Boolean doInBackground(Void... params) {

                Integer result = 0;
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    String Url = ActivityFair.url;
                    Connection con = DriverManager.getConnection(Url, ActivityFair.username, ActivityFair.password);
                    System.out.println("Connected");

                    PreparedStatement st = con.prepareStatement("Update "+ActivityFair.fair.getDb_name()+"_sells set product_name=?,employee_name=?,date=?,time=?,price=?,description=? where id=?");

                    st.setString(1, temp.getProduct_name());
                    st.setString(2, temp.getEmployee_name());
                    st.setString(3, temp.getDate());
                    st.setString(4, temp.getTime());
                    st.setString(5, temp.getPrice());
                    st.setString(6, temp.getDescription());
                    st.setInt(7, temp.getId());
                    System.out.println("Statement");

                    ResultSet rs = null;
                    int row = 0;
                    row = st.executeUpdate();

                    System.out.println("Row ="+row);
                    if (row == 1) {
                        return true;
                    }

                } catch (ClassNotFoundException | SQLException e) {
                    e.printStackTrace();
                    return false;
                }
                return false;
            }
        }

        editSell ui = new editSell(temp);
        ui.execute();
    }


    private void delete(int pos) {
        class Delete extends AsyncTask<Void, Void, Integer> {

            ProgressDialog loading;
            int pos;

            public Delete(int pos) {
                this.pos = pos;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(getActivity(), "Removing Sell", "Please wait...", true, true);
            }

            @Override
            protected void onPostExecute(Integer value) {
                super.onPostExecute(value);
                loading.dismiss();
                if (value == 1) {
                    L.t(getActivity(), "Deleted Successfully");
                } else {
                    L.t(getActivity(), "Removing Failed!Please Check Connection and Refresh the List!");
                }
            }

            @Override
            protected Integer doInBackground(Void... params) {

                Integer result = 0;
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    String Url = ActivityFair.url;
                    Connection con = DriverManager.getConnection(Url, ActivityFair.username, ActivityFair.password);
                    System.out.println("Connected");

                    PreparedStatement st = con.prepareStatement("Delete from "+ActivityFair.fair.getDb_name()+"_sells where id=?");

                    st.setInt(1, mListSells.get(pos).getId());

                    System.out.println("Statement");

                    ResultSet rs = null;

                    int rows = st.executeUpdate();

                    System.out.println(rows);

                    if (rows == 1) {
                        result = 1;
                    }

                } catch (ClassNotFoundException | SQLException e) {
                    e.printStackTrace();
                }
                return result;

            }
        }

        Delete ui = new Delete(pos);
        ui.execute();
    }


    Dialog addDialog;
    Sell temp = null;

    private void addDialogShow() {
        addDialog = new Dialog(getActivity());
        addDialog.setTitle("Add Sell");
        addDialog.setContentView(R.layout.dialog_add_sell);
        final EditText product = (EditText) addDialog.findViewById(R.id.addsellproduct);
        final EditText employee = (EditText) addDialog.findViewById(R.id.addsellemployee);
        final EditText date = (EditText) addDialog.findViewById(R.id.addselldate);
        final EditText time = (EditText) addDialog.findViewById(R.id.addselltime);
        final EditText price = (EditText) addDialog.findViewById(R.id.addsellprice);
        final EditText description = (EditText) addDialog.findViewById(R.id.addselldescription);


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
                temp = new Sell();

                temp.setId(0);
                temp.setStall("");
                temp.setProduct_name(product.getText().toString());
                temp.setEmployee_name(employee.getText().toString());
                temp.setDate(date.getText().toString());
                temp.setTime(time.getText().toString());
                temp.setPrice(price.getText().toString());
                temp.setDescription(description.getText().toString());

                if (temp.getProduct_name().equals("") || temp.getProduct_name() == null) {
                    L.t(getActivity(), "Product Name can't be empty!");
                } else {
                    addSell(temp);
                }
            }
        });
    }

    private void addSell(Sell temp) {


        class AddSell extends AsyncTask<Void, Void, Boolean> {
            public AsyncResponse delegate = null;

            public AddSell(Sell temp) {
                this.temp = temp;
            }

            private Sell temp;

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
                    Class.forName("com.mysql.jdbc.Driver");
                    String Url = ActivityFair.url;
                    Connection con = DriverManager.getConnection(Url, ActivityFair.username, ActivityFair.password);
                    System.out.println("Connected");

                    PreparedStatement st = con.prepareStatement("INSERT INTO "+ActivityFair.fair.getDb_name()+"_sells" +
                            "(stall,product_name,employee_name,date,time,price,description)" +
                            "VALUES" +
                            "(?,?,?,?,?,?,?)");

                    st.setString(1, ActivitySeller.stall.getStall());
                    st.setString(2, temp.getProduct_name());
                    st.setString(3, temp.getEmployee_name());
                    st.setString(4, temp.getDate());
                    st.setString(5, temp.getTime());
                    st.setString(6, temp.getPrice());
                    st.setString(7, temp.getDescription());

                    System.out.println("Statement");

                    ResultSet rs = null;
                    int row = 0;
                    row = st.executeUpdate();

                    if (row == 1) {
                        return true;
                    }

                } catch (ClassNotFoundException | SQLException e) {
                    e.printStackTrace();
                    return false;
                }
                return false;
            }
        }

        AddSell ui = new AddSell(temp);
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
        outState.putParcelableArrayList(STATE_STALL_PRODUCTS, mListSells);
    }


    @Override
    public void onSellLoaded(ArrayList<Sell> listSells) {

        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        mListSells = listSells;
        mAdapter.setSells(listSells);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        System.out.println("On Activity Created Called");
        Button baddproducts = (Button) getActivity().findViewById(R.id.baddsell);
        baddproducts.setOnClickListener(this);
        Button bsearchproduct = (Button) getActivity().findViewById(R.id.bsearchsell);
        bsearchproduct.setOnClickListener(this);
    }

    @Override
    public void onRefresh() {
        L.t(getActivity(), "Refreshing......");
        //load the whole feed again on refresh, dont try this at home :)
        new TaskLoadSells(this, ActivityFair.fair.getDb_name(), ActivitySeller.stall.getStall(), null).execute();

    }

    //Click listener for Add Button
    @Override
    public void onClick(View v) {
        //L.t(getActivity(),"Add button Clicked");
        if (v.getId() == R.id.baddsell) {
            System.out.println("Add Button");
            addDialogShow();
        }

        if (v.getId() == R.id.bsearchsell) {
            System.out.println("Search Button");

            InputMethodManager inputManager = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);

            searchResult(search);
        }

    }

    //Touch
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

    }

}
