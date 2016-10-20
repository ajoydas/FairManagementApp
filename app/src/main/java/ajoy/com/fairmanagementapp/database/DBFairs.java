package ajoy.com.fairmanagementapp.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;

import ajoy.com.fairmanagementapp.logging.L;
import ajoy.com.fairmanagementapp.objects.Fair;

/**
 * Created by ajoy on 5/22/16.
 */
public class DBFairs {
    public static final int RUNNING_FAIR=1;
    public static final int UPCOMING_FAIR=2;
    private FairsHelper mHelper;
    private SQLiteDatabase mDatabase;

    public DBFairs(Context context) {
        mHelper = new FairsHelper(context);
        mDatabase = mHelper.getWritableDatabase();
    }

    public void insertFairs(int table, ArrayList<Fair> listFairs, boolean clearPrevious) {
        if (clearPrevious) {
            deleteFairs(table);
        }

        //create a sql prepared statement
        String sql = "INSERT INTO " + (table == RUNNING_FAIR ? FairsHelper.TABLE_RUNNING_FAIR : FairsHelper.TABLE_UPCOMING_FAIR) + " VALUES (?,?,?,?,?,?,?,?,?,?);";
        //compile the statement and start a transaction
        SQLiteStatement statement = mDatabase.compileStatement(sql);
        mDatabase.beginTransaction();
        for (int i = 0; i < listFairs.size(); i++) {
            Fair currentFair = listFairs.get(i);
            statement.clearBindings();
            //for a given column index, simply bind the data to be put inside that index
            statement.bindString(2,currentFair.getDb_name());
            statement.bindString(3, currentFair.getTitle());
            statement.bindString(4,currentFair.getOrganizer());
            statement.bindString(5,currentFair.getLocation());
            statement.bindLong(6, currentFair.getStart_date() == null ? -1 : currentFair.getStart_date().getTime());
            statement.bindLong(7, currentFair.getEnd_date() == null ? -1 : currentFair.getEnd_date().getTime());
            statement.bindLong(8, currentFair.getOpen_time() == null ? -1 : currentFair.getOpen_time().getTime());
            statement.bindLong(9, currentFair.getClose_time() == null ? -1 : currentFair.getClose_time().getTime());
            statement.bindString(10, currentFair.getMap_address());

            statement.execute();
        }
        //set the transaction as successful and end the transaction
        L.m("inserting entries " + listFairs.size() + new Date(System.currentTimeMillis()));
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }
/*

    public ArrayList<Fair> readFairs(int table) {
        ArrayList<Fair> listFairs = new ArrayList<>();

        //get a list of columns to be retrieved, we need all of them
        String[] columns = {FairsHelper.COLUMN_UID,
                FairsHelper.COLUMN_DATABASENAME,
                FairsHelper.COLUMN_TITLE,
                FairsHelper.COLUMN_ORGANIZER,
                FairsHelper.COLUMN_LOCATION,
                FairsHelper.COLUMN_STARTDATE,
                FairsHelper.COLUMN_ENDDATE,
                FairsHelper.COLUMN_OPENTIME,
                FairsHelper.COLUMN_CLOSETIME,
                FairsHelper.COLUMN_MAPADDRESS
        };
        Cursor cursor = mDatabase.query((table == RUNNING_FAIR ? FairsHelper.TABLE_RUNNING_FAIR : FairsHelper.TABLE_UPCOMING_FAIR), columns, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            L.m("loading entries " + cursor.getCount() + new Date(System.currentTimeMillis()));
            do {

                Fair fair=new Fair();

                fair.setId(cursor.getInt(cursor.getColumnIndex(FairsHelper.COLUMN_UID)));
                fair.setDb_name(cursor.getString(cursor.getColumnIndex(FairsHelper.COLUMN_DATABASENAME)));
                fair.setTitle(cursor.getString(cursor.getColumnIndex(FairsHelper.COLUMN_TITLE)));
                fair.setOrganizer(cursor.getString(cursor.getColumnIndex(FairsHelper.COLUMN_ORGANIZER)));
                fair.setLocation(cursor.getString(cursor.getColumnIndex(FairsHelper.COLUMN_LOCATION)));
                long fairMilliseconds = cursor.getLong(cursor.getColumnIndex(FairsHelper.COLUMN_STARTDATE));
                fair.setStart_date(fairMilliseconds != -1 ? new Date(fairMilliseconds) : null);
                fairMilliseconds = cursor.getLong(cursor.getColumnIndex(FairsHelper.COLUMN_ENDDATE));
                fair.setEnd_date(fairMilliseconds != -1 ? new Date(fairMilliseconds) : null);
                fairMilliseconds = cursor.getLong(cursor.getColumnIndex(FairsHelper.COLUMN_OPENTIME));
                fair.setOpen_time(fairMilliseconds != -1 ? new Time(fairMilliseconds) : null);
                fairMilliseconds = cursor.getLong(cursor.getColumnIndex(FairsHelper.COLUMN_CLOSETIME));
                fair.setClose_time(fairMilliseconds != -1 ? new Time(fairMilliseconds) : null);
                fair.setMap_address(cursor.getString(cursor.getColumnIndex(FairsHelper.COLUMN_MAPADDRESS)));
                listFairs.add(fair);
            }
            while (cursor.moveToNext());
        }
        return listFairs;
    }
*/

