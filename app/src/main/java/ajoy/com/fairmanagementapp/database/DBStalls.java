package ajoy.com.fairmanagementapp.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.Date;

import ajoy.com.fairmanagementapp.logging.L;
import ajoy.com.fairmanagementapp.pojo.Stall;

/**
 * Created by ajoy on 5/29/16.
 */
public class DBStalls {
    public static final int StallList=0;
    private StallsHelper mHelper;
    private SQLiteDatabase mDatabase;

    public DBStalls(Context context) {
        mHelper = new StallsHelper(context);
        mDatabase = mHelper.getWritableDatabase();
    }

    public void insertStalls(int table, ArrayList<Stall> listStalls, boolean clearPrevious) {
        if (clearPrevious) {
            deleteStalls();
        }

        //create a sql prepared statement
        String sql = "INSERT INTO " + StallsHelper.TABLE_STALL_LIST + " VALUES (?,?,?,?,?,?);";
        //compile the statement and start a transaction
        SQLiteStatement statement = mDatabase.compileStatement(sql);
        mDatabase.beginTransaction();
        for (int i = 0; i < listStalls.size(); i++) {
            Stall currentstall = listStalls.get(i);
            statement.clearBindings();
            //for a given column index, simply bind the data to be put inside that index
            statement.bindString(2, currentstall.getStall());
            statement.bindString(3, currentstall.getStall_name());
            statement.bindString(4, currentstall.getOwner());
            statement.bindString(5, currentstall.getDescription());
            statement.bindString(6,currentstall.getLocation());

            statement.execute();
        }
        //set the transaction as successful and end the transaction
        L.m("inserting entries " + listStalls.size() + new Date(System.currentTimeMillis()));
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }

    public ArrayList<Stall> readStalls() {
        ArrayList<Stall> listStalls = new ArrayList<>();

        //get a list of columns to be retrieved, we need all of them
        String[] columns = {StallsHelper.COLUMN_UID,
                StallsHelper.COLUMN_STALL,
                StallsHelper.COLUMN_STALL_NAME,
                StallsHelper.COLUMN_OWNER,
                StallsHelper.COLUMN_DESCRIPTION,
                StallsHelper.COLUMN_LOCATION,
        };
        Cursor cursor = mDatabase.query(StallsHelper.TABLE_STALL_LIST, columns, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            L.m("loading entries " + cursor.getCount() + new Date(System.currentTimeMillis()));
            do {

                Stall stall=new Stall();

                stall.setId(cursor.getInt(cursor.getColumnIndex(StallsHelper.COLUMN_UID)));
                stall.setStall(cursor.getString(cursor.getColumnIndex(StallsHelper.COLUMN_STALL)));
                stall.setStall_name(cursor.getString(cursor.getColumnIndex(StallsHelper.COLUMN_STALL_NAME)));
                stall.setOwner(cursor.getString(cursor.getColumnIndex(StallsHelper.COLUMN_OWNER)));
                stall.setDescription(cursor.getString(cursor.getColumnIndex(StallsHelper.COLUMN_DESCRIPTION)));
                stall.setLocation(cursor.getString(cursor.getColumnIndex(StallsHelper.COLUMN_LOCATION)));
                //add the movie to the list of movie objects which we plan to return
                listStalls.add(stall);
            }
            while (cursor.moveToNext());
        }
        return listStalls;
    }

    public void deleteStalls() {
        mDatabase.delete( StallsHelper.TABLE_STALL_LIST, null, null);
    }

    private static class StallsHelper extends SQLiteOpenHelper {
        public static final String TABLE_STALL_LIST = "stall_list";

        public static final String COLUMN_UID = "_id";
        public static final String COLUMN_STALL = "stall";
        public static final String COLUMN_STALL_NAME = "stall_name";
        public static final String COLUMN_OWNER = "owner";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_LOCATION ="location";

        private static final String CREATE_TABLE_STALL_LIST = "CREATE TABLE " + TABLE_STALL_LIST + " (" +
                COLUMN_UID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_STALL + " TEXT," +
                COLUMN_STALL_NAME + " TEXT," +
                COLUMN_OWNER + " TEXT," +
                COLUMN_DESCRIPTION + " TEXT," +
                COLUMN_LOCATION +" TEXT"+
                ");";

        private static final String DB_NAME = "stalls_db";
        private static final int DB_VERSION = 1;
        private Context mContext;

        public StallsHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
            mContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(CREATE_TABLE_STALL_LIST);
                L.m("create table stall list executed");
            } catch (SQLiteException exception) {
                L.t(mContext, exception + "");
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                L.m("upgrade table box office executed");
                db.execSQL(" DROP TABLE " + TABLE_STALL_LIST + " IF EXISTS;");
                onCreate(db);
            } catch (SQLiteException exception) {
                L.t(mContext, exception + "");
            }
        }
    }

}
