package ajoy.com.fairmanagementapp.activities;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import ajoy.com.fairmanagementapp.application.BuildConfig;
import ajoy.com.fairmanagementapp.application.MyApplication;
import ajoy.com.fairmanagementapp.callbacks.EmployeeLoadedListener;
import ajoy.com.fairmanagementapp.callbacks.SellLoadedListener;
import ajoy.com.fairmanagementapp.extras.SortListener;
import ajoy.com.fairmanagementapp.fragments.FragmentDrawerSeller;
import ajoy.com.fairmanagementapp.fragments.FragmentEmployees;
import ajoy.com.fairmanagementapp.fragments.FragmentSells;
import ajoy.com.fairmanagementapp.fragments.FragmentStallDetails;
import ajoy.com.fairmanagementapp.fragments.FragmentStallProducts;
import ajoy.com.fairmanagementapp.logging.L;
import ajoy.com.fairmanagementapp.application.R;
import ajoy.com.fairmanagementapp.objects.Employee;
import ajoy.com.fairmanagementapp.objects.Sell;
import ajoy.com.fairmanagementapp.objects.Stall;
import ajoy.com.fairmanagementapp.task.TaskLoadEmployees;
import ajoy.com.fairmanagementapp.task.TaskLoadSells;
import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

/**
 * Created by ajoy on 5/22/16.
 */
public class ActivitySeller extends AppCompatActivity implements EmployeeLoadedListener,SellLoadedListener,MaterialTabListener, View.OnClickListener{
    public static final int TAB_DETAILS = 0;
    public static final int TAB_PRODUCTS = 1;
    public static final int TAB_EMPLOYEES = 2;
    public static final int TAB_SELLS = 3;
    public static final int TAB_COUNT = 4;
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
    private FragmentDrawerSeller mDrawerFragment;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 123;
    private Intent callIntent;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    ProgressDialog loading;

    private String stallname;
    private String stallowner;
    private String stalldescription;
    private String contactnum;
    private String email;

