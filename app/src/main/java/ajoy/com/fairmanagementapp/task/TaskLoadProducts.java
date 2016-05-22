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

    public TaskLoadProducts(ProductLoadedListener myComponent) {

        this.myComponent = myComponent;

    }


    @Override
    protected ArrayList<Product> doInBackground(Void... params) {

        ArrayList<Product> listProducts = MovieUtils.loadProducts();
        return listProducts;
    }

    @Override
    protected void onPostExecute(ArrayList<Product> listMovies) {
        if (myComponent != null) {
            myComponent.onProductLoaded(listMovies);
        }
    }


}
