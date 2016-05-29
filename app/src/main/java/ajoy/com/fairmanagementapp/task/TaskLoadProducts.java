package ajoy.com.fairmanagementapp.task;

import android.os.AsyncTask;

import java.util.ArrayList;

import  ajoy.com.fairmanagementapp.callbacks.ProductLoadedListener;
import  ajoy.com.fairmanagementapp.extras.MovieUtils;
import  ajoy.com.fairmanagementapp.pojo.Product;

/**
 * Created by ajoy on 5/19/16.
 */
public class TaskLoadProducts extends AsyncTask<Void,Void,ArrayList<Product>> {

    private ProductLoadedListener myComponent;
    String fair_db;
    String query;
    int option;

    public TaskLoadProducts(ProductLoadedListener myComponent, String fair_db, String query, int option) {
        this.myComponent = myComponent;
        this.fair_db = fair_db;
        this.query = query;
        this.option = option;
    }

    @Override
    protected ArrayList<Product> doInBackground(Void... params) {

        ArrayList<Product> listProducts = MovieUtils.loadSearchProducts(fair_db,query,option);
        return listProducts;
    }

    @Override
    protected void onPostExecute(ArrayList<Product> listMovies) {
        if (myComponent != null) {
            myComponent.onProductLoaded(listMovies);
        }
    }


}
