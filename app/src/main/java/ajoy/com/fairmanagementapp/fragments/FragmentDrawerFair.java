package ajoy.com.fairmanagementapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ajoy.com.fairmanagementapp.activities.ActivityFair;
import ajoy.com.fairmanagementapp.adapters.AdapterDrawer;
import ajoy.com.fairmanagementapp.application.MyApplication;
import ajoy.com.fairmanagementapp.application.R;
import ajoy.com.fairmanagementapp.objects.Information;

/**
 * Created by ajoy on 5/22/16.
 */
public class FragmentDrawerFair extends Fragment  {
    public static final String KEY_USER_LEARNED_DRAWER = "user_learned_drawer";
    private RecyclerView mRecyclerDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private AdapterDrawer mAdapter;
    private boolean mUserLearnedDrawer;
    private boolean mFromSavedInstanceState;
    private View mContainer;
    private boolean mDrawerOpened = false;

    public FragmentDrawerFair() {
    }

    public List<Information> getData() {

        List<Information> data = new ArrayList<>();
        int[] icons = {R.drawable.ic_login,R.drawable.ic_favorite,R.drawable.ic_details,R.drawable.ic_products,R.drawable.ic_stalls,R.drawable.ic_about,R.drawable.ic_employee};
        String[] titles = getResources().getStringArray(R.array.drawer_fair_tabs);
        for (int i = 0; i < titles.length; i++) {
            Information information = new Information();
            information.title = titles[i];
            information.iconId = icons[i];
            data.add(information);
        }
        return data;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserLearnedDrawer = MyApplication.readFromPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, false);
        mFromSavedInstanceState = savedInstanceState != null ? true : false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_navigation_drawer_fair, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mRecyclerDrawer = (RecyclerView) view.findViewById(R.id.drawerList);
        mAdapter = new AdapterDrawer(getActivity(), getData());
        mRecyclerDrawer.setAdapter(mAdapter);
        mRecyclerDrawer.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerDrawer.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), mRecyclerDrawer, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
                ((ActivityFair) getActivity()).onDrawerItemClicked(position - 1);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        mContainer = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!mUserLearnedDrawer) {
                    mUserLearnedDrawer = true;
                    MyApplication.saveToPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, mUserLearnedDrawer);
                }
                getActivity().supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                ((ActivityFair) getActivity()).onDrawerSlide(slideOffset);
                toolbar.setAlpha(1 - slideOffset / 2);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
                if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
                    mDrawerLayout.openDrawer(mContainer);
                }
            }
        });


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
