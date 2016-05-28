package ajoy.com.fairmanagementapp.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import ajoy.com.fairmanagementapp.anim.AnimationUtils;
import ajoy.com.fairmanagementapp.extras.SortListener;
import ajoy.com.fairmanagementapp.fragments.FragmentDrawerSeller;
import ajoy.com.fairmanagementapp.fragments.FragmentStallDetails;
import ajoy.com.fairmanagementapp.fragments.FragmentStallProducts;
import ajoy.com.fairmanagementapp.fragments.FragmentUpcoming;
import ajoy.com.fairmanagementapp.logging.L;
import ajoy.com.fairmanagementapp.materialtest.R;
import ajoy.com.fairmanagementapp.pojo.Stall;
import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;
import me.tatarka.support.job.JobScheduler;

/**
 * Created by ajoy on 5/22/16.
 */
public class ActivitySeller extends AppCompatActivity implements MaterialTabListener, View.OnClickListener{
    //int representing our 0th tab corresponding to the Fragment where search results are dispalyed
    public static final int TAB_SEARCH_RESULTS = 0;
    //int corresponding to our 1st tab corresponding to the Fragment where box office hits are dispalyed
    public static final int TAB_HITS = 1;
    //int corresponding to our 2nd tab corresponding to the Fragment where upcoming movies are displayed
    public static final int TAB_UPCOMING = 2;
    //int corresponding to the number of tabs in our Activity
    public static final int TAB_COUNT = 3;
    //int corresponding to the id of our JobSchedulerService
    private static final int JOB_ID = 100;
    //tag associated with the FAB menu button that sorts by name
    private static final String TAG_SORT_NAME = "sortName";
    //tag associated with the FAB menu button that sorts by date
    private static final String TAG_SORT_DATE = "sortDate";
    //tag associated with the FAB menu button that sorts by ratings
    private static final String TAG_SORT_RATINGS = "sortRatings";
    //Run the JobSchedulerService every 2 minutes
    private static final long POLL_FREQUENCY = 28800000;
    private JobScheduler mJobScheduler;
    private Toolbar mToolbar;
    //a layout grouping the toolbar and the tabs together
    private ViewGroup mContainerToolbar;
    private MaterialTabHost mTabHost;
    private ViewPager mPager;
    private ViewPagerAdapter mAdapter;
    private FloatingActionButton mFAB;
    private FloatingActionMenu mFABMenu;
    private FragmentDrawerSeller mDrawerFragment;


    ProgressDialog loading;

    private static final String url = "jdbc:mysql://192.168.0.101:3306/";
    private static final String username="ajoy";
    private static final String password="ajoydas";
    private String stallname;
    private String stallowner;
    private String stalldescription;


    public static Stall stall;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stall=(Stall)getIntent().getParcelableExtra("Information");

        /*LayoutInflater inflater = null;
        View view=inflater.inflate(R.layout.drawer_header, null,false);*/

