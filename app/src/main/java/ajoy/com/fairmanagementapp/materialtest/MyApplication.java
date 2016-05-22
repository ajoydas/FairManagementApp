package ajoy.com.fairmanagementapp.materialtest;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import  ajoy.com.fairmanagementapp.database.DBMovies;
import  ajoy.com.fairmanagementapp.database.DBProducts;

/**
 * Created by Windows on 30-01-2015.
 */
public class MyApplication extends Application {


    public static final String API_KEY_ROTTEN_TOMATOES = "54wzfswsa4qmjg8hjwa64d4c";
    private static MyApplication sInstance;

    private static DBMovies mDatabaseMovie;
    private static DBProducts mDatabaseProduct;

    public static MyApplication getInstance() {
        return sInstance;
    }

    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }

    public synchronized static DBMovies getWritableDatabaseMovie() {
        if (mDatabaseMovie == null) {
            mDatabaseMovie = new DBMovies(getAppContext());
        }
        return mDatabaseMovie;
    }

    public synchronized static DBProducts getWritableDatabaseProduct() {
        if (mDatabaseProduct == null) {
            mDatabaseProduct = new DBProducts(getAppContext());
        }
        return mDatabaseProduct;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        mDatabaseMovie = new DBMovies(this);
        mDatabaseProduct=new DBProducts(this);
    }

    public static void saveToPreferences(Context context, String preferenceName, String preferenceValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceName, preferenceValue);
        editor.apply();
    }

    public static void saveToPreferences(Context context, String preferenceName, boolean preferenceValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(preferenceName, preferenceValue);
        editor.apply();
    }

    public static String readFromPreferences(Context context, String preferenceName, String defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return sharedPreferences.getString(preferenceName, defaultValue);
    }

    public static boolean readFromPreferences(Context context, String preferenceName, boolean defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return sharedPreferences.getBoolean(preferenceName, defaultValue);
    }
}
