package ajoy.com.fairmanagementapp.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import ajoy.com.fairmanagementapp.extras.SortListener;
import ajoy.com.fairmanagementapp.fragments.FragmentDrawerFair;
import ajoy.com.fairmanagementapp.fragments.FragmentFairDetails;
import ajoy.com.fairmanagementapp.fragments.FragmentFavourites;
import ajoy.com.fairmanagementapp.fragments.FragmentSearchProducts;
import ajoy.com.fairmanagementapp.fragments.FragmentSearchStalls;
import ajoy.com.fairmanagementapp.logging.L;
import ajoy.com.fairmanagementapp.application.MyApplication;
import ajoy.com.fairmanagementapp.application.R;
import ajoy.com.fairmanagementapp.objects.Fair;
import ajoy.com.fairmanagementapp.objects.Stall;
import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

public class ActivityFair extends AppCompatActivity implements MaterialTabListener, View.OnClickListener {

    public static final int TAB_DETAILS = 0;
    public static final int TAB_PRODUCTS = 1;
    public static final int TAB_STALLS = 2;
    public static final int TAB_COUNT = 3;
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
    private FragmentDrawerFair mDrawerFragment;
    public static Fair fair;
    private static Stall stall;

    public static final String url = "jdbc:mysql://162.221.186.242:3306/buetian1_fairinfo";
    public static final String username = "buetian1_ajoy";
    public static final String password = "termjan2016";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fair = (Fair) getIntent().getParcelableExtra("Information");
        stall = new Stall();
        setContentView(R.layout.activity_fair);
        setupFAB();
        setupTabs();
        setupDrawer();

    }

    private void setupDrawer() {
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        mContainerToolbar = (ViewGroup) findViewById(R.id.container_app_bar);
        //set the Toolbar as ActionBar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //setup the NavigationDrawer
        mDrawerFragment = (FragmentDrawerFair)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer_fair);
        mDrawerFragment.setUp(R.id.fragment_navigation_drawer_fair, (DrawerLayout) findViewById(R.id.drawer_layout_fair), mToolbar);
    }


    ProgressDialog loading;

    private String user = "", pass = "", response = "";

    public void onDrawerItemClicked(int index) {
        if (index == 0) {
            dialogShow();
        }
        else if (index == 1) {
            startActivity(new Intent(this, ActivityFavourites.class));
        }
        else if (index == 5) {
            startActivity(new Intent(this, ActivityAbout.class));
        } else {
            mPager.setCurrentItem(index - 2);
        }
    }

    private void dialogShow() {
        final Dialog dialog = new Dialog(ActivityFair.this);
        dialog.setTitle("Seller Sign In");
        dialog.setContentView(R.layout.dialog_signin);
        dialog.show();

        final EditText usernameInput = (EditText) dialog.findViewById(R.id.username);
        final EditText passwordInput = (EditText) dialog.findViewById(R.id.password);
        Button bsignin = (Button) dialog.findViewById(R.id.bsignin);
        Button bcancel = (Button) dialog.findViewById(R.id.bcancel);
        bsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //original
                user = usernameInput.getText().toString();
                pass = passwordInput.getText().toString();

               /* user="stall1";
                pass="stall1";*/

                new Mytask().execute();
                dialog.cancel();
            }
        });

        bcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                L.t(getApplicationContext(), "Request Canceled");
                dialog.cancel();
            }
        });
    }


    private class Mytask extends AsyncTask<Void, Void, Integer> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(ActivityFair.this, "Signing In", "Please wait...", true, true);
            System.out.println(user + pass);
        }

        @Override
        protected Integer doInBackground(Void... params) {

            try {

                //PreparedStatement preparedStatement=con.prepareStatement("Select password from  "+fair.getDb_name()+"_users where username=?");
                //preparedStatement.setString(1,user);

                URL loadProductUrl = new URL("http://buetian14.com/fairmanagementapp/loginStall.php");
                HttpURLConnection httpURLConnection = (HttpURLConnection) loadProductUrl.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String db = fair.getDb_name() + "_users";
                String data = URLEncoder.encode("db_table", "UTF-8") + "=" + URLEncoder.encode(db, "UTF-8") + "&" +
                        URLEncoder.encode("login_name", "UTF-8") + "=" + URLEncoder.encode(user, "UTF-8") + "&" +
                        URLEncoder.encode("login_pass", "UTF-8") + "=" + URLEncoder.encode(pass, "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                if ((line = bufferedReader.readLine()) != null) {
                    System.out.println(line);
                    response += line;
                    inputStream.close();
                    bufferedReader.close();
                    httpURLConnection.disconnect();
                    if (response.contains("Success")) {
                        String st = "Select * from  " + fair.getDb_name() + "_stalls where stall='" + user + "'";
                        System.out.println(st);
                        /*if (rowcount == 0) {
                            L.T(getApplicationContext(), "Login Successful But User Information Not Found In Database!");
                            connect.close();
                            return 3;
                        }*/

                        URL loadStallUrl = new URL("http://buetian14.com/fairmanagementapp/loadStalls.php");
                        HttpURLConnection httpURLConnection2 = (HttpURLConnection) loadStallUrl.openConnection();
                        httpURLConnection2.setRequestMethod("POST");
                        httpURLConnection2.setDoOutput(true);
                        httpURLConnection2.setDoInput(true);
                        OutputStream outputStream2 = httpURLConnection2.getOutputStream();
                        BufferedWriter bufferedWriter2 = new BufferedWriter(new OutputStreamWriter(outputStream2, "UTF-8"));
                        String data2 = URLEncoder.encode("statement", "UTF-8") + "=" + URLEncoder.encode(st, "UTF-8");
                        bufferedWriter2.write(data2);
                        bufferedWriter2.flush();
                        bufferedWriter2.close();
                        outputStream2.close();

                        InputStream inputStream2 = httpURLConnection2.getInputStream();
                        BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(inputStream2));
                        StringBuilder stringBuilder = new StringBuilder();
                        String jsonString;
                        while ((jsonString = bufferedReader2.readLine()) != null) {
                            stringBuilder.append(jsonString + "\n");

                            System.out.println(jsonString);
                        }
                        bufferedReader2.close();
                        inputStream2.close();
                        httpURLConnection2.disconnect();
                        JSONObject jsonObject = new JSONObject(stringBuilder.toString().trim());
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        int count = 0;

                        JSONObject rs = jsonArray.getJSONObject(count);
                        stall = new Stall();
                        stall.setId(rs.getInt("id"));
                        stall.setStall(rs.getString("stall"));
                        stall.setStall_name(rs.getString("stall_name"));
                        stall.setOwner(rs.getString("owner"));
                        stall.setDescription(rs.getString("description"));
                        stall.setLocation(rs.getString("location"));
                        System.out.println(stall);


                        return 1;
                    } else if (response.contains("Failed")) {
                        return 2;
                    }

                }
                else
                {
                    inputStream.close();
                    bufferedReader.close();
                    httpURLConnection.disconnect();
                }
                return 0;

            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }

        @Override
        protected void onPostExecute(Integer value) {
            super.onPostExecute(value);
            loading.dismiss();

            if (value == 1) {

                L.t(getApplicationContext(), "Login Successfull!");
                Intent i = new Intent(MyApplication.getAppContext(), ActivitySeller.class);
                i.putExtra("Information", stall);
                startActivity(i);
            } else if (value == 2) {
                //Toast.makeText(getApplicationContext(), "Login failed!",Toast.LENGTH_LONG).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityFair.this);
                builder.setTitle("Sign In Failed!");
                builder.setMessage("The password you entered in wrong. Try again?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        dialogShow();
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
            } else if (value == 0) {
                //L.t(getApplicationContext(), "User Not Found or Check Connection");
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityFair.this);
                builder.setTitle("Sign In Failed!");
                builder.setMessage("The Username you entered is not found Or Connection to the database not established. Try again?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        dialogShow();
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
        }
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
        buttonSortDate.setTag(TAG_SORT_PRICE);
        buttonSortRatings.setTag(TAG_SORT_AVAIL);

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
        int id = item.getItemId();

        if (id == R.id.about) {
            startActivity(new Intent(this, ActivityAbout.class));
            return true;
        }


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
            if (v.getTag().equals(TAG_SORT_PRICE)) {
                //call the sort by date method on any Fragment that implements sortlistener
                ((SortListener) fragment).onSortByPrice();
            }
            if (v.getTag().equals(TAG_SORT_AVAIL)) {
                //call the sort by ratings method on any Fragment that implements sortlistener
                ((SortListener) fragment).onSortByAvailability();
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

    public void fairmapClicked(View view) {
        Intent i = new Intent(getApplicationContext(), ActivityFairMapView.class);
        i.putExtra("Url", fair.getMap_address());
        startActivity(i);
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

        public Fragment getItem(int num) {
            Fragment fragment = null;
            switch (num) {
                case TAB_DETAILS:
                    fragment = FragmentFairDetails.newInstance("", "");
                    break;
                case TAB_PRODUCTS:
                    fragment = FragmentSearchProducts.newInstance("", "");
                    break;
                case TAB_STALLS:
                    fragment = FragmentSearchStalls.newInstance("", "");
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
            return getResources().getStringArray(R.array.tab_fair)[position];
        }

        private Drawable getIcon(int position) {
            return getResources().getDrawable(icons[position]);
        }
    }

}
