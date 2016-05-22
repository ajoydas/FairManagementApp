package ajoy.com.fairmanagementapp.materialtest;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import  ajoy.com.fairmanagementapp.database.DBProducts;

/**
 * Created by ajoy on 5/19/16.
 */
public class MyApplicationProducts extends Application {

    private static MyApplicationProducts sInstanceProduct;

    private static DBProducts mDatabase;

    public static MyApplicationProducts getInstance() {
        return sInstanceProduct;
    }

    public static Context getAppContext() {
        return sInstanceProduct.getApplicationContext();
    }

    public synchronized static DBProducts getWritableDatabaseProduct() {
        if (mDatabase == null) {
            mDatabase = new DBProducts(getAppContext());
        }
        return mDatabase;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstanceProduct = this;
        mDatabase = new DBProducts(this);
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
