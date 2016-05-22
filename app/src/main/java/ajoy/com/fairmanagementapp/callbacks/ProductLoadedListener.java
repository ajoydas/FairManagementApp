package ajoy.com.fairmanagementapp.callbacks;

import java.util.ArrayList;

import ajoy.com.fairmanagementapp.pojo.Product;


/**
 * Created by ajoy on 5/19/16.
 */
public interface ProductLoadedListener {
    public void onProductLoaded(ArrayList<Product> listProducts);

}
