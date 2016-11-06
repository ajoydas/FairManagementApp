package ajoy.com.fairmanagementapp.activities;

import android.content.Intent;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.viksaa.sssplash.lib.activity.AwesomeSplash;
import com.viksaa.sssplash.lib.cnst.Flags;
import com.viksaa.sssplash.lib.model.ConfigSplash;

import ajoy.com.fairmanagementapp.application.BuildConfig;
import ajoy.com.fairmanagementapp.application.MyApplication;
import ajoy.com.fairmanagementapp.application.R;

/**
 * Created by hp on 29-10-2016.
 */

//extends AwesomeSplash!
public class SplashScreen extends AwesomeSplash {

    //DO NOT OVERRIDE onCreate()!
    //if you need to start some services do it in initSplash()!
    private FirebaseRemoteConfig mFirebaseRemoteConfig;


    @Override
    public void initSplash(ConfigSplash configSplash) {

            /* you don't have to override every property */

        //Customize Circular Reveal
        configSplash.setBackgroundColor(R.color.colorPrimary); //any color you want form colors.xml
        configSplash.setAnimCircularRevealDuration(1000); //int ms
        configSplash.setRevealFlagX(Flags.REVEAL_RIGHT);  //or Flags.REVEAL_LEFT
        configSplash.setRevealFlagY(Flags.REVEAL_BOTTOM); //or Flags.REVEAL_TOP

        //Choose LOGO OR PATH; if you don't provide String value for path it's logo by default

        //Customize Logo
        configSplash.setLogoSplash(R.drawable.ic_splash); //or any other drawable
        configSplash.setAnimLogoSplashDuration(1000); //int ms
        configSplash.setAnimLogoSplashTechnique(Techniques.Bounce); //choose one form Techniques (ref: https://github.com/daimajia/AndroidViewAnimations)


        //Customize Path
/*
        configSplash.setPathSplash(SyncStateContract.Constants.DROID_LOGO); //set path String
        configSplash.setOriginalHeight(400); //in relation to your svg (path) resource
        configSplash.setOriginalWidth(400); //in relation to your svg (path) resource
        configSplash.setAnimPathStrokeDrawingDuration(3000);
        configSplash.setPathSplashStrokeSize(3); //I advise value be <5
        configSplash.setPathSplashStrokeColor(R.color.accent); //any color you want form colors.xml
        configSplash.setAnimPathFillingDuration(3000);
        configSplash.setPathSplashFillColor(R.color.Wheat); //path object filling color
*/

        //Customize Title
        configSplash.setTitleSplash("Fair Files");
        configSplash.setTitleTextColor(R.color.white);
        configSplash.setTitleTextSize(25f); //float value
        configSplash.setAnimTitleDuration(1000);
        configSplash.setAnimTitleTechnique(Techniques.FlipInX);
        configSplash.setTitleFont("kalpurush.ttf"); //provide string to your font located in assets/fonts/

    }

    @Override
    public void animationsFinished() {

        //transit to another activity here
        //or do whatever you want

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        ActivityMain.Server=mFirebaseRemoteConfig.getString("Server");
        ActivityMain.Count=mFirebaseRemoteConfig.getLong("Count");
        ActivityMain.email=mFirebaseRemoteConfig.getString("Email");

        mFirebaseRemoteConfig.fetch(0)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //Toast.makeText(MyApplication.getAppContext(), "Fetch Successfull", Toast.LENGTH_SHORT).show();
                            mFirebaseRemoteConfig.activateFetched();
                        } else {
                            //Toast.makeText(MyApplication.getAppContext(), "Fetch Failed", Toast.LENGTH_SHORT).show();
                        }
                        ActivityMain.Server=mFirebaseRemoteConfig.getString("Server");
                        ActivityMain.Count=mFirebaseRemoteConfig.getLong("Count");
                        ActivityMain.email=mFirebaseRemoteConfig.getString("Email");
                        finish();
                        startActivity(new Intent(MyApplication.getAppContext(),ActivityMain.class));
                    }
                });

    }
}