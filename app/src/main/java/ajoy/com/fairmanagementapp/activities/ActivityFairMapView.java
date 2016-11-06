package ajoy.com.fairmanagementapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInviteInvitation;

import ajoy.com.fairmanagementapp.application.MyApplication;
import ajoy.com.fairmanagementapp.application.R;
import ajoy.com.fairmanagementapp.logging.L;

public class ActivityFairMapView extends AppCompatActivity {

    private WebView mWebview ;
    String value=null;
    private Toolbar mToolbar;
    private ProgressBar mProgressBar;
    private static final int REQUEST_INVITE = 1;
    WebView browser=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fair_map_view);
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
        System.out.println("Inside mapview");

        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            value = extras.getString("Url");
        }
        System.out.println(value);
        if(URLUtil.isValidUrl(value)) {


            try {

                browser = (WebView) findViewById(R.id.webView);
                if (browser != null) {

                    browser.setWebViewClient(new WebViewClient() {
                        @Override
                        public void onPageFinished(WebView view, String url) {
                            super.onPageFinished(view, url);
                            mProgressBar.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                            super.onReceivedHttpError(view, request, errorResponse);
                            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityFairMapView.this);
                            builder.setTitle("Map Not Available!");
                            builder.setMessage("The map of the fair is not available now.");
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

                    });
                }

                if (browser != null) {
                    browser.getSettings().setJavaScriptEnabled(true);
                    browser.loadUrl(value);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityFairMapView.this);
            builder.setTitle("Map Not Available!");
            builder.setMessage("The map of the fair is not available now.");
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
                    Toast.makeText(ActivityFairMapView.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
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
        if (browser.canGoBack()) {
            browser.goBack();
        } else {
            super.onBackPressed();
            finish();
        }
    }

}
