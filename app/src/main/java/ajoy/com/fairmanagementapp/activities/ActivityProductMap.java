package ajoy.com.fairmanagementapp.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import ajoy.com.fairmanagementapp.application.MyApplication;
import ajoy.com.fairmanagementapp.extras.PermissionUtils;
import ajoy.com.fairmanagementapp.application.R;

public class ActivityProductMap extends AppCompatActivity implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private GoogleMap mMap;
    String markerpoint;
    String stallName;
    String imageString;
    Bitmap image=null;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Toolbar mToolbar;
    private GroundOverlay mGroundOverlay;
    private static final int REQUEST_INVITE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        imageString = null;
        if (extras != null) {
            markerpoint = extras.getString("Location");
            stallName = extras.getString("Stallname");
            imageString = extras.getString("Image");
        }
        Glide.with(MyApplication.getAppContext()).load(imageString).asBitmap().into(new SimpleTarget() {
            @Override
            public void onResourceReady(Object resource, GlideAnimation glideAnimation) {
                image= (Bitmap) resource;
            }
        });
        //image = StringToBitMap(imageString);
        try {
            System.out.println(markerpoint);
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_stall_map);

        mToolbar = (Toolbar) findViewById(R.id.app_bar);
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

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
                    Toast.makeText(ActivityProductMap.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
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

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng stall = null;
        try {
            String d1 = markerpoint.substring(0, markerpoint.indexOf(',') - 1);
            String d2 = markerpoint.substring(markerpoint.indexOf(',') + 1, markerpoint.length() - 1);
            System.out.println(d1 + " and " + d2);

            stall = new LatLng(Double.parseDouble(d1), Double.parseDouble(d2));
        } catch (Exception e) {
            stall = new LatLng(0, 0);
            e.printStackTrace();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Location Not Available!");
            builder.setMessage("The location of the stall is not available now.");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    finish();
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        mMap.addMarker(new MarkerOptions().position(stall).title(stallName));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(stall));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
        enableMyLocation();

        if(image!=null) {
            mGroundOverlay = mMap.addGroundOverlay(new GroundOverlayOptions()
                    .image(BitmapDescriptorFactory.fromBitmap(image))
                    .anchor(0, 1)
                    .transparency((float) 0.7)
                    .position(stall, 200f, 150f));

        }
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }
}
