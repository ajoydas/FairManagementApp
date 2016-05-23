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
import ajoy.com.fairmanagementapp.adapters.AdapterFairs;
import ajoy.com.fairmanagementapp.callbacks.FairLoadedListener;
import ajoy.com.fairmanagementapp.logging.L;
import ajoy.com.fairmanagementapp.materialtest.MyApplication;
import ajoy.com.fairmanagementapp.materialtest.R;
import ajoy.com.fairmanagementapp.pojo.Fair;
import ajoy.com.fairmanagementapp.task.TaskLoadFairs;

/**
 * Created by ajoy on 5/22/16.
 */
public class FragmentRunningFairs extends Fragment implements FairLoadedListener, SwipeRefreshLayout.OnRefreshListener{
    private static final String STATE_RUNNING_FAIRS = "state_Running_Fairs";
    protected ArrayList<Fair> mListFairs;
    private AdapterFairs mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerFairs;
    private TextView mTextError;

    public FragmentRunningFairs() {
        mListFairs = new ArrayList<>();
    }

    // TODO: Rename and change types and number of parameters
    public static FragmentRunningFairs newInstance(String param1, String param2) {
        FragmentRunningFairs fragment = new FragmentRunningFairs();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_running_fairs, container, false);
        //mTextError = (TextView) layout.findViewById(R.id.textVolleyError);


        mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipeFairs);
        mSwipeRefreshLayout.setOnRefreshListener(this);


        mRecyclerFairs = (RecyclerView) layout.findViewById(R.id.listFairs);
        //set the layout manager before trying to display data
        mRecyclerFairs.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new AdapterFairs(getActivity());
        mRecyclerFairs.setAdapter(mAdapter);

        L.t(getActivity(),"FragmentRunning: inside");
        if (savedInstanceState != null) {
            //if this fragment starts after a rotation or configuration change, load the existing movies from a parcelable
            mListFairs = savedInstanceState.getParcelableArrayList(STATE_RUNNING_FAIRS);
        } else {
            //if this fragment starts for the first time, load the list of movies from a database
            mListFairs = MyApplication.getWritableDatabaseFair().readFairs(1);

            //if the database is empty, trigger an AsycnTask to download movie list from the web
            if (mListFairs.isEmpty()) {
                L.T(getActivity(),"FragmentRunning: executing task from fragment");
                new TaskLoadFairs(this,1).execute();

            }
        }
        //update your Adapter to containg the retrieved movies
        mAdapter.setFairs(mListFairs);
        return layout;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerFairs.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), mRecyclerFairs, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent i = new Intent(MyApplication.getAppContext(),ActivityFair.class);
                i.putExtra("Information",mListFairs.get(position));
                System.out.println(mListFairs.get(position));
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
        outState.putParcelableArrayList(STATE_RUNNING_FAIRS, mListFairs);
    }


    @Override
    public void onFairLoaded(ArrayList<Fair> listFairs) {

        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }

        mAdapter.setFairs(listFairs);
    }

    @Override
    public void onRefresh() {
        L.t(getActivity(), "onRefresh");
        //load the whole feed again on refresh, dont try this at home :)
        new TaskLoadFairs(this,1).execute();

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