        setContentView(R.layout.activity_seller);
        //setupFAB();
        setupTabs();
        //setupJob();
        setupDrawer();
        //animate the Toolbar when it comes into the picture
        AnimationUtils.animateToolbarDroppingDown(mContainerToolbar);

    }


    public void update()
    {
        TextView title=(TextView)findViewById(R.id.detailsstallname);
        title.setText(ActivitySeller.stall.getStall_name());
        TextView organizer=(TextView)findViewById(R.id.detailsstallowner);
        organizer.setText(ActivitySeller.stall.getOwner());
        TextView location=(TextView)findViewById(R.id.detailsstalldescription);
        location.setText(ActivitySeller.stall.getDescription());
    }


    public void stallMapClicked(View view) {
        Intent i = new Intent(ActivitySeller.this, ActivityStallMap.class);
        i.putExtra("Information",ActivitySeller.stall.getLocation());
        startActivity(i);
    }

    public void editDetailsClicked(View view) {
        editDialogShow();
        L.t(ActivitySeller.this,"Edit button clicked");
        update();
    }

    private void editDialogShow() {
        final Dialog dialog = new Dialog(ActivitySeller.this);
        dialog.setTitle("Seller Details Update");
        dialog.setContentView(R.layout.dialog_update_stall_details);
        final EditText updatename = (EditText) dialog.findViewById(R.id.updatestallname);
        final EditText updateowner = (EditText) dialog.findViewById(R.id.updatestallowner);
        final EditText updatedescription = (EditText) dialog.findViewById(R.id.updatestalldescription);
        updatename.setText(ActivitySeller.stall.getStall_name());
        updateowner.setText(ActivitySeller.stall.getOwner());
        updatedescription.setText(ActivitySeller.stall.getDescription());

        dialog.show();
        Button bsave= (Button) dialog.findViewById(R.id.bsave);
        Button bcancel= (Button) dialog.findViewById(R.id.bcancel);

        bsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //original
                stallname=updatename.getText().toString();
                stallowner=updateowner.getText().toString();
                stalldescription = updatedescription.getText().toString();

                new Mytask().execute();
                dialog.cancel();
            }
        });

        bcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                L.t(ActivitySeller.this, "Request Canceled");
                dialog.cancel();
            }
        });
    }

    /*@Override
    public void onClick(View v) {

        if (v==layout.findViewById(R.id.bedit))
        {
            editDetailsClicked(v);
        }
    }
*/
    private class Mytask extends AsyncTask<Void,Void,Integer>
    {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(ActivitySeller.this, "Updating Information", "Please wait...",true,true);
        }

        @Override
        protected Integer doInBackground(Void... params) {

            try {
                Class.forName("com.mysql.jdbc.Driver");
                String Url=url+ActivityFair.fair.getDb_name();

                System.out.println(ActivityFair.fair.getDb_name());
                Connection con= DriverManager.getConnection(Url,username,password);

                System.out.println("Connected");

                PreparedStatement preparedStatement=con.prepareStatement("update stalls set stall_name=?,owner=?,description=? where stall=?");
                preparedStatement.setString(1,stallname);
                preparedStatement.setString(2,stallowner);
                preparedStatement.setString(3,stalldescription);
                preparedStatement.setString(4,ActivitySeller.stall.getStall());

                System.out.println("Statement");
                int rs=0;
                //preparedStatement.setString(1,user);
                rs=preparedStatement.executeUpdate();

                System.out.println("Executed");

                System.out.println("Count: "+rs);

                if(rs==1) {
                    ActivitySeller.stall.setStall_name(stallname);
                    ActivitySeller.stall.setOwner(stallowner);
                    ActivitySeller.stall.setDescription(stalldescription);

                    System.out.println(ActivitySeller.stall);
                    return 1;
                }

                return 0;
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                return 0;
            }
        }

        @Override
        protected void onPostExecute(Integer value) {
            super.onPostExecute(value);
            loading.dismiss();

            L.T(ActivitySeller.this,String.valueOf(value));

            if(value==1) {
                L.t(ActivitySeller.this, "Updated Successfully!");
                update();
            }
            else if(value==0) {
                //Toast.makeText(getApplicationContext(), "Login failed!",Toast.LENGTH_LONG).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivitySeller.this);
                builder.setTitle("Update Failed!");
                builder.setMessage("Connection lost. Try again?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        editDialogShow();
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

                //L.t(getApplicationContext(), "Password Wrong");
            }
        }
    }
    
    

    private void setupDrawer() {
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        mContainerToolbar = (ViewGroup) findViewById(R.id.container_app_bar);
        //set the Toolbar as ActionBar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //setup the NavigationDrawer
        mDrawerFragment = (FragmentDrawerSeller)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer_seller);
        mDrawerFragment.setUp(R.id.fragment_navigation_drawer_seller, (DrawerLayout) findViewById(R.id.drawer_layout_seller), mToolbar);
    }


    public void onDrawerItemClicked(int index) {
        if (index == 0) {
            startActivity(new Intent(this, ActivityTouchEvent.class));
        } else {
            mPager.setCurrentItem(index-1);
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

        //setting floating button invisible
        //mFAB.setVisibility(View.INVISIBLE);
    }
    //.setIcon(mAdapter.getIcon(i))
    /*private void setupJob() {
        mJobScheduler = JobScheduler.getInstance(this);
        //set an initial delay with a Handler so that the data loading by the JobScheduler does not clash with the loading inside the Fragment
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //schedule the job after the delay has been elapsed
                buildJob();
            }
        }, 30000);
    }

    private void buildJob() {
        //attach the job ID and the name of the Service that will work in the background
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, new ComponentName(this, ServiceMoviesBoxOffice.class));
        //set periodic polling that needs net connection and works across device reboots
        builder.setPeriodic(POLL_FREQUENCY)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPersisted(true);
        mJobScheduler.schedule(builder.build());
    }*/

    private void setupFAB() {
        //define the icon for the main floating action button
        ImageView iconFAB = new ImageView(this);
        iconFAB.setImageResource(R.drawable.ic_action_new);

        //set the appropriate background for the main floating action button along with its icon
        mFAB = new FloatingActionButton.Builder(this)
                .setContentView(iconFAB)
                .setBackgroundDrawable(R.drawable.selector_button_red)
                .build();

        //define the icons for the sub action buttons
        ImageView iconSortName = new ImageView(this);
        iconSortName.setImageResource(R.drawable.ic_action_alphabets);
        ImageView iconSortDate = new ImageView(this);
        iconSortDate.setImageResource(R.drawable.ic_action_calendar);
        ImageView iconSortRatings = new ImageView(this);
        iconSortRatings.setImageResource(R.drawable.ic_action_important);

        //set the background for all the sub buttons
        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
        itemBuilder.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_sub_button_gray));


        //build the sub buttons
        SubActionButton buttonSortName = itemBuilder.setContentView(iconSortName).build();
        SubActionButton buttonSortDate = itemBuilder.setContentView(iconSortDate).build();
        SubActionButton buttonSortRatings = itemBuilder.setContentView(iconSortRatings).build();

        //to determine which button was clicked, set Tags on each button
        buttonSortName.setTag(TAG_SORT_NAME);
        buttonSortDate.setTag(TAG_SORT_DATE);
        buttonSortRatings.setTag(TAG_SORT_RATINGS);

        buttonSortName.setOnClickListener(this);
        buttonSortDate.setOnClickListener(this);
        buttonSortRatings.setOnClickListener(this);

        //add the sub buttons to the main floating action button
        mFABMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(buttonSortName)
                .addSubActionView(buttonSortDate)
                .addSubActionView(buttonSortRatings)
                .attachTo(mFAB)
                .build();

        //mFAB.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            L.m("Settings selected");
            return true;
        }
        if (id == R.id.action_touch_intercept_activity) {
            startActivity(new Intent(this, ActivityTouchEvent.class));
        }

        /*if (R.id.action_activity_calling == id) {
            startActivity(new Intent(this, ActivityA.class));
        }*/
        /*if (R.id.action_shared_transitions == id) {
            startActivity(new Intent(this, ActivitySharedA.class));
        }*/
        /*if (R.id.action_tabs_using_library == id) {
            startActivity(new Intent(this, ActivitySlidingTabLayout.class));
        }*/
        /*if (R.id.action_vector_test_activity == id) {
            startActivity(new Intent(this, ActivityVectorDrawable.class));
        }*/

       /* if (R.id.action_dynamic_tabs_activity == id) {
            startActivity(new Intent(this, ActivityDynamicTabs.class));
        }*/
        /*if (R.id.action_recycler_item_animations == id) {
            startActivity(new Intent(this, ActivityRecylerAnimators.class));
        }*/
        return super.onOptionsItemSelected(item);
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

            if (v.getTag().equals(TAG_SORT_NAME)) {
                //call the sort by name method on any Fragment that implements sortlistener
                ((SortListener) fragment).onSortByName();
            }
            if (v.getTag().equals(TAG_SORT_DATE)) {
                //call the sort by date method on any Fragment that implements sortlistener
                ((SortListener) fragment).onSortByDate();
            }
            if (v.getTag().equals(TAG_SORT_RATINGS)) {
                //call the sort by ratings method on any Fragment that implements sortlistener
                ((SortListener) fragment).onSortByRating();
            }
        }

    }


    private void toggleTranslateFAB(float slideOffset) {
        if (mFABMenu != null) {
            if (mFABMenu.isOpen()) {
                mFABMenu.close(true);
            }
            mFAB.setTranslationX(slideOffset * 200);
        }
    }

    public void onDrawerSlide(float slideOffset) {
        toggleTranslateFAB(slideOffset);
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {


        //icons of the tabs can be changed from here
        int icons[] = {R.drawable.ic_action_search,
                R.drawable.ic_action_trending,
                R.drawable.ic_action_upcoming};


        FragmentManager fragmentManager;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            fragmentManager = fm;
        }

        @Override
        public Fragment getItem(int num) {
            Fragment fragment = null;
//            L.m("getItem called for " + num);
            switch (num) {
                case TAB_SEARCH_RESULTS:
                    fragment = FragmentStallDetails.newInstance("", "");
                    break;

                case TAB_HITS:
                    fragment = FragmentStallProducts.newInstance("", "");
                    break;
                case TAB_UPCOMING:
                    fragment = FragmentUpcoming.newInstance("", "");
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
            return getResources().getStringArray(R.array.tab_seller)[position];
        }

        private Drawable getIcon(int position) {
            return getResources().getDrawable(icons[position]);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
