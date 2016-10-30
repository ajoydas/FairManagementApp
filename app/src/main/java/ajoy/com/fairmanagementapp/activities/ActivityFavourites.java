package ajoy.com.fairmanagementapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInviteInvitation;

import ajoy.com.fairmanagementapp.application.R;

public class ActivityFavourites extends AppCompatActivity {

    private Toolbar mToolbar;
    private static final int REQUEST_INVITE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

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
                    Toast.makeText(ActivityFavourites.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
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
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
