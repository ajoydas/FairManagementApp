package ajoy.com.fairmanagementapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.io.File;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;

import ajoy.com.fairmanagementapp.logging.L;
import ajoy.com.fairmanagementapp.objects.FavProduct;

/**
 * Created by hp on 22-10-2016.
 */

public class DBFavProducts {
    public static final int ProductList = 0;
    private DBFavProducts.FavProductsHelper mHelper;
    private SQLiteDatabase mDatabase;

    public DBFavProducts(Context context) {
        mHelper = new DBFavProducts.FavProductsHelper(context);
        mDatabase = mHelper.getWritableDatabase();
    }

    public boolean updateFavProducts(FavProduct favProduct) {
        ContentValues cv = new ContentValues();
/*
FavProductsHelper.COLUMN_UID,
                FavProductsHelper.COLUMN_TABLE,
                FavProductsHelper.COLUMN_PRODUCTID,
                FavProductsHelper.COLUMN_TITLE,
                FavProductsHelper.COLUMN_LOCATION,
                FavProductsHelper.COLUMN_STARTDATE,
                FavProductsHelper.COLUMN_ENDDATE,
                FavProductsHelper.COLUMN_OPENTIME,
                FavProductsHelper.COLUMN_CLOSETIME,
                FavProductsHelper.COLUMN_STALL,
                FavProductsHelper.COLUMN_NAME,
                FavProductsHelper.COLUMN_COMPANY,
                FavProductsHelper.COLUMN_DESCRIPTION,
                FavProductsHelper.COLUMN_PRICE,
                FavProductsHelper.COLUMN_AVAILABILITY,
                FavProductsHelper.COLUMN_IMAGE,
                FavProductsHelper.COLUMN_STALLLOCATION
                */
        try {
            cv.put(FavProductsHelper.COLUMN_TITLE, favProduct.getFair());
            cv.put(FavProductsHelper.COLUMN_LOCATION, favProduct.getLocation());
            if (favProduct.getStart_date() == null) {
                cv.put(FavProductsHelper.COLUMN_STARTDATE, -1);
            } else {
                cv.put(FavProductsHelper.COLUMN_STARTDATE, favProduct.getStart_date().getTime());
            }
            if (favProduct.getEnd_date() == null) {
                cv.put(FavProductsHelper.COLUMN_ENDDATE, -1);
            } else {
                cv.put(FavProductsHelper.COLUMN_ENDDATE, favProduct.getEnd_date().getTime());
            }
            if (favProduct.getOpen_time() == null) {
                cv.put(FavProductsHelper.COLUMN_OPENTIME, -1);
            } else {
                cv.put(FavProductsHelper.COLUMN_OPENTIME, favProduct.getOpen_time().getTime());
            }
            if (favProduct.getClose_time() == null) {
                cv.put(FavProductsHelper.COLUMN_CLOSETIME, -1);
            } else {
                cv.put(FavProductsHelper.COLUMN_CLOSETIME, favProduct.getClose_time().getTime());
            }
            cv.put(FavProductsHelper.COLUMN_STALL, favProduct.getStall());
            cv.put(FavProductsHelper.COLUMN_NAME, favProduct.getName());
            cv.put(FavProductsHelper.COLUMN_COMPANY, favProduct.getCompany());
            cv.put(FavProductsHelper.COLUMN_DESCRIPTION, favProduct.getDescription());
            cv.put(FavProductsHelper.COLUMN_PRICE, favProduct.getPrice());
            cv.put(FavProductsHelper.COLUMN_AVAILABILITY, favProduct.getAvailability());
            cv.put(FavProductsHelper.COLUMN_IMAGE, favProduct.getImage());
            cv.put(FavProductsHelper.COLUMN_STALLLOCATION, favProduct.getStalllocation());
            mDatabase.update(FavProductsHelper.TABLE_PRODUCT_LIST, cv, "_id=" + favProduct.getId(), null);
            return true;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean queryFavProducts(String db_table,String productid) {
        boolean state = false;
        //String sql = "SELECT * FROM " + DBFavProducts.FavProductsHelper.TABLE_PRODUCT_LIST + " WHERE table=?";
        //compile the statement and start a transaction
//        SQLiteStatement statement = mDatabase.compileStatement(sql);
        mDatabase.beginTransaction();
//        statement.clearBindings();
//        statement.bindString(1, table);

        //for a given column index, simply bind the data to be put inside that index
        Cursor cursor = mDatabase.query(DBFavProducts.FavProductsHelper.TABLE_PRODUCT_LIST, new String[]{"_id", "db_table","productid"},
                "db_table = '" + db_table + "' and productid = '"+productid+"'", null, null, null, null);
        if (cursor.getCount() != 0) state = true;
        //set the transaction as successful and end the transaction
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
        return state;
    }

    public void insertFavProducts(FavProduct currentproduct, boolean clearPrevious) {
        /*
        if (clearPrevious) {
            deleteProducts();
        }
*/        //create a sql prepared statement
        String sql = "INSERT INTO " + DBFavProducts.FavProductsHelper.TABLE_PRODUCT_LIST + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
        //compile the statement and start a transaction
        SQLiteStatement statement = mDatabase.compileStatement(sql);
        mDatabase.beginTransaction();
        statement.clearBindings();
        //for a given column index, simply bind the data to be put inside that index
        statement.bindString(2, currentproduct.getTable());
        statement.bindString(3, currentproduct.getProductid());
        statement.bindString(4, currentproduct.getFair());
        statement.bindString(5, currentproduct.getLocation());
        statement.bindLong(6, currentproduct.getStart_date() == null ? -1 : currentproduct.getStart_date().getTime());
        statement.bindLong(7, currentproduct.getEnd_date() == null ? -1 : currentproduct.getEnd_date().getTime());
        statement.bindLong(8, currentproduct.getOpen_time() == null ? -1 : currentproduct.getOpen_time().getTime());
        statement.bindLong(9, currentproduct.getClose_time() == null ? -1 : currentproduct.getClose_time().getTime());
        statement.bindString(10, currentproduct.getStall());
        statement.bindString(11, currentproduct.getName());
        statement.bindString(12, currentproduct.getCompany());
        statement.bindString(13, currentproduct.getDescription());
        statement.bindString(14, currentproduct.getPrice());
        statement.bindString(15, currentproduct.getAvailability());
        if (currentproduct.getImage() != null) statement.bindString(16, currentproduct.getImage());
        statement.bindString(17, currentproduct.getStalllocation());
        statement.execute();

        //set the transaction as successful and end the transaction
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }

    public ArrayList<FavProduct> readFavProducts() {
        ArrayList<FavProduct> listProducts = new ArrayList<>();

        //get a list of columns to be retrieved, we need all of them
        String[] columns = {
                FavProductsHelper.COLUMN_UID,
                FavProductsHelper.COLUMN_TABLE,
                FavProductsHelper.COLUMN_PRODUCTID,
                FavProductsHelper.COLUMN_TITLE,
                FavProductsHelper.COLUMN_LOCATION,
                FavProductsHelper.COLUMN_STARTDATE,
                FavProductsHelper.COLUMN_ENDDATE,
                FavProductsHelper.COLUMN_OPENTIME,
                FavProductsHelper.COLUMN_CLOSETIME,
                FavProductsHelper.COLUMN_STALL,
                FavProductsHelper.COLUMN_NAME,
                FavProductsHelper.COLUMN_COMPANY,
                FavProductsHelper.COLUMN_DESCRIPTION,
                FavProductsHelper.COLUMN_PRICE,
                FavProductsHelper.COLUMN_AVAILABILITY,
                FavProductsHelper.COLUMN_IMAGE,
                FavProductsHelper.COLUMN_STALLLOCATION
        };
        Cursor cursor = mDatabase.query(FavProductsHelper.TABLE_PRODUCT_LIST, columns, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            L.m("loading entries " + cursor.getCount() + new Date(System.currentTimeMillis()));
            do {

                FavProduct product = new FavProduct();

                product.setId(cursor.getInt(cursor.getColumnIndex(FavProductsHelper.COLUMN_UID)));
                product.setTable(cursor.getString(cursor.getColumnIndex(FavProductsHelper.COLUMN_TABLE)));
                product.setProductid(cursor.getString(cursor.getColumnIndex(FavProductsHelper.COLUMN_PRODUCTID)));
                product.setFair(cursor.getString(cursor.getColumnIndex(FavProductsHelper.COLUMN_TITLE)));
                product.setLocation(cursor.getString(cursor.getColumnIndex(FavProductsHelper.COLUMN_LOCATION)));
                long productMilliseconds = cursor.getLong(cursor.getColumnIndex(FavProductsHelper.COLUMN_STARTDATE));
                product.setStart_date(productMilliseconds != -1 ? new Date(productMilliseconds) : null);
                productMilliseconds = cursor.getLong(cursor.getColumnIndex(FavProductsHelper.COLUMN_ENDDATE));
                product.setEnd_date(productMilliseconds != -1 ? new Date(productMilliseconds) : null);
                productMilliseconds = cursor.getLong(cursor.getColumnIndex(FavProductsHelper.COLUMN_OPENTIME));
                product.setOpen_time(productMilliseconds != -1 ? new Time(productMilliseconds) : null);
                productMilliseconds = cursor.getLong(cursor.getColumnIndex(FavProductsHelper.COLUMN_CLOSETIME));
                product.setClose_time(productMilliseconds != -1 ? new Time(productMilliseconds) : null);
                product.setStall(cursor.getString(cursor.getColumnIndex(FavProductsHelper.COLUMN_STALL)));
                product.setName(cursor.getString(cursor.getColumnIndex(FavProductsHelper.COLUMN_NAME)));
                product.setCompany(cursor.getString(cursor.getColumnIndex(FavProductsHelper.COLUMN_COMPANY)));
                product.setDescription(cursor.getString(cursor.getColumnIndex(FavProductsHelper.COLUMN_DESCRIPTION)));
                product.setPrice(cursor.getString(cursor.getColumnIndex(FavProductsHelper.COLUMN_PRICE)));
                product.setAvailability(cursor.getString(cursor.getColumnIndex(FavProductsHelper.COLUMN_AVAILABILITY)));
                product.setImage(cursor.getString(cursor.getColumnIndex(FavProductsHelper.COLUMN_IMAGE)));
                product.setStalllocation(cursor.getString(cursor.getColumnIndex(FavProductsHelper.COLUMN_STALLLOCATION)));
                //add the movie to the list of movie objects which we plan to return
                listProducts.add(product);
                System.out.println(product);
            }
            while (cursor.moveToNext());
        }
        return listProducts;
    }


    public boolean deleteIdentifier(String db_table, String productid) {
        boolean ret = false;
        mDatabase.beginTransaction();
        Cursor cursor = mDatabase.query(DBFavProducts.FavProductsHelper.TABLE_PRODUCT_LIST, new String[]{"_id", "db_table", "image"},
                "db_table = '" + db_table + "' and productid = '" + productid + "'", null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            try {
                new File(cursor.getString(cursor.getColumnIndex(FavProductsHelper.COLUMN_IMAGE))).delete();
                //MyApplication.getAppContext().deleteFile(cursor.getString(cursor.getColumnIndex(FavProductsHelper.COLUMN_IMAGE)));
                ret = (mDatabase.delete(DBFavProducts.FavProductsHelper.TABLE_PRODUCT_LIST, "db_table = '" + db_table + "' and productid = '" + productid + "'", null) > 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
        return ret;
    }


    public void deleteProducts() {
        mDatabase.delete(DBFavProducts.FavProductsHelper.TABLE_PRODUCT_LIST, null, null);
    }

    private static class FavProductsHelper extends SQLiteOpenHelper {
        public static final String TABLE_PRODUCT_LIST = "product_list";

        public static final String COLUMN_UID = "_id";
        public static final String COLUMN_TABLE = "db_table";
        public static final String COLUMN_PRODUCTID = "productid";
        public static final String COLUMN_TITLE = "fair";
        public static final String COLUMN_LOCATION = "location";
        public static final String COLUMN_STARTDATE = "start_date";
        public static final String COLUMN_ENDDATE = "end_date";
        public static final String COLUMN_OPENTIME = "open_time";
        public static final String COLUMN_CLOSETIME = "close_time";
        public static final String COLUMN_STALL = "stall";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_COMPANY = "company";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_AVAILABILITY = "availability";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_STALLLOCATION = "stalllocation";


        private static final String CREATE_TABLE_PRODUCT_LIST = "CREATE TABLE " + TABLE_PRODUCT_LIST + " (" +
                COLUMN_UID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_TABLE + " TEXT," +
                COLUMN_PRODUCTID + " TEXT," +
                COLUMN_TITLE + " TEXT," +
                COLUMN_LOCATION + " TEXT," +
                COLUMN_STARTDATE + " INTEGER," +
                COLUMN_ENDDATE + " INTEGER," +
                COLUMN_OPENTIME + " INTEGER," +
                COLUMN_CLOSETIME + " INTEGER," +
                COLUMN_STALL + " TEXT," +
                COLUMN_NAME + " TEXT," +
                COLUMN_COMPANY + " TEXT," +
                COLUMN_DESCRIPTION + " TEXT," +
                COLUMN_PRICE + " TEXT," +
                COLUMN_AVAILABILITY + " TEXT," +
                COLUMN_IMAGE + " TEXT, " +
                COLUMN_STALLLOCATION + " TEXT " +
                ");";

        private static final String DB_NAME = "FavProducts_db";
        private static final int DB_VERSION = 1;
        private Context mContext;

        public FavProductsHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
            mContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(CREATE_TABLE_PRODUCT_LIST);
                L.m("create table product list executed");
            } catch (SQLiteException exception) {
                System.out.println(exception);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                L.m("upgrade table box office executed");
                db.execSQL(" DROP TABLE " + TABLE_PRODUCT_LIST + " IF EXISTS;");
                onCreate(db);
            } catch (SQLiteException exception) {
                System.out.println(exception);
            }
        }
    }
}
