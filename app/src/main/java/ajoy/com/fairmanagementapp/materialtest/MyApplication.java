package ajoy.com.fairmanagementapp.materialtest;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ajoy.com.fairmanagementapp.database.DBFairs;
import  ajoy.com.fairmanagementapp.database.DBProducts;
import ajoy.com.fairmanagementapp.database.DBStalls;

/**
 * Created by ajoy on 5/22/16.
 */
public class MyApplication extends Application {

    private static MyApplication sInstance;

    private static DBProducts mDatabaseProduct;
    private static DBFairs mDatabaseFair;
    private static DBStalls mDatabaseStall;

    public static MyApplication getInstance() {
        return sInstance;
    }

    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }

    public synchronized static DBProducts getWritableDatabaseProduct() {
        if (mDatabaseProduct == null) {
            mDatabaseProduct = new DBProducts(getAppContext());
        }
        return mDatabaseProduct;
    }

    public static DBFairs getWritableDatabaseFair() {
        if (mDatabaseFair == null) {
            mDatabaseFair = new DBFairs(getAppContext());
        }
        return mDatabaseFair;
    }

    public static DBStalls getWritableDatabaseStall() {
        if (mDatabaseStall == null) {
            mDatabaseStall = new DBStalls(getAppContext());
        }
        return mDatabaseStall;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        mDatabaseProduct=new DBProducts(this);
        mDatabaseFair = new DBFairs(this);
        mDatabaseStall = new DBStalls(this);
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
