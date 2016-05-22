package ajoy.com.fairmanagementapp.extras;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import  ajoy.com.fairmanagementapp.pojo.Product;


/**
 * Created by ajoy on 5/19/16.
 */
public class ProductSorter {
    public void sortProductsByName(ArrayList<Product> products){
        Collections.sort(products, new Comparator<Product>() {
            @Override
            public int compare(Product lhs, Product rhs) {
                return lhs.getTitle().compareTo(rhs.getTitle());
            }
        });
    }
    public void sortProductsByPrice(ArrayList<Product> products){

        Collections.sort(products, new Comparator<Product>() {
            @Override
            public int compare(Product lhs, Product rhs) {
                if(lhs.getPrice()>rhs.getPrice())
                {
                    return 1;
                }
                else
                    return -1;

            }
        });
    }
}
