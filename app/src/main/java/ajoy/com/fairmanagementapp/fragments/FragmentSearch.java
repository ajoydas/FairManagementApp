package ajoy.com.fairmanagementapp.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ajoy.com.fairmanagementapp.activities.ActivityFair;
import  ajoy.com.fairmanagementapp.adapters.AdapterProducts;
import  ajoy.com.fairmanagementapp.callbacks.ProductLoadedListener;
import  ajoy.com.fairmanagementapp.extras.ProductSorter;
import  ajoy.com.fairmanagementapp.extras.SortListener;
import  ajoy.com.fairmanagementapp.logging.L;
import  ajoy.com.fairmanagementapp.materialtest.MyApplication;

import ajoy.com.fairmanagementapp.materialtest.R;
import  ajoy.com.fairmanagementapp.pojo.Product;
import  ajoy.com.fairmanagementapp.task.TaskLoadProducts;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentSearch#newInstance} factory method to
 * create an instance of this fragment.
 */

public class FragmentSearch extends Fragment implements SortListener, ProductLoadedListener, SwipeRefreshLayout.OnRefreshListener {


    //The key used to store arraylist of movie objects to and from parcelable
    private static final String STATE_PRODUCTS = "state_products";
    //the arraylist containing our list of box office his
    protected ArrayList<Product> mListProducts;
    //the adapter responsible for displaying our movies within a RecyclerView
    private AdapterProducts mAdapter;
    //for refresh listener
    private SwipeRefreshLayout mSwipeRefreshLayout;

    //the recyclerview containing showing all our movies
    private RecyclerView mRecyclerProducts;
    //the TextView containing error messages generated by Volley
    private TextView mTextError;
    //the sorter responsible for sorting our movie results based on choice made by the user in the FAB
    private ProductSorter mSorter = new ProductSorter();

    public FragmentSearch() {
        // Required empty public constructor
        mListProducts = new ArrayList<>();
    }

    // TODO: Rename and change types and number of parameters
    public static FragmentSearch newInstance(String param1, String param2) {
        FragmentSearch fragment = new FragmentSearch();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public void onSortByName() {
        mSorter.sortProductsByName(mListProducts);
        mAdapter.notifyDataSetChanged();
    }


    //Todo fix SortListener

    @Override
    public void onSortByDate() {
        mSorter.sortProductsByPrice(mListProducts);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSortByRating() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_search, container, false);
        //mTextError = (TextView) layout.findViewById(R.id.textVolleyError);


        mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipeProducts);
        mSwipeRefreshLayout.setOnRefreshListener(this);


        mRecyclerProducts = (RecyclerView) layout.findViewById(R.id.listProducts);
        //set the layout manager before trying to display data
        mRecyclerProducts.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new AdapterProducts(getActivity());
        mRecyclerProducts.setAdapter(mAdapter);

        if (savedInstanceState != null) {
            //if this fragment starts after a rotation or configuration change, load the existing movies from a parcelable
            mListProducts = savedInstanceState.getParcelableArrayList(STATE_PRODUCTS);
        } else {
            //if this fragment starts for the first time, load the list of movies from a database
            mListProducts = MyApplication.getWritableDatabaseProduct().readProducts();
            //if the database is empty, trigger an AsycnTask to download movie list from the web
            if (mListProducts.isEmpty()) {
                L.m("FragmentUpcoming: executing task from fragment");
                new TaskLoadProducts(this).execute();

            }
        }
        //update your Adapter to containg the retrieved movies
        mAdapter.setProducts(mListProducts);
        return layout;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerProducts.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), mRecyclerProducts, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                Intent i = new Intent(MyApplication.getAppContext(),ActivityFair.class);
                i.putExtra("Url","https://www.google.com/maps/d/edit?mid=1lGcLL7WSCrilqiBQLTeXetjgIOI");
                startActivity(i);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //save the movie list to a parcelable prior to rotation or configuration change
        outState.putParcelableArrayList(STATE_PRODUCTS, mListProducts);
    }


    @Override
    public void onProductLoaded(ArrayList<Product> listProducts) {

        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }

        mAdapter.setProducts(listProducts);
    }

    @Override
    public void onRefresh() {
        L.t(getActivity(), "onRefresh");
        //load the whole feed again on refresh, dont try this at home :)
        new TaskLoadProducts(this).execute();

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




/*
    private static final String url = "jdbc:mysql://192.168.0.100:3306/logindatabase";
    private static final String username="ajoy";
    private static final String password="ajoydas";



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public FragmentSearch() {

        // Required empty public constructor
    }

    */
/**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentSearch.
     *//*

    // TODO: Rename and change types and number of parameters
    public static FragmentSearch newInstance(String param1, String param2) {
        FragmentSearch fragment = new FragmentSearch();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_search, container, false);
        TextView t=(TextView)layout.findViewById(R.id.search_text);
        t.setText(work());



        // Inflate the layout for this fragment
        return layout;
    }




    private String work(){

        class Work extends AsyncTask<Void,String,String >
        {

            @Override
            protected String doInBackground(Void... voids) {

                String s=null;
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    Connection con = DriverManager.getConnection(url, username, password);

                    System.out.println("Connected");
                    //L.T(getActivity(),"Connected");
                    PreparedStatement st = con.prepareStatement("select * from images where id=1");
                    //PreparedStatement stcount = con.prepareStatement("select count(*) from images where id = ?");

                    //st.setInt(1,Integer.parseInt(id));
                    //stcount.setInt(1,Integer.parseInt(id));

                    System.out.println("Statement");

                    ResultSet rs = null,rscount=null;

                    rs = st.executeQuery();


                    while (rs.next()) {
                        s=rs.getString("title");
                        //L.T(getActivity(),"Recieved "+rs.getString("title"));
                    }


                } catch (ClassNotFoundException | SQLException e) {
                    e.printStackTrace();
                    //L.T(getActivity(),"NOT Connected");
                }
                return s;
            }

        }
        try {
            String s1= new Work().execute().get();
            L.T(getActivity(),"Connected "+s1);
            return s1;
        } catch (InterruptedException e) {

            e.printStackTrace();
            L.T(getActivity(),"NOT Connected");
            return null;
        } catch (ExecutionException e) {
            e.printStackTrace();
            L.T(getActivity(),"NOT Connected");
            return null;
        }

    }Intent i = new Intent(getApplicationContext(), MapView.class);
        i.putExtra("Url","https://www.google.com/maps/d/edit?mid=1lGcLL7WSCrilqiBQLTeXetjgIOI");
        startActivity(i);

*/




}
