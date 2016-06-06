package ajoy.com.fairmanagementapp.extras;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ajoy.com.fairmanagementapp.database.DBFairs;
import ajoy.com.fairmanagementapp.database.DBProducts;
import ajoy.com.fairmanagementapp.database.DBStalls;
import ajoy.com.fairmanagementapp.materialtest.MyApplication;
import ajoy.com.fairmanagementapp.pojo.Employee;
import ajoy.com.fairmanagementapp.pojo.Fair;
import ajoy.com.fairmanagementapp.pojo.Product;
import ajoy.com.fairmanagementapp.pojo.Sell;
import ajoy.com.fairmanagementapp.pojo.Stall;

/**
 * Created by Windows on 02-03-2015.
 */
public class FairUtils {
    private static final String url = "jdbc:mysql://162.221.186.242:3306/buetian1_fairinfo";
    private static final String username = "buetian1_ajoy";
    private static final String password = "termjan2016";

    public static ArrayList<Product> loadStallProducts(String fair_db, String stallname, String query, int option) {
        ArrayList<Product> listProducts = new ArrayList<>();
        String Url = url;
        PreparedStatement st = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(Url, username, password);

            System.out.println("Connected\nQuery: " + query);

            if (query == null || query.equals("") || option == 0) {
                st = con.prepareStatement("select * from "+fair_db+"_products where stall=?");
                //PreparedStatement stcount = con.prepareStatement("select count(*) from images where id = ?");
                st.setString(1, stallname);

            } else if (option == 1) {
                st = con.prepareStatement("select * from "+fair_db+"_products where stall=? and name like '%" + query + "%' ");
                st.setString(1, stallname);
            } else if (option == 2) {
                st = con.prepareStatement("select * from "+fair_db+"_products where stall=? and company like '%" + query + "%' ");
                st.setString(1, stallname);
            }

            System.out.println("Statement");

            ResultSet rs = null, rscount = null;

            rs = st.executeQuery();
            //rscount=st.executeQuery();
            int rowcount = 0;
            if (rs.last()) {
                rowcount = rs.getRow();
                rs.beforeFirst(); // not rs.first() because the rs.next() below will move on, missing the first element
            }

            System.out.println("Count: " + rowcount);

            if (rowcount == 0) {
                System.out.println(rowcount);

                return null;
            } else {

                while (rs.next()) {
                    Product product = new Product();
                    product.setId(rs.getInt("id"));
                    product.setStall(rs.getString("stall"));
                    product.setName(rs.getString("name"));
                    product.setCompany(rs.getString("company"));
                    product.setDescription(rs.getString("description"));
                    product.setPrice(rs.getString("price"));
                    product.setAvailability(rs.getString("availability"));
                    product.setImage(rs.getString("image"));

                    listProducts.add(product);
                }
                //MyApplication.getWritableDatabaseProduct().insertProducts(DBProducts.ProductList, listProducts, true);
                con.close();
                return listProducts;
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }

    }


