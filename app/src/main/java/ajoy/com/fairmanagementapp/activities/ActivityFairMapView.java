package ajoy.com.fairmanagementapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import ajoy.com.fairmanagementapp.application.R;

public class ActivityFairMapView extends AppCompatActivity {

    private WebView mWebview ;
    String value=null;
    private Toolbar mToolbar;
    private ProgressBar mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fair_map_view);
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
        System.out.println("Inside mapview");

        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            value = extras.getString("Url");
        }
        System.out.println(value);
        WebView browser=null;

        try {

            browser = (WebView) findViewById(R.id.webView);
            if (browser != null) {

                browser .setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        mProgressBar.setVisibility(View.INVISIBLE);
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
