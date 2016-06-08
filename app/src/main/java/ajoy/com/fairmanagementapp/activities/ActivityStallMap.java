package ajoy.com.fairmanagementapp.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import ajoy.com.fairmanagementapp.extras.PermissionUtils;
import ajoy.com.fairmanagementapp.materialtest.R;

public class ActivityStallMap extends AppCompatActivity implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private GoogleMap mMap;
    String markerpoint;
    String stallName;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            markerpoint = extras.getString("Location");
            stallName = extras.getString("Stallname");

        }
        System.out.println(markerpoint);

        setContentView(R.layout.activity_stall_map);

        mToolbar = (Toolbar) findViewById(R.id.app_bar);

        setSupportActionBar(mToolbar);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng stall = null;
        try {
            String d1 = markerpoint.substring(0, markerpoint.indexOf(',') - 1);
            String d2 = markerpoint.substring(markerpoint.indexOf(',') + 1, markerpoint.length() - 1);
            System.out.println(d1 + " and " + d2);

            stall = new LatLng(Double.parseDouble(d1), Double.parseDouble(d2));
        } catch (Exception e) {
            stall = new LatLng(0,0);
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
        mMap.animateCamera( CameraUpdateFactory.zoomTo( 17.0f ) );
        enableMyLocation();
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
