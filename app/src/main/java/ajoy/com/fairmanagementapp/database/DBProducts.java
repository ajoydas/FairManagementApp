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
        String sql = "INSERT INTO " + ProductsHelper.TABLE_PRODUCT_LIST+ " VALUES (?,?,?,?);";
        //compile the statement and start a transaction
        SQLiteStatement statement = mDatabase.compileStatement(sql);
        mDatabase.beginTransaction();
        for (int i = 0; i < listProducts.size(); i++) {
            Product currentproduct = listProducts.get(i);
            statement.clearBindings();
            //for a given column index, simply bind the data to be put inside that index
            statement.bindString(2, currentproduct.getTitle());
            statement.bindDouble(3,currentproduct.getPrice());
            statement.bindString(4, currentproduct.getThumbnail());

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
                ProductsHelper.COLUMN_TITLE,
                ProductsHelper.COLUMN_PRICE,
                ProductsHelper.COLUMN_IMAGE
        };
        Cursor cursor = mDatabase.query(ProductsHelper.TABLE_PRODUCT_LIST, columns, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            L.m("loading entries " + cursor.getCount() + new Date(System.currentTimeMillis()));
            do {

                Product product=new Product();

                product.setId(cursor.getInt(cursor.getColumnIndex(ProductsHelper.COLUMN_UID)));
                product.setTitle(cursor.getString(cursor.getColumnIndex(ProductsHelper.COLUMN_TITLE)));
                product.setPrice(cursor.getDouble(cursor.getColumnIndex(ProductsHelper.COLUMN_PRICE)));
                product.setThumbnail(cursor.getString(cursor.getColumnIndex(ProductsHelper.COLUMN_IMAGE)));

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
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_PRICE="price";
        public static final String COLUMN_IMAGE="image";

        private static final String CREATE_TABLE_PRODUCT_LIST = "CREATE TABLE " +TABLE_PRODUCT_LIST + " (" +
                COLUMN_UID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_TITLE + " TEXT," +
                COLUMN_PRICE+" DOUBLE,"+
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
