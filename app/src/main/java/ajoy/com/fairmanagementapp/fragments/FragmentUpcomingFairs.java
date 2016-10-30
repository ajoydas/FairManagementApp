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
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import ajoy.com.fairmanagementapp.activities.ActivityFair;
import ajoy.com.fairmanagementapp.adapters.AdapterFairs;
import ajoy.com.fairmanagementapp.callbacks.FairLoadedListener;
import ajoy.com.fairmanagementapp.logging.L;
import ajoy.com.fairmanagementapp.application.MyApplication;
import ajoy.com.fairmanagementapp.application.R;
import ajoy.com.fairmanagementapp.objects.Fair;
import ajoy.com.fairmanagementapp.task.TaskLoadFairs;

/**
 * Created by ajoy on 5/22/16.
 */
public class FragmentUpcomingFairs extends Fragment implements FairLoadedListener, SwipeRefreshLayout.OnRefreshListener{
    private static final String STATE_UPCOMING_FAIRS = "state_Upcoming_Fairs";
    protected ArrayList<Fair> mListFairs;
    private AdapterFairs mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerFairs;
    private TextView mTextError;
    private ProgressBar mProgressBar;

    public FragmentUpcomingFairs() {
        mListFairs = new ArrayList<>();
    }

    // TODO: Rename and change types and number of parameters
    public static FragmentUpcomingFairs newInstance(String param1, String param2) {
        FragmentUpcomingFairs fragment = new FragmentUpcomingFairs();
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

        mProgressBar = (ProgressBar) layout.findViewById(R.id.progressBar);
        mTextError = (TextView) layout.findViewById(R.id.tError);
        //L.t(getActivity(),"FragmentRunning: inside");
        if (savedInstanceState != null) {
            mListFairs = savedInstanceState.getParcelableArrayList(STATE_UPCOMING_FAIRS);
        } else {
//            mListFairs = MyApplication.getWritableDatabaseFair().readFairs(2);
//
//
//            if (mListFairs.isEmpty()) {
//                //L.T(getActivity(),"FragmentRunning: executing task from fragment");
                new TaskLoadFairs(this,2).execute();

            //}
        }
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
        outState.putParcelableArrayList(STATE_UPCOMING_FAIRS, mListFairs);
    }


    @Override
    public void onFairLoaded(ArrayList<Fair> listFairs) {
        mProgressBar.setVisibility(View.INVISIBLE);
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        if(listFairs==null)
        {
            mTextError.setText("Please check the connection.\nSwipe to refresh.");
            mTextError.setVisibility(View.VISIBLE);
            mListFairs=null;
            mAdapter.setFairs(mListFairs);
            return;
        }
        else if (listFairs.get(0).getId()==-1)
        {
            mTextError.setText("There is no upcoming fair available.");
            mTextError.setVisibility(View.VISIBLE);
            mListFairs=null;
            mAdapter.setFairs(mListFairs);
            return;
        }
        mTextError.setVisibility(View.INVISIBLE);
        mListFairs=listFairs;
        mAdapter.setFairs(listFairs);
    }

    @Override
    public void onRefresh() {
        //L.t(getActivity(), "Refreshing.....");
        mTextError.setVisibility(View.INVISIBLE);
        //load the whole feed again on refresh, dont try this at home :)
        new TaskLoadFairs(this,2).execute();

    }

    @Override
    public void onPause() {
        super.onPause();
        if(mProgressBar.getVisibility()==View.VISIBLE)
        {
            mProgressBar.setVisibility(View.INVISIBLE);
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