    public static Stall stall;
    private String newpass1;
    private String newpass2;
    private String oldpass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stall=(Stall)getIntent().getParcelableExtra("Information");


        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        contactnum=mFirebaseRemoteConfig.getString("ContactNumber");
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
                        contactnum=mFirebaseRemoteConfig.getString("ContactNumber");
                        email=mFirebaseRemoteConfig.getString("Email");
                    }
                });


        setContentView(R.layout.activity_seller);
        setupTabs();
        setupDrawer();

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
    ProgressDialog pdfDialog=null;
    public void saveEmployeesClicked(View view) {
        pdfDialog = new ProgressDialog(ActivitySeller.this);
        pdfDialog.setTitle("Saving as Pdf...");
        pdfDialog.show();
        new TaskLoadEmployees(this,ActivityFair.fair.getDb_name(), ActivitySeller.stall.getStall(),null).execute();
    }

    @Override
    public void onEmployeeLoaded(ArrayList<Employee> listEmployees) {
        BaseFont unicode = null;
        try {
            unicode = BaseFont.createFont("assets/kalpurush.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font font = new Font(unicode);
        Document doc = new Document();
//output file path
        String outpath = Environment.getExternalStorageDirectory() + "/EmployeesDetail"+System.currentTimeMillis()+".pdf";
//create pdf writer instance
            PdfWriter.getInstance(doc, new FileOutputStream(outpath));
//open the document for writing
            doc.open();
//add paragraph to the document
            PdfPTable table =new PdfPTable(5);
            PdfPCell cell =new PdfPCell(new Paragraph("Employees Detail:"));
            cell.setColspan(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            table.addCell("Name:");
            table.addCell("Description:");
            table.addCell("Contact No:");
            table.addCell("Position:");
            table.addCell("Salary:");

            for(int i=0;i<listEmployees.size();i++)
            {
                table.addCell(new Paragraph(listEmployees.get(i).getName(),font));
                table.addCell(new Paragraph(listEmployees.get(i).getDescription(),font));
                table.addCell(new Paragraph(listEmployees.get(i).getContact_no(),font));
                table.addCell(new Paragraph(listEmployees.get(i).getPosition(),font));
                table.addCell(new Paragraph(listEmployees.get(i).getSalary(),font));
            }

            doc.add(table);
//close the document
            doc.close();
            L.t(this,"Saved Succefully");

        } catch (Exception e) {
            e.printStackTrace();
            L.t(this,"Saving failed! Try again.");
        }
        pdfDialog.dismiss();
    }


    public void saveSellsClicked(View view) {
        pdfDialog = new ProgressDialog(ActivitySeller.this);
        pdfDialog.setTitle("Saving as Pdf...");
        pdfDialog.show();
        new TaskLoadSells(this,ActivityFair.fair.getDb_name(), ActivitySeller.stall.getStall(),null).execute();
    }


    @Override
    public void onSellLoaded(ArrayList<Sell> listSells) {
        BaseFont unicode = null;
        try {
            unicode = BaseFont.createFont("assets/kalpurush.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font font = new Font(unicode);
            Document doc = new Document();
//output file path
            String outpath = Environment.getExternalStorageDirectory() + "/SellsDetail"+System.currentTimeMillis()+".pdf";
//create pdf writer instance
            PdfWriter.getInstance(doc, new FileOutputStream(outpath));
//open the document for writing
            doc.open();
//add paragraph to the document
            PdfPTable table =new PdfPTable(6);
            PdfPCell cell =new PdfPCell(new Paragraph("Sells Detail:"));
            cell.setColspan(6);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            table.addCell("Product:");
            table.addCell("Employee:");
            table.addCell("Date:");
            table.addCell("Time:");
            table.addCell("Price:");
            table.addCell("Description:");

            for(int i=0;i<listSells.size();i++)
            {
                table.addCell(new Paragraph(listSells.get(i).getProduct_name(),font));
                table.addCell(new Paragraph(listSells.get(i).getEmployee_name(),font));
                table.addCell(new Paragraph(listSells.get(i).getDate(),font));
                table.addCell(new Paragraph(listSells.get(i).getTime(),font));
                table.addCell(new Paragraph(listSells.get(i).getPrice(),font));
                table.addCell(new Paragraph(listSells.get(i).getDescription(),font));
            }

            doc.add(table);
//close the document
            doc.close();
            L.t(this,"Saved Successfully");

        } catch (Exception e) {
            e.printStackTrace();
            L.t(this,"Saving failed! Try again.");
        }
        pdfDialog.dismiss();
    }



    public void stallMapClicked(View view) {
        Intent i = new Intent(ActivitySeller.this, ActivityStallMap.class);
        i.putExtra("Location",ActivitySeller.stall.getLocation());
        i.putExtra("Stallname",ActivitySeller.stall.getStall_name());

        startActivity(i);
    }

    public void editDetailsClicked(View view) {
        editDialogShow();
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
                URL loadProductUrl = new URL(ActivityMain.Server+"updateStall.php");
                HttpURLConnection httpURLConnection = (HttpURLConnection) loadProductUrl.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String db = ActivityFair.fair.getDb_name() + "_stalls";
                String data = URLEncoder.encode("db_table", "UTF-8") + "=" + URLEncoder.encode(db, "UTF-8") + "&" +
                        URLEncoder.encode("stall_name", "UTF-8") + "=" + URLEncoder.encode(stallname, "UTF-8") + "&" +
                        URLEncoder.encode("stall_owner", "UTF-8") + "=" + URLEncoder.encode(stallowner, "UTF-8") + "&" +
                        URLEncoder.encode("stall_description", "UTF-8") + "=" + URLEncoder.encode(stalldescription, "UTF-8") + "&" +
                        URLEncoder.encode("stall", "UTF-8") + "=" + URLEncoder.encode(ActivitySeller.stall.getStall(), "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                String response="";
                if ((line = bufferedReader.readLine()) != null) {
                    System.out.println(line);
                    response += line;
                    inputStream.close();
                    bufferedReader.close();
                    httpURLConnection.disconnect();
                    if (response.contains("Success")) {
                        ActivitySeller.stall.setStall_name(stallname);
                        ActivitySeller.stall.setOwner(stallowner);
                        ActivitySeller.stall.setDescription(stalldescription);

                        System.out.println(ActivitySeller.stall);
                        return 1;
                    } else if (response.contains("Failed")) {
                        return 0;
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
            finish();
        }
        else if(index==1)
        {
            dialogShow();
        }
        else if (index == 6) {
            startActivity(new Intent(this, ActivityAbout.class));
        }
        else if (index == 7) {
            final CharSequence[] items = { "Call Us", "Email Us",
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(ActivitySeller.this);
            builder.setTitle("Contact Us!");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (items[item].equals("Call Us")) {
                        //System.out.println("Call us clicked");
                        dialog.dismiss();
                        makeCallfunc();
                    } else if (items[item].equals("Email Us")) {
                        //System.out.println("Email us clicked");
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("message/rfc822");
                        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{email});
                        i.putExtra(Intent.EXTRA_SUBJECT, "Contact");
                        i.putExtra(Intent.EXTRA_TEXT   , "Please write here");
                        try {
                            startActivity(Intent.createChooser(i, "Send mail..."));
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(ActivitySeller.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                        }

                    } else if (items[item].equals("Cancel")) {
                        dialog.dismiss();
                    }
                }
            });
            builder.show();
        }
        else {
            mPager.setCurrentItem(index - 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted!
                    // Perform the action
                    startActivity(callIntent);
                } else {
                    // Permission was denied
                    // :(
                    // Gracefully handle the denial
                    Toast.makeText(ActivitySeller.this, "Requesting Call Permission Denied!", Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }

    void makeCallfunc()
    {
        callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse(contactnum));
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
                {
                    // We will need to request the permission
                    System.out.println("Inside version Requesting.....");

                    System.out.println("Requesting.....");
                    ActivityCompat.requestPermissions(ActivitySeller.this,
                            new String[]{Manifest.permission.CALL_PHONE},
                            MY_PERMISSIONS_REQUEST_CALL_PHONE);

                    // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an app-defined int constant
                } else {
                    // The permission is granted, we can perform the action
                    startActivity(callIntent);
                }
            }
            else
            {
                startActivity(callIntent);
            }
        } catch (Exception e) {
            Toast.makeText(ActivitySeller.this, "Call Permission Denied!", Toast.LENGTH_LONG).show();
        }
    }


    private void dialogShow() {
        final Dialog dialog = new Dialog(ActivitySeller.this);
        dialog.setTitle("Seller Change Password");
        dialog.setContentView(R.layout.dialog_change_password);
        dialog.show();

        final EditText oldPassInput = (EditText) dialog.findViewById(R.id.eoldpass);
        final EditText newPass1Input = (EditText) dialog.findViewById(R.id.enewpass1);
        final EditText newPass2Input = (EditText) dialog.findViewById(R.id.enewpass2);
        Button bupdate = (Button) dialog.findViewById(R.id.bupdate);
        Button bcancel = (Button) dialog.findViewById(R.id.bcancel);
        bupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //original
                oldpass = oldPassInput.getText().toString();
                newpass1 = newPass1Input.getText().toString();
                newpass2 = newPass2Input.getText().toString();

                if(!newpass1.equals(newpass2))
                {
                    L.t(getApplicationContext(), "New passwords don't match.");
                }
                else if(newpass1.length()<6)
                {
                    L.t(getApplicationContext(), "New passwords must contain atleast 6 characters .");
                }
                else {
                    new ActivitySeller.Passwordtask().execute();
                    dialog.cancel();
                }
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


    private class Passwordtask extends AsyncTask<Void, Void, Integer> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(ActivitySeller.this, "Updating...", "Please wait...", true, true);
        }

        @Override
        protected Integer doInBackground(Void... params) {

            try {

                //PreparedStatement preparedStatement=con.prepareStatement("Select password from  "+fair.getDb_name()+"_users where username=?");
                //preparedStatement.setString(1,user);

                URL loadProductUrl = new URL(ActivityMain.Server+"changePassStall.php");
                HttpURLConnection httpURLConnection = (HttpURLConnection) loadProductUrl.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String db = ActivityFair.fair.getDb_name() + "_users";
                String data = URLEncoder.encode("db_table", "UTF-8") + "=" + URLEncoder.encode(db, "UTF-8") + "&" +
                        URLEncoder.encode("login_name", "UTF-8") + "=" + URLEncoder.encode(ActivitySeller.stall.getStall(), "UTF-8") + "&" +
                        URLEncoder.encode("login_old_pass", "UTF-8") + "=" + URLEncoder.encode(oldpass, "UTF-8") + "&" +
                        URLEncoder.encode("login_new_pass", "UTF-8") + "=" + URLEncoder.encode(newpass1, "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                String response = "";
                if ((line = bufferedReader.readLine()) != null) {
                    System.out.println(line);
                    response += line;
                    inputStream.close();
                    bufferedReader.close();
                    httpURLConnection.disconnect();
                    if (response.contains("Success")) {
                        return 2;
                    }
                    else if (response.contains("Failed1")) {
                        return 1;
                    }
                    return 0;
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

            if (value == 2) {
                L.t(getApplicationContext(), "Update Successfull!");
            }else if (value == 1) {
                //L.t(getApplicationContext(), "User Not Found or Check Connection");
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivitySeller.this);
                builder.setTitle("Update Failed!");
                builder.setMessage("Old password is not correct! Try again?");
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
            else if (value == 0) {
                //L.t(getApplicationContext(), "User Not Found or Check Connection");
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivitySeller.this);
                builder.setTitle("Update Failed!");
                builder.setMessage("Connection to the database not established. Try again?");
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
                case TAB_DETAILS:
                    fragment = FragmentStallDetails.newInstance("", "");
                    break;

                case TAB_PRODUCTS:
                    fragment = FragmentStallProducts.newInstance("", "");
                    break;
                case TAB_EMPLOYEES:
                    fragment = FragmentEmployees.newInstance("", "");
                    break;
                case TAB_SELLS:
                    fragment = FragmentSells.newInstance("", "");
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
