package ajoy.com.fairmanagementapp.fragments;

import android.app.Activity;
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
import android.widget.ProgressBar;
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
import ajoy.com.fairmanagementapp.adapters.AdapterSells;
import ajoy.com.fairmanagementapp.callbacks.SellLoadedListener;
import ajoy.com.fairmanagementapp.extras.AsyncResponse;
import ajoy.com.fairmanagementapp.logging.L;
import ajoy.com.fairmanagementapp.application.R;
import ajoy.com.fairmanagementapp.objects.Sell;
import ajoy.com.fairmanagementapp.task.TaskLoadSells;

/**
 * Created by ajoy on 6/2/16.
 */
public class FragmentSells extends Fragment implements AsyncResponse, View.OnClickListener, SellLoadedListener, SwipeRefreshLayout.OnRefreshListener {
    private static EditText searchView;
    private static RadioGroup radioGroup;

    private static final String STATE_STALL_PRODUCTS = "states_sells";
    protected ArrayList<Sell> mListSells;
    private AdapterSells mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerSells;

    private String search;
    private int option;

    private ProgressBar mProgressBar;
    private TextView mTextError;

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
        searchView = (EditText) layout.findViewById(R.id.searchView);
        searchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        mProgressBar = (ProgressBar) layout.findViewById(R.id.progressBar);
        mTextError = (TextView) layout.findViewById(R.id.tError);
        option = 1;

        /*searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
        });*/


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

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
        product.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        employee.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        time.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        price.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        description.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
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
                    URL loadProductUrl = new URL("http://buetian14.com/fairmanagementapp/updateSell.php");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) loadProductUrl.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    String db =ActivityFair.fair.getDb_name()+"_sells";
                    String data = URLEncoder.encode("db_table", "UTF-8") + "=" + URLEncoder.encode(db, "UTF-8") + "&" +
                            URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(temp.getId()), "UTF-8") + "&" +
                            URLEncoder.encode("product_name", "UTF-8") + "=" + URLEncoder.encode( temp.getProduct_name(), "UTF-8") + "&" +
                            URLEncoder.encode("employee_name", "UTF-8") + "=" + URLEncoder.encode(temp.getEmployee_name(), "UTF-8") + "&" +
                            URLEncoder.encode("date", "UTF-8") + "=" + URLEncoder.encode(temp.getDate(), "UTF-8") + "&" +
                            URLEncoder.encode("time", "UTF-8") + "=" + URLEncoder.encode(temp.getTime(), "UTF-8") + "&" +
                            URLEncoder.encode("price", "UTF-8") + "=" + URLEncoder.encode(temp.getPrice(), "UTF-8") + "&" +
                            URLEncoder.encode("description", "UTF-8") + "=" + URLEncoder.encode(temp.getDescription(), "UTF-8");
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
                    URL loadProductUrl = new URL("http://buetian14.com/fairmanagementapp/deleteSell.php");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) loadProductUrl.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    String db =ActivityFair.fair.getDb_name()+"_sells";
                    String data = URLEncoder.encode("db_table", "UTF-8") + "=" + URLEncoder.encode(db, "UTF-8") + "&" +
                            URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(String.valueOf( mListSells.get(pos).getId()), "UTF-8");

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

        product.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        employee.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        time.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        price.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        description.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

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
                    URL loadProductUrl = new URL("http://buetian14.com/fairmanagementapp/addSell.php");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) loadProductUrl.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    String db =ActivityFair.fair.getDb_name()+"_sells";
                    String data = URLEncoder.encode("db_table", "UTF-8") + "=" + URLEncoder.encode(db, "UTF-8") + "&" +
                            URLEncoder.encode("stall", "UTF-8") + "=" + URLEncoder.encode(ActivitySeller.stall.getStall(), "UTF-8") + "&" +
                            URLEncoder.encode("product_name", "UTF-8") + "=" + URLEncoder.encode( temp.getProduct_name(), "UTF-8") + "&" +
                            URLEncoder.encode("employee_name", "UTF-8") + "=" + URLEncoder.encode(temp.getEmployee_name(), "UTF-8") + "&" +
                            URLEncoder.encode("date", "UTF-8") + "=" + URLEncoder.encode(temp.getDate(), "UTF-8") + "&" +
                            URLEncoder.encode("time", "UTF-8") + "=" + URLEncoder.encode(temp.getTime(), "UTF-8") + "&" +
                            URLEncoder.encode("price", "UTF-8") + "=" + URLEncoder.encode(temp.getPrice(), "UTF-8") + "&" +
                            URLEncoder.encode("description", "UTF-8") + "=" + URLEncoder.encode(temp.getDescription(), "UTF-8");

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

       /* if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        mListSells = listSells;
        mAdapter.setSells(listSells);*/

        mProgressBar.setVisibility(View.INVISIBLE);
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        if(listSells==null)
        {
            mTextError.setText("Please check the connection.\nSwipe to refresh.");
            mTextError.setVisibility(View.VISIBLE);
            mListSells=null;
            mAdapter.setSells(mListSells);
            return;
        }
        else if (listSells.get(0).getId()==-1)
        {
            mTextError.setText("There is no sells available.");
            mTextError.setVisibility(View.VISIBLE);
            mListSells=null;
            mAdapter.setSells(mListSells);
            return;
        }
        mTextError.setVisibility(View.INVISIBLE);
        mListSells=listSells;
        mAdapter.setSells(listSells);

    }

    @Override
    public void onPause() {
        super.onPause();
        if(mProgressBar.getVisibility()==View.VISIBLE)
        {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
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
        //L.t(getActivity(), "Refreshing......");
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
            search = searchView.getText().toString();
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

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }

    }

}
