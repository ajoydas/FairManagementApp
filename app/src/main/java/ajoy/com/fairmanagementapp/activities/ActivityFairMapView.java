package ajoy.com.fairmanagementapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import ajoy.com.fairmanagementapp.materialtest.R;

public class ActivityFairMapView extends AppCompatActivity {

    private WebView mWebview ;
    String value=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fair_map_view);

        System.out.println("Inside mapview");

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
                    public void onReceivedError(WebView view, int errorCode, String description, String   failingUrl) {

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