    public void deleteFairs(int  table) {
        mDatabase.delete( (table == RUNNING_FAIR ? FairsHelper.TABLE_RUNNING_FAIR : FairsHelper.TABLE_UPCOMING_FAIR), null, null);
    }

    private static class FairsHelper extends SQLiteOpenHelper {
        public static final String TABLE_RUNNING_FAIR = "running_fair";
        public static final String TABLE_UPCOMING_FAIR = "upcoming_fair";

        public static final String COLUMN_UID = "_id";
        public static final String COLUMN_DATABASENAME = "db_name";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_ORGANIZER = "organizer";
        public static final String COLUMN_LOCATION = "location";
        public static final String COLUMN_STARTDATE = "start_date";
        public static final String COLUMN_ENDDATE = "end_date";
        public static final String COLUMN_OPENTIME = "open_time";
        public static final String COLUMN_CLOSETIME = "close_time";
        public static final String COLUMN_MAPADDRESS = "map_address";

        private static final String CREATE_TABLE_RUNNING_FAIR = "CREATE TABLE " + TABLE_RUNNING_FAIR + " (" +
                COLUMN_UID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_DATABASENAME+" TEXT,"+
                COLUMN_TITLE + " TEXT," +
                COLUMN_ORGANIZER+" TEXT,"+
                COLUMN_LOCATION+" TEXT,"+
                COLUMN_STARTDATE+" INTEGER,"+
                COLUMN_ENDDATE+" INTEGER,"+
                COLUMN_OPENTIME+" INTEGER,"+
                COLUMN_CLOSETIME+" INTEGER,"+
                COLUMN_MAPADDRESS+" TEXT"+
                ");";

        private static final String CREATE_TABLE_UPCOMING_FAIR = "CREATE TABLE " + TABLE_UPCOMING_FAIR + " (" +
                COLUMN_UID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_DATABASENAME+" TEXT,"+
                COLUMN_TITLE + " TEXT," +
                COLUMN_ORGANIZER+" TEXT,"+
                COLUMN_LOCATION+" TEXT,"+
                COLUMN_STARTDATE+" INTEGER,"+
                COLUMN_ENDDATE+" INTEGER,"+
                COLUMN_OPENTIME+" INTEGER,"+
                COLUMN_CLOSETIME+" INTEGER,"+
                COLUMN_MAPADDRESS+" TEXT"+
                ");";

        private static final String DB_NAME = "fairs_db";
        private static final int DB_VERSION = 1;
        private Context mContext;

        public FairsHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
            mContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(CREATE_TABLE_RUNNING_FAIR);
                db.execSQL(CREATE_TABLE_UPCOMING_FAIR);
                L.m("create table Fair list executed");
            } catch (SQLiteException exception) {
                L.t(mContext, exception + "");
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                L.m("upgrade table box office executed");
                db.execSQL(" DROP TABLE " + TABLE_RUNNING_FAIR + " IF EXISTS;");
                db.execSQL(" DROP TABLE " + TABLE_UPCOMING_FAIR + " IF EXISTS;");
                onCreate(db);
            } catch (SQLiteException exception) {
                L.t(mContext, exception + "");
            }
        }
    }

}
