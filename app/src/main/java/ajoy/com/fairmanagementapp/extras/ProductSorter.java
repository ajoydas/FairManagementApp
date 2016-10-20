package ajoy.com.fairmanagementapp.extras;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import  ajoy.com.fairmanagementapp.objects.Product;


/**
 * Created by ajoy on 5/19/16.
 */
public class ProductSorter {
    public void sortProductsByName(ArrayList<Product> products){
        Collections.sort(products, new Comparator<Product>() {
            @Override
            public int compare(Product lhs, Product rhs) {

                return (lhs.getName().toLowerCase()).compareTo(rhs.getName().toLowerCase());
            }
        });
    }
    public void sortProductsByPrice(ArrayList<Product> products){

        Collections.sort(products, new Comparator<Product>() {
            @Override
            public int compare(Product lhs, Product rhs) {
                double ls=Double.parseDouble(lhs.getPrice());
                double rs=Double.parseDouble(rhs.getPrice());

                if(ls>rs)
                {
                    return 1;
                }
                else
                    return -1;

            }
        });
    }

    public  void sortProductsByAvailability(ArrayList<Product> products)
    {
        Collections.sort(products, new Comparator<Product>() {
            @Override
            public int compare(Product lhs, Product rhs) {
                int ls=0,rs=0;
                if(lhs.getAvailability().equals("Medium"))ls=1;
                else if(lhs.getAvailability().equals("Low"))ls=2;
                else if(lhs.getAvailability().equals("Out of Stock"))ls=3;
                if(rhs.getAvailability().equals("Medium"))rs=1;
                if(rhs.getAvailability().equals("Low"))rs=2;
                if(rhs.getAvailability().equals("Out of Stock"))rs=3;

                if(ls>rs)
                {
                    return 1;
                }
                else
                    return -1;

            }
        });
    }
}
