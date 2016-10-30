package ajoy.com.fairmanagementapp.activities;


import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;

import ajoy.com.fairmanagementapp.anim.AnimationUtils;
import ajoy.com.fairmanagementapp.application.BuildConfig;
import ajoy.com.fairmanagementapp.extras.SortListener;
import ajoy.com.fairmanagementapp.fragments.FragmentDrawer;
import ajoy.com.fairmanagementapp.fragments.FragmentFavourites;
import ajoy.com.fairmanagementapp.fragments.FragmentRunningFairs;
import ajoy.com.fairmanagementapp.fragments.FragmentUpcomingFairs;
import ajoy.com.fairmanagementapp.application.R;
import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;


public class ActivityMain extends AppCompatActivity implements MaterialTabListener, View.OnClickListener {

    public static final int TAB_RUNNING = 0;
    public static final int TAB_UPCOMING = 1;
    public static final int TAB_COUNT = 2;
    private static final String TAG_SORT_NAME = "sortName";
    private static final String TAG_SORT_PRICE = "sortprice";
    private static final String TAG_SORT_AVAIL = "sortAvail";
    private Toolbar mToolbar;
    private ViewGroup mContainerToolbar;
    private MaterialTabHost mTabHost;
    private ViewPager mPager;
    private ViewPagerAdapter mAdapter;
    private FloatingActionButton mFAB;
    private FloatingActionMenu mFABMenu;
    private FragmentDrawer mDrawerFragment;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    public static String Server="";
    public static long Count=20;
    public static String email;
    private static final int REQUEST_INVITE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            if (getIntent().getBooleanExtra("Exit me", false)) {
                finish();
                return; // add this to prevent from doing unnecessary stuffs
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        Server=mFirebaseRemoteConfig.getString("Server");
        Count=mFirebaseRemoteConfig.getLong("Count");
        email=mFirebaseRemoteConfig.getString("Email");

        mFirebaseRemoteConfig.fetch(0)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //Toast.makeText(StartActivity.this, "Fetch Successfull", Toast.LENGTH_SHORT).show();
                            mFirebaseRemoteConfig.activateFetched();
                        } else {
                            //Toast.makeText(StartActivity.this, "Fetch Failed", Toast.LENGTH_SHORT).show();
                        }
                        Server=mFirebaseRemoteConfig.getString("Server");
                        Count=mFirebaseRemoteConfig.getLong("Count");
                        email=mFirebaseRemoteConfig.getString("Email");
                    }
                });


        setupTabs();
        setupDrawer();
        //animate the Toolbar when it comes into the picture
        AnimationUtils.animateToolbarDroppingDown(mContainerToolbar);

    }

    private void setupDrawer() {
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        mContainerToolbar = (ViewGroup) findViewById(R.id.container_app_bar);
        //set the Toolbar as ActionBar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //setup the NavigationDrawer
        mDrawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        mDrawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
    }


    public void onDrawerItemClicked(int index) {
        if (index == 2) {
            startActivity(new Intent(this, ActivityFavourites.class));
        }
        else if (index == 3) {
            startActivity(new Intent(this, ActivityAbout.class));
        }
        else if (index == 4) {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL  , new String[]{email});
            i.putExtra(Intent.EXTRA_SUBJECT, "Contact");
            i.putExtra(Intent.EXTRA_TEXT   , "Please write here");
            try {
                startActivity(Intent.createChooser(i, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(ActivityMain.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            mPager.setCurrentItem(index);
        }
    }


    public View getContainerToolbar() {
        return mContainerToolbar;
    }

    private void setupTabs() {
        mTabHost = (MaterialTabHost) findViewById(R.id.materialTabHost);
        mPager = (ViewPager) findViewById(R.id.viewPager);
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);
        //when the page changes in the ViewPager, update the Tabs accordingly
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mTabHost.setSelectedNavigationItem(position);

            }
        });
        //Add all the Tabs to the TabHost
        for (int i = 0; i < mAdapter.getCount(); i++) {
            mTabHost.addTab(
                    mTabHost.newTab()
                            .setText(mAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present. 
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.invite_menu:
                sendInvitation();
                return true;
            case R.id.about_menu:
                startActivity(new Intent(this, ActivityAbout.class));
                return true;
            case R.id.contact_menu:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{email});
                i.putExtra(Intent.EXTRA_SUBJECT, "Contact");
                i.putExtra(Intent.EXTRA_TEXT   , "Please write here");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(ActivityMain.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.exit_menu:
                /*Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);*/
                android.os.Process.killProcess(android.os.Process.myPid());
                super.onDestroy();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void sendInvitation() {
        Intent intent = new AppInviteInvitation.IntentBuilder(" Invitation")
                .setMessage("Please install Fair Files (a fair management app)")
                .setCallToActionText("Call to action")
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }

    @Override
    public void onTabSelected(MaterialTab materialTab) {
        //when a Tab is selected, update the ViewPager to reflect the changes
        mPager.setCurrentItem(materialTab.getPosition());
    }


    @Override
    public void onTabReselected(MaterialTab materialTab) {
    }

    @Override
    public void onTabUnselected(MaterialTab materialTab) {
    }

    @Override
    public void onClick(View v) {
        //call instantiate item since getItem may return null depending on whether the PagerAdapter is of type FragmentPagerAdapter or FragmentStatePagerAdapter
        Fragment fragment = (Fragment) mAdapter.instantiateItem(mPager, mPager.getCurrentItem());
        if (fragment instanceof SortListener) {

        }

    }


    public void onDrawerSlide(float slideOffset) {

    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {


        //icons of the tabs can be changed from here
        int icons[] = {R.drawable.ic_action_search,
                R.drawable.ic_action_trending,
                //R.drawable.ic_action_upcoming
                };


        FragmentManager fragmentManager;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            fragmentManager = fm;
        }

        public Fragment getItem(int num) {
            Fragment fragment = null;
            switch (num) {
                case TAB_RUNNING:
                    fragment = FragmentRunningFairs.newInstance("", "");
                    break;

                case TAB_UPCOMING:
                    fragment = FragmentUpcomingFairs.newInstance("", "");
                    break;
            }
            return fragment;

        }

        @Override
        public int getCount() {
            return TAB_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getResources().getStringArray(R.array.tab_main)[position];
        }

        private Drawable getIcon(int position) {
            return getResources().getDrawable(icons[position]);
        }
    }

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            android.os.Process.killProcess(android.os.Process.myPid());
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}