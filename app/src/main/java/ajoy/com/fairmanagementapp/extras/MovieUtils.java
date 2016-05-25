package ajoy.com.fairmanagementapp.extras;

import com.android.volley.RequestQueue;

import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ajoy.com.fairmanagementapp.database.DBFairs;
import ajoy.com.fairmanagementapp.database.DBMovies;
import ajoy.com.fairmanagementapp.database.DBProducts;
import ajoy.com.fairmanagementapp.json.Endpoints;
import ajoy.com.fairmanagementapp.json.Parser;
import ajoy.com.fairmanagementapp.json.Requestor;
import ajoy.com.fairmanagementapp.materialtest.MyApplication;
import ajoy.com.fairmanagementapp.pojo.Fair;
import ajoy.com.fairmanagementapp.pojo.Movie;
import ajoy.com.fairmanagementapp.pojo.Product;

/**
 * Created by Windows on 02-03-2015.
 */
public class MovieUtils {
    private static final String url = "jdbc:mysql://192.168.0.100:3306/";
    private static final String username="ajoy";
    private static final String password="ajoydas";

    public static ArrayList<Movie> loadBoxOfficeMovies(RequestQueue requestQueue) {
        JSONObject response = Requestor.requestMoviesJSON(requestQueue, Endpoints.getRequestUrlBoxOfficeMovies(30));
        ArrayList<Movie> listMovies = Parser.parseMoviesJSON(response);
        MyApplication.getWritableDatabaseMovie().insertMovies(DBMovies.BOX_OFFICE, listMovies, true);
        return listMovies;
    }

    public static ArrayList<Movie> loadUpcomingMovies(RequestQueue requestQueue) {
        JSONObject response = Requestor.requestMoviesJSON(requestQueue, Endpoints.getRequestUrlUpcomingMovies(30));
        ArrayList<Movie> listMovies = Parser.parseMoviesJSON(response);
        MyApplication.getWritableDatabaseMovie().insertMovies(DBMovies.UPCOMING, listMovies, true);
        return listMovies;
    }

    public static ArrayList<Product> loadProducts()
    {
        ArrayList<Product> listProducts = new ArrayList<>();
        String Url=url+"logindatabase";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(Url, username, password);

            System.out.println("Connected");

            PreparedStatement st = con.prepareStatement("select * from images");
            //PreparedStatement stcount = con.prepareStatement("select count(*) from images where id = ?");

            //st.setInt(1,Integer.parseInt(id));
            //stcount.setInt(1,Integer.parseInt(id));

            System.out.println("Statement");

            ResultSet rs = null,rscount=null;

            rs = st.executeQuery();
            //rscount=st.executeQuery();
            int rowcount=0;
            if (rs.last()) {
                rowcount = rs.getRow();
                rs.beforeFirst(); // not rs.first() because the rs.next() below will move on, missing the first element
            }

            System.out.println("Count: "+rowcount);

            if(rowcount==0)
            {
                System.out.println(rowcount);

                return null;
            }
            else {

                while (rs.next()) {
                    Product product=new Product();
                    product.setId(rs.getInt("id"));
                    product.setTitle(rs.getString("title"));
                    product.setPrice(rs.getDouble("price"));
                    product.setThumbnail(rs.getString("image"));

                    listProducts.add(product);
                }
                MyApplication.getWritableDatabaseProduct().insertProducts(DBProducts.ProductList, listProducts, true);
                return listProducts;
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static ArrayList<Fair> loadFairs(int table) {

        System.out.println("Loading");
        ArrayList<Fair> listFairs = new ArrayList<>();
        String Url=url+"fairinfo";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(Url, username, password);

            System.out.println("Connected");

            PreparedStatement st = con.prepareStatement("select * from fairs");
            //PreparedStatement stcount = con.prepareStatement("select count(*) from images where id = ?");

            //st.setInt(1,Integer.parseInt(id));
            //stcount.setInt(1,Integer.parseInt(id));

            System.out.println("Statement");

            ResultSet rs = null,rscount=null;

            rs = st.executeQuery();
            //rscount=st.executeQuery();
            int rowcount=0;
            if (rs.last()) {
                rowcount = rs.getRow();
                rs.beforeFirst(); // not rs.first() because the rs.next() below will move on, missing the first element
            }

            System.out.println("Count: "+rowcount);

            if(rowcount==0)
            {
                System.out.println(rowcount);

                return null;
            }
            else {

                while (rs.next()) {
                    Fair fair=new Fair();
                    fair.setId(rs.getInt("id"));
                    fair.setDb_name(rs.getString("db_name"));
                    fair.setTitle(rs.getString("title"));
                    fair.setOrganizer(rs.getString("organizer"));
                    fair.setLocation(rs.getString("location"));
                    fair.setStart_date(rs.getDate("start_date"));
                    fair.setEnd_date(rs.getDate("end_date"));
                    fair.setOpen_time(rs.getTime("open_time"));
                    fair.setClose_time(rs.getTime("close_time"));
                    fair.setMap_address(rs.getString("map_address"));

                    Calendar c = Calendar.getInstance();
                    Date date=c.getTime();

                    if(table==1)
                    {
                        if((fair.getStart_date()).compareTo(date)==-1)listFairs.add(fair);
                    }
                    else if(table==2)
                    {
                        if(fair.getStart_date().compareTo(date)!=-1)listFairs.add(fair);
                    }

                    System.out.println("Loading again");

                }
                MyApplication.getWritableDatabaseFair().insertFairs((table == 1 ? DBFairs.RUNNING_FAIR : DBFairs.UPCOMING_FAIR), listFairs, true);
                System.out.println("Done");
                return listFairs;
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }

    }
}
