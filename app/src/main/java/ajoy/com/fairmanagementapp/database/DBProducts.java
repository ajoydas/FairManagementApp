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
import ajoy.com.fairmanagementapp.pojo.Product;

/**
 * Created by ajoy on 5/19/16.
 */
public class DBProducts {
    public static final int ProductList=0;
    private ProductsHelper mHelper;
    private SQLiteDatabase mDatabase;

    public DBProducts(Context context) {
        mHelper = new ProductsHelper(context);
        mDatabase = mHelper.getWritableDatabase();
    }

    public void insertProducts(int table, ArrayList<Product> listProducts, boolean clearPrevious) {
        if (clearPrevious) {
            deleteProducts();
        }

        //create a sql prepared statement
        String sql = "INSERT INTO " + ProductsHelper.TABLE_PRODUCT_LIST+ " VALUES (?,?,?,?,?,?,?,?);";
        //compile the statement and start a transaction
        SQLiteStatement statement = mDatabase.compileStatement(sql);
        mDatabase.beginTransaction();
        for (int i = 0; i < listProducts.size(); i++) {
            Product currentproduct = listProducts.get(i);
            statement.clearBindings();
            //for a given column index, simply bind the data to be put inside that index
            statement.bindString(2, currentproduct.getStall());
            statement.bindString(3, currentproduct.getName());
            statement.bindString(4, currentproduct.getCompany());
            statement.bindString(5, currentproduct.getDescription());
            statement.bindString(6,currentproduct.getPrice());
            statement.bindString(7, currentproduct.getAvailability());
            if( currentproduct.getImage()!=null)statement.bindString(8, currentproduct.getImage());

            statement.execute();
        }
        //set the transaction as successful and end the transaction
        L.m("inserting entries " + listProducts.size() + new Date(System.currentTimeMillis()));
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }

    public ArrayList<Product> readProducts() {
        ArrayList<Product> listProducts = new ArrayList<>();

        //get a list of columns to be retrieved, we need all of them
        String[] columns = {ProductsHelper.COLUMN_UID,
                ProductsHelper.COLUMN_STALL,
                ProductsHelper.COLUMN_NAME,
                ProductsHelper.COLUMN_COMPANY,
                ProductsHelper.COLUMN_DESCRIPTION,
                ProductsHelper.COLUMN_PRICE,
                ProductsHelper.COLUMN_AVAILABILITY,
                ProductsHelper.COLUMN_IMAGE
        };
        Cursor cursor = mDatabase.query(ProductsHelper.TABLE_PRODUCT_LIST, columns, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            L.m("loading entries " + cursor.getCount() + new Date(System.currentTimeMillis()));
            do {

                Product product=new Product();

                product.setId(cursor.getInt(cursor.getColumnIndex(ProductsHelper.COLUMN_UID)));
                product.setStall(cursor.getString(cursor.getColumnIndex(ProductsHelper.COLUMN_STALL)));
                product.setName(cursor.getString(cursor.getColumnIndex(ProductsHelper.COLUMN_NAME)));
                product.setCompany(cursor.getString(cursor.getColumnIndex(ProductsHelper.COLUMN_COMPANY)));
                product.setDescription(cursor.getString(cursor.getColumnIndex(ProductsHelper.COLUMN_DESCRIPTION)));
                product.setPrice(cursor.getString(cursor.getColumnIndex(ProductsHelper.COLUMN_PRICE)));
                product.setAvailability(cursor.getString(cursor.getColumnIndex(ProductsHelper.COLUMN_AVAILABILITY)));
                product.setImage(cursor.getString(cursor.getColumnIndex(ProductsHelper.COLUMN_IMAGE)));

                //add the movie to the list of movie objects which we plan to return
                listProducts.add(product);
            }
            while (cursor.moveToNext());
        }
        return listProducts;
    }

    public void deleteProducts() {
        mDatabase.delete( ProductsHelper.TABLE_PRODUCT_LIST, null, null);
    }

    private static class ProductsHelper extends SQLiteOpenHelper {
        public static final String TABLE_PRODUCT_LIST = "product_list";

        public static final String COLUMN_UID = "_id";
        public static final String COLUMN_STALL = "stall";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_COMPANY = "company";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_PRICE="price";
        public static final String COLUMN_AVAILABILITY = "availability";
        public static final String COLUMN_IMAGE="image";

        private static final String CREATE_TABLE_PRODUCT_LIST = "CREATE TABLE " +TABLE_PRODUCT_LIST + " (" +
                COLUMN_UID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_STALL + " TEXT," +
                COLUMN_NAME + " TEXT," +
                COLUMN_COMPANY + " TEXT," +
                COLUMN_DESCRIPTION + " TEXT," +
                COLUMN_PRICE+" TEXT,"+
                COLUMN_AVAILABILITY + " TEXT," +
                COLUMN_IMAGE+" LONGBLOB "+
                ");";

        private static final String DB_NAME = "products_db";
        private static final int DB_VERSION = 1;
        private Context mContext;

        public ProductsHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
            mContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(CREATE_TABLE_PRODUCT_LIST);
                L.m("create table product list executed");
            } catch (SQLiteException exception) {
                L.t(mContext, exception + "");
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                L.m("upgrade table box office executed");
                db.execSQL(" DROP TABLE " + TABLE_PRODUCT_LIST + " IF EXISTS;");
                onCreate(db);
            } catch (SQLiteException exception) {
                L.t(mContext, exception + "");
            }
        }
    }

}
