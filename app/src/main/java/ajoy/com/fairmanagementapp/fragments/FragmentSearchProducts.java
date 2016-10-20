package ajoy.com.fairmanagementapp.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import ajoy.com.fairmanagementapp.activities.ActivityProductMap;
import ajoy.com.fairmanagementapp.activities.ActivityStallMap;
import ajoy.com.fairmanagementapp.adapters.AdapterProducts;
import ajoy.com.fairmanagementapp.callbacks.ProductLoadedListener;
import ajoy.com.fairmanagementapp.extras.AsyncResponse;
import ajoy.com.fairmanagementapp.extras.ProductSorter;
import ajoy.com.fairmanagementapp.extras.SortListener;
import ajoy.com.fairmanagementapp.logging.L;
import ajoy.com.fairmanagementapp.application.MyApplication;
import ajoy.com.fairmanagementapp.application.R;
import ajoy.com.fairmanagementapp.objects.Product;
import ajoy.com.fairmanagementapp.task.TaskLoadProducts;

/**
 * Created by ajoy on 5/24/16.
 */

public class FragmentSearchProducts extends Fragment implements AsyncResponse,SortListener,View.OnClickListener,ProductLoadedListener, SwipeRefreshLayout.OnRefreshListener{
    private static SearchView searchView;
    private static RadioGroup radioGroup;

    private static final String STATE_STALL_PRODUCTS = "states_search_products";
    protected ArrayList<Product> mListProducts;
    private AdapterProducts mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private RecyclerView mRecyclerProducts;
    private TextView mTextError;
    private ProductSorter mSorter = new ProductSorter();

    private String search;
    private int option;
    private String location;
    private String stallname;
    private boolean res=false;


    public static FragmentSearchProducts newInstance(String param1, String param2) {
        FragmentSearchProducts fragment = new FragmentSearchProducts();
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
        final View layout = inflater.inflate(R.layout.fragment_search_products, container, false);
        //L.t(getActivity(),"Inside stall Products!!!");
        searchView= (SearchView) layout.findViewById(R.id.searchView);
        option=1;
        radioGroup= (RadioGroup) layout.findViewById(R.id.searchViewRadio);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                if (checkedId==R.id.byName){
                    searchView.setQueryHint("Search by Name");
                    option=1;
                }
                else {
                    searchView.setQueryHint("Search by Author/Company");
                    option=2;
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search = query;
                searchResult();
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


        mRecyclerProducts = (RecyclerView) layout.findViewById(R.id.listProducts);
        //set the layout manager before trying to display data
        mRecyclerProducts.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new AdapterProducts(getActivity());
        mRecyclerProducts.setAdapter(mAdapter);

        if (savedInstanceState != null) {
            mListProducts = savedInstanceState.getParcelableArrayList(STATE_STALL_PRODUCTS);
        } else {
//            mListProducts = MyApplication.getWritableDatabaseProduct().readProducts();
//            if (mListProducts.isEmpty()) {
//                L.m("FragmentUpcoming: executing task from fragment");
                new TaskLoadProducts(this,ActivityFair.fair.getDb_name(),null,0).execute();

            //}
        }
        mAdapter.setProducts(mListProducts);

        return layout;
    }

    @Override
    public void onSortByName() {
        mSorter.sortProductsByName(mListProducts);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSortByPrice() {
        mSorter.sortProductsByPrice(mListProducts);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSortByAvailability() {
        mSorter.sortProductsByAvailability(mListProducts);
        mAdapter.notifyDataSetChanged();
    }


    private void searchResult()
    {
        new TaskLoadProducts(this,ActivityFair.fair.getDb_name(),search,option).execute();
    }

    @Override
    public void onStart() {
        super.onStart();
        onRefresh();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerProducts.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), mRecyclerProducts, new ClickListener() {
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

    //Product Details dialog
    private void editDialogShow(final int position) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setTitle("Product Details");
        dialog.setContentView(R.layout.dialog_search_products_details);
        final TextView productName = (TextView) dialog.findViewById(R.id.dialog_stall_product_name);
        final TextView productCompany = (TextView) dialog.findViewById(R.id.dialog_stall_product_company);
        final TextView productDescription = (TextView) dialog.findViewById(R.id.dialog_stall_product_description);
        final TextView productPrice = (TextView) dialog.findViewById(R.id.dialog_stall_product_price);
        final TextView productAvailability = (TextView) dialog.findViewById(R.id.dialog_stall_product_availability);
        final ImageView productImage = (ImageView) dialog.findViewById(R.id.dialog_stall_product_image);

        productImage.setImageBitmap(StringToBitMap(mListProducts.get(position).getImage()));
        productName.setText(mListProducts.get(position).getName());
        productCompany.setText(mListProducts.get(position).getCompany());
        productDescription.setText(mListProducts.get(position).getDescription());
        productPrice.setText(mListProducts.get(position).getPrice());
        productAvailability.setText(mListProducts.get(position).getAvailability());

        dialog.show();
        Button bcancel= (Button) dialog.findViewById(R.id.bcancel);
        Button bviewstallmap= (Button) dialog.findViewById(R.id.bviewStallMap);
        final android.widget.LinearLayout.LayoutParams params= (LinearLayout.LayoutParams) productImage.getLayoutParams();

        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isImageFitToScreen) {
                    isImageFitToScreen=false;
                    productImage.setLayoutParams(params);
                    productImage.setAdjustViewBounds(true);
                }else{
                    isImageFitToScreen=true;
                    productImage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    productImage.setScaleType(ImageView.ScaleType.FIT_XY);
                }
            }
        });


        bcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                L.t(getActivity(),"Request Canceled");
                dialog.cancel();
            }
        });

        bviewstallmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pos = position;
                getLocation(mListProducts.get(position).getStall());
            }
        });
    }
int pos;

    private void getLocation(String name){


        class GetLocation extends AsyncTask<Void, Void, Boolean> {
            public AsyncResponse delegate = null;

            private String stall;
            public GetLocation(String stall) {
                this.stall = stall;
            }

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

                Integer result=0;
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    String Url = ActivityFair.url;
                    Connection con = DriverManager.getConnection(Url, ActivityFair.username, ActivityFair.password);
                    System.out.println("Connected");

                    PreparedStatement st = con.prepareStatement("Select location,stall_name from "+ActivityFair.fair.getDb_name()+"_stalls where stall=?");

                    st.setString(1, stall);

                    System.out.println("Statement");

                    ResultSet rs = null;

                    rs = st.executeQuery();

                    while (rs.next())
                    {
                        location = rs.getString("location");
                        stallname = rs.getString("stall_name");
                        System.out.println(location+" "+stallname);
                        return  true;
                    }

                } catch (ClassNotFoundException | SQLException e) {
                    e.printStackTrace();
                    return false;
                }

                return false;
            }
        }

        GetLocation ui = new GetLocation(name);
        ui.delegate = this;
        ui.execute();
    }

    @Override
    public void processFinish(Boolean output) {
        if(output) {
            if(mListProducts.get(pos).getImage()!=null) {
                Intent i = new Intent(getActivity(), ActivityProductMap.class);
                i.putExtra("Location", location);
                i.putExtra("Stallname", stallname);
                i.putExtra("Image", mListProducts.get(pos).getImage());
                startActivity(i);
            }
            else
            {
                Intent i = new Intent(getActivity(), ActivityStallMap.class);
                i.putExtra("Location", location);
                i.putExtra("Stallname", stallname);
                startActivity(i);
            }
        }
        else {
            L.t(getActivity(), "Connection Error");
        }

    }

    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }




    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //save the movie list to a parcelable prior to rotation or configuration change
        outState.putParcelableArrayList(STATE_STALL_PRODUCTS, mListProducts);
    }


    @Override
    public void onProductLoaded(ArrayList<Product> listProducts) {

        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        mListProducts = listProducts;
        mAdapter.setProducts(listProducts);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button bsearchproduct = (Button) getActivity().findViewById(R.id.bsearchproduct);
        bsearchproduct.setOnClickListener(this);
    }

    @Override
    public void onRefresh() {
        L.t(getActivity(), "Refreshing......");
        //load the whole feed again on refresh, dont try this at home :)
        new TaskLoadProducts(this,ActivityFair.fair.getDb_name(),null,0).execute();

    }

    //Click listener for Add Button
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.bsearchproduct)
        {
            InputMethodManager inputManager = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);

            searchResult();
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
