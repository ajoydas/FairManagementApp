package ajoy.com.fairmanagementapp.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;

import ajoy.com.fairmanagementapp.extras.SortListener;
import ajoy.com.fairmanagementapp.fragments.FragmentDrawerSeller;
import ajoy.com.fairmanagementapp.fragments.FragmentStallViewDetails;
import ajoy.com.fairmanagementapp.fragments.FragmentStallViewProducts;
import ajoy.com.fairmanagementapp.application.R;
import ajoy.com.fairmanagementapp.objects.Stall;
import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

public class ActivityStallView extends AppCompatActivity implements MaterialTabListener, View.OnClickListener {

    public static final int TAB_DETAILS = 0;
    public static final int TAB_PRODUCTS = 1;
    public static final int TAB_COUNT = 2;
    private static final String TAG_SORT_NAME = "sortName";
    private static final String TAG_SORT_PRICE = "sortprice";
    private static final String TAG_SORT_AVAIL = "sortAvail";
    private static final int REQUEST_INVITE = 1;
    private Toolbar mToolbar;
    private ViewGroup mContainerToolbar;
    private MaterialTabHost mTabHost;
    private ViewPager mPager;
    private ViewPagerAdapter mAdapter;
    private FloatingActionButton mFAB;
    private FloatingActionMenu mFABMenu;
    private FragmentDrawerSeller mDrawerFragment;


    public static Stall stall;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stall= getIntent().getParcelableExtra("Information");

        setContentView(R.layout.activity_stall_view);
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        mContainerToolbar = (ViewGroup) findViewById(R.id.container_app_bar);
        //set the Toolbar as ActionBar
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //mToolbar.setNavigationIcon(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_arrow_back_black));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                finish();
            }
        });
        setupTabs();
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
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{ActivityMain.email});
                i.putExtra(Intent.EXTRA_SUBJECT, "Contact");
                i.putExtra(Intent.EXTRA_TEXT   , "Please write here");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(ActivityStallView.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.exit_menu:
                /*Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);*/
                //android.os.Process.killProcess(android.os.Process.myPid());
                //super.onDestroy();
                Intent intent = new Intent(this, ActivityMain.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("Exit me", true);
                startActivity(intent);
                finish();
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
        View focus = getCurrentFocus();
        if (focus != null) {
            hideKeyboard(focus);
        }
        mPager.setCurrentItem(materialTab.getPosition());
    }


    @Override
    public void onTabReselected(MaterialTab materialTab) {
        View focus = getCurrentFocus();
        if (focus != null) {
            hideKeyboard(focus);
        }
    }

    @Override
    public void onTabUnselected(MaterialTab materialTab) {
        View focus = getCurrentFocus();
        if (focus != null) {
            hideKeyboard(focus);
        }
    }
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onClick(View v) {
        //call instantiate item since getItem may return null depending on whether the PagerAdapter is of type FragmentPagerAdapter or FragmentStatePagerAdapter
        Fragment fragment = (Fragment) mAdapter.instantiateItem(mPager, mPager.getCurrentItem());
        if (fragment instanceof SortListener) {

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
        };


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
                case TAB_DETAILS:
                    fragment = FragmentStallViewDetails.newInstance("", "");
                    break;

                case TAB_PRODUCTS:
                    fragment = FragmentStallViewProducts.newInstance("", "");
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
            return getResources().getStringArray(R.array.tab_stall_view)[position];
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
