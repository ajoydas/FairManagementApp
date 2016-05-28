package ajoy.com.fairmanagementapp.task;

import android.os.AsyncTask;

import java.util.ArrayList;

import ajoy.com.fairmanagementapp.callbacks.ProductLoadedListener;
import ajoy.com.fairmanagementapp.extras.MovieUtils;
import ajoy.com.fairmanagementapp.pojo.Product;

/**
 * Created by ajoy on 5/27/16.
 */
public class TaskLoadStallProducts extends AsyncTask<Void,Void,ArrayList<Product>> {

    private ProductLoadedListener myComponent;
    String fair_db;
    String stallname;
    String query;

    public TaskLoadStallProducts(ProductLoadedListener myComponent, String fair_db, String stallname, String query) {
        this.myComponent = myComponent;
        this.fair_db = fair_db;
        this.stallname = stallname;
        this.query = query;
    }

    @Override
    protected ArrayList<Product> doInBackground(Void... params) {

        ArrayList<Product> listProducts = MovieUtils.loadStallProducts(fair_db,stallname,query);
        return listProducts;
    }

    @Override
    protected void onPostExecute(ArrayList<Product> listMovies) {
        if (myComponent != null) {
            myComponent.onProductLoaded(listMovies);
        }
    }
}
