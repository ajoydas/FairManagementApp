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
import ajoy.com.fairmanagementapp.activities.ActivityStallMap;
import ajoy.com.fairmanagementapp.activities.ActivityStallView;
import ajoy.com.fairmanagementapp.adapters.AdapterProducts;
import ajoy.com.fairmanagementapp.adapters.AdapterStalls;
import ajoy.com.fairmanagementapp.callbacks.ProductLoadedListener;
import ajoy.com.fairmanagementapp.callbacks.StallLoadedListener;
import ajoy.com.fairmanagementapp.extras.AsyncResponse;
import ajoy.com.fairmanagementapp.extras.ProductSorter;
import ajoy.com.fairmanagementapp.extras.SortListener;
import ajoy.com.fairmanagementapp.extras.StallSorter;
import ajoy.com.fairmanagementapp.logging.L;
import ajoy.com.fairmanagementapp.materialtest.MyApplication;
import ajoy.com.fairmanagementapp.materialtest.R;
import ajoy.com.fairmanagementapp.pojo.Product;
import ajoy.com.fairmanagementapp.pojo.Stall;
import ajoy.com.fairmanagementapp.task.TaskLoadProducts;
import ajoy.com.fairmanagementapp.task.TaskLoadStalls;

/**
 * Created by ajoy on 5/29/16.
 */

public class FragmentSearchStalls extends Fragment implements SortListener,View.OnClickListener,StallLoadedListener, SwipeRefreshLayout.OnRefreshListener{
    private static SearchView searchView;

    private static final String STATE_STALL_STALLS = "states_search_stalls";
    protected ArrayList<Stall> mListStalls;
    private AdapterStalls mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private RecyclerView mRecyclerStalls;
    private TextView mTextError;
    private StallSorter mSorter = new StallSorter();

    private String search;
    private int option;
    private String location;
    private String stallname;
    private boolean res=false;


    public static FragmentSearchStalls newInstance(String param1, String param2) {
        FragmentSearchStalls fragment = new FragmentSearchStalls();
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
        final View layout = inflater.inflate(R.layout.fragment_search_stalls, container, false);
        //L.t(getActivity(),"Inside Stalls!!!");
        searchView= (SearchView) layout.findViewById(R.id.searchView);
        option=1;

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


        mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipeStalls);
        mSwipeRefreshLayout.setOnRefreshListener(this);


        mRecyclerStalls = (RecyclerView) layout.findViewById(R.id.listStalls);
        //set the layout manager before trying to display data
        mRecyclerStalls.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new AdapterStalls(getActivity());
        mRecyclerStalls.setAdapter(mAdapter);

        if (savedInstanceState != null) {

            mListStalls = savedInstanceState.getParcelableArrayList(STATE_STALL_STALLS);
        } else {

            mListStalls = MyApplication.getWritableDatabaseStall().readStalls();

            if (mListStalls.isEmpty()) {
                L.m("FragmentUpcoming: executing task from fragment");
                new TaskLoadStalls(this,ActivityFair.fair.getDb_name(),null).execute();
            }
        }
        mAdapter.setStalls(mListStalls);

        return layout;
    }

    @Override
    public void onSortByName() {
        mSorter.sortStallsByName(mListStalls);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSortByPrice() {
        mSorter.sortStallsByOwner(mListStalls);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSortByAvailability() {
        mSorter.sortStallsById(mListStalls);
        mAdapter.notifyDataSetChanged();
    }


    private void searchResult()
    {
        new TaskLoadStalls(this,ActivityFair.fair.getDb_name(),search).execute();
    }

    @Override
    public void onStart() {
        super.onStart();
        onRefresh();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerStalls.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), mRecyclerStalls, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent i = new Intent(getActivity(), ActivityStallView.class);
                i.putExtra("Information", mListStalls.get(position));
                startActivity(i);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
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
        outState.putParcelableArrayList(STATE_STALL_STALLS, mListStalls);
    }


    @Override
    public void onStallLoaded(ArrayList<Stall> listStalls) {

        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        mListStalls = listStalls;
        mAdapter.setStalls(listStalls);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button bsearchstall = (Button) getActivity().findViewById(R.id.bsearchstall);
        bsearchstall.setOnClickListener(this);
    }

    @Override
    public void onRefresh() {
        L.t(getActivity(), "Refreshing......");
        //load the whole feed again on refresh, dont try this at home :)
        new TaskLoadStalls(this,ActivityFair.fair.getDb_name(),null).execute();

    }

    //Click listener for Add Button
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.bsearchstall)
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

    }
}