    public static ArrayList<Product> loadSearchProducts(String fair_db, String query, int option) {
        ArrayList<Product> listProducts = new ArrayList<>();
        String Url = url;
        //Statement st=null;
        PreparedStatement st = null;
        ResultSet rs = null, rscount = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(Url, username, password);

            System.out.println("Connected\nQuery: " + query);

            if (query == null || query.equals("") || option == 0) {
                st = con.prepareStatement("select * from "+fair_db+"_products");

            } else if (option == 1) {
                st = con.prepareStatement("select * from "+fair_db+"_products where name like '%" + query + "%' ");
            } else if (option == 2) {
                st = con.prepareStatement("select * from "+fair_db+"_products where company like '%" + query + "%' ");
            }


            System.out.println("Statement");

            rs = st.executeQuery();

            int rowcount = 0;
            if (rs.last()) {
                rowcount = rs.getRow();
                rs.beforeFirst(); // not rs.first() because the rs.next() below will move on, missing the first element
            }

            System.out.println("Count: " + rowcount);

            if (rowcount == 0) {
                System.out.println(rowcount);

                return null;
            } else {

                while (rs.next()) {
                    Product product = new Product();
                    product.setId(rs.getInt("id"));
                    product.setStall(rs.getString("stall"));
                    product.setName(rs.getString("name"));
                    product.setCompany(rs.getString("company"));
                    product.setDescription(rs.getString("description"));
                    product.setPrice(rs.getString("price"));
                    product.setAvailability(rs.getString("availability"));
                    product.setImage(rs.getString("image"));

                    listProducts.add(product);
                }
                MyApplication.getWritableDatabaseProduct().insertProducts(DBProducts.ProductList, listProducts, true);
                con.close();
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
        String Url = url;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(Url, username, password);

            System.out.println("Connected");

            PreparedStatement st = con.prepareStatement("select * from fairs");

            System.out.println("Statement");

            ResultSet rs = null, rscount = null;

            rs = st.executeQuery();
            //rscount=st.executeQuery();
            int rowcount = 0;
            if (rs.last()) {
                rowcount = rs.getRow();
                rs.beforeFirst(); // not rs.first() because the rs.next() below will move on, missing the first element
            }

            System.out.println("Count: " + rowcount);

            if (rowcount == 0) {
                System.out.println(rowcount);

                return null;
            } else {

                while (rs.next()) {
                    Fair fair = new Fair();
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
                    Date date = c.getTime();

                    if (table == 1) {
                        if ((fair.getStart_date()).compareTo(date) == -1) listFairs.add(fair);
                    } else if (table == 2) {
                        if (fair.getStart_date().compareTo(date) != -1) listFairs.add(fair);
                    }

                    System.out.println("Loading again");

                }
                MyApplication.getWritableDatabaseFair().insertFairs((table == 1 ? DBFairs.RUNNING_FAIR : DBFairs.UPCOMING_FAIR), listFairs, true);
                System.out.println("Done");
                con.close();
                return listFairs;
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static ArrayList<Stall> loadSearchStall(String fair_db, String query) {

        ArrayList<Stall> listStalls = new ArrayList<>();
        String Url = url;
        //Statement st=null;
        PreparedStatement st = null;
        ResultSet rs = null, rscount = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(Url, username, password);

            System.out.println("Connected\nQuery: " + query);

            if (query == null || query.equals("")) {
                st = con.prepareStatement("select * from "+fair_db+"_stalls WHERE stall_name is not null and location is not null and owner is not null and description is not null");

            } else {
                st = con.prepareStatement("select * from  "+fair_db+"_stalls where stall_name like '%" + query + "%' and stall_name is not null and location is not null and owner is not null and description is not null");
            }

            System.out.println("Statement");

            rs = st.executeQuery();
            int rowcount = 0;
            if (rs.last()) {
                rowcount = rs.getRow();
                rs.beforeFirst(); // not rs.first() because the rs.next() below will move on, missing the first element
            }

            System.out.println("Count: " + rowcount);

            if (rowcount == 0) {
                System.out.println(rowcount);

                return null;
            } else {

                while (rs.next()) {
                    Stall stall = new Stall();
                    stall.setId(rs.getInt("id"));
                    stall.setStall(rs.getString("stall"));
                    stall.setStall_name(rs.getString("stall_name"));
                    stall.setOwner(rs.getString("owner"));
                    stall.setDescription(rs.getString("description"));
                    stall.setLocation(rs.getString("location"));
                    listStalls.add(stall);
                }
                MyApplication.getWritableDatabaseStall().insertStalls(DBStalls.StallList, listStalls, true);
                con.close();
                return listStalls;
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static ArrayList<Employee> loadEmployees(String fair_db, String stallname, String query) {
        ArrayList<Employee> listEmployees = new ArrayList<>();
        String Url = url;
        //Statement st=null;
        PreparedStatement st = null;
        ResultSet rs = null, rscount = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(Url, username, password);

            System.out.println("Connected\nQuery: " + query);

            if (query == null || query.equals("")) {
                st = con.prepareStatement("select * from "+fair_db+"_employees");

            } else {
                st = con.prepareStatement("select * from "+fair_db+"_employees where name like '%" + query + "%' ");
            }


            System.out.println("Statement");

            rs = st.executeQuery();
            int rowcount = 0;
            if (rs.last()) {
                rowcount = rs.getRow();
                rs.beforeFirst(); // not rs.first() because the rs.next() below will move on, missing the first element
            }

            System.out.println("Count: " + rowcount);

            if (rowcount == 0) {
                System.out.println(rowcount);

                return null;
            } else {

                while (rs.next()) {
                    Employee employee = new Employee();
                    employee.setId(rs.getInt("id"));
                    employee.setStall(rs.getString("stall"));
                    employee.setName(rs.getString("name"));
                    employee.setDescription(rs.getString("description"));
                    employee.setContact_no(rs.getString("contact_no"));
                    employee.setPosition(rs.getString("position"));
                    employee.setSalary(rs.getString("salary"));

                    listEmployees.add(employee);
                }
                con.close();
                return listEmployees;
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static ArrayList<Sell> loadSells(String fair_db, String stallname, String query) {
        ArrayList<Sell> listSells = new ArrayList<>();
        String Url = url;
        //Statement st=null;
        PreparedStatement st = null;
        ResultSet rs = null, rscount = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(Url, username, password);

            System.out.println("Connected\nQuery: " + query);

            if (query == null || query.equals("")) {
                st = con.prepareStatement("select * from "+fair_db+"_sells");

            } else {
                st = con.prepareStatement("select * from "+fair_db+"_sells where employee_name like '%" + query + "%' ");
            }

            System.out.println("Statement");

            rs = st.executeQuery();
            int rowcount = 0;
            if (rs.last()) {
                rowcount = rs.getRow();
                rs.beforeFirst(); // not rs.first() because the rs.next() below will move on, missing the first element
            }

            System.out.println("Count: " + rowcount);

            if (rowcount == 0) {
                System.out.println(rowcount);

                return null;
            } else {

                while (rs.next()) {
                    Sell sell = new Sell();
                    sell.setId(rs.getInt("id"));
                    sell.setStall(rs.getString("stall"));
                    sell.setProduct_name(rs.getString("product_name"));
                    sell.setEmployee_name(rs.getString("employee_name"));
                    sell.setDate(rs.getString("date"));
                    sell.setTime(rs.getString("time"));
                    sell.setPrice(rs.getString("price"));
                    sell.setDescription(rs.getString("description"));

                    listSells.add(sell);
                }
                System.out.println(listSells);
                con.close();
                return listSells;
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }

    }
}
