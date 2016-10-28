package ajoy.com.fairmanagementapp.extras;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ajoy.com.fairmanagementapp.activities.ActivityMain;
import ajoy.com.fairmanagementapp.database.DBFairs;
import ajoy.com.fairmanagementapp.database.DBProducts;
import ajoy.com.fairmanagementapp.database.DBStalls;
import ajoy.com.fairmanagementapp.application.MyApplication;
import ajoy.com.fairmanagementapp.objects.Employee;
import ajoy.com.fairmanagementapp.objects.Fair;
import ajoy.com.fairmanagementapp.objects.FavProduct;
import ajoy.com.fairmanagementapp.objects.Product;
import ajoy.com.fairmanagementapp.objects.Sell;
import ajoy.com.fairmanagementapp.objects.Stall;


public class FairUtils {
    private static final String url = ActivityMain.Server;

    public static ArrayList<Product> loadStallProducts(String fair_db, String stallname, String query, int option) {
        ArrayList<Product> listProducts = new ArrayList<>();
        String st = null;
        try {
            if (query == null || query.equals("") || option == 0) {
                st = "select * from " + fair_db + "_products where stall='" + stallname + "' limit "+ActivityMain.Count;
            } else if (option == 1) {
                st = "select * from " + fair_db + "_products where stall='" + stallname + "' and name like '%" + query + "%' ";
            } else if (option == 2) {
                st = "select * from " + fair_db + "_products where stall='" + stallname + "' and company like '%" + query + "%' ";
            }

            System.out.println("Statement");

            URL loadProductUrl = new URL(url + "loadProducts.php");
            HttpURLConnection httpURLConnection = (HttpURLConnection) loadProductUrl.openConnection();
            System.out.println("Connected\nQuery: " + query);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String data = URLEncoder.encode("statement", "UTF-8") + "=" + URLEncoder.encode(st, "UTF-8");
            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String jsonString;
            while ((jsonString = bufferedReader.readLine()) != null) {
                stringBuilder.append(jsonString + "\n");
            }
            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();
            JSONObject jsonObject = new JSONObject(stringBuilder.toString().trim());
            JSONArray jsonArray = jsonObject.getJSONArray("result");
            int count = 0;
            while (count < jsonArray.length()) {
                JSONObject rs = jsonArray.getJSONObject(count);
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

                count++;
            }
            if(listProducts.size()==0)
            {
                Product product=new Product();
                product.setId(-1);
                listProducts.add(product);
            }
            return listProducts;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    public static ArrayList<Product> loadSearchProducts(String fair_db, String query, int option) {
        ArrayList<Product> listProducts = new ArrayList<>();
        String st = null;
        try {
            System.out.println("Connected\nQuery: " + query);

            if (query == null || query.equals("") || option == 0) {
                st = "select * from " + fair_db + "_products limit "+ActivityMain.Count;

            } else if (option == 1) {
                st = "select * from " + fair_db + "_products where name like '%" + query + "%' ";
            } else if (option == 2) {
                st = "select * from " + fair_db + "_products where company like '%" + query + "%' ";
            }else if (option==3)
            {
                st = "select * from " + fair_db + "_products where id = '" + query + "' ";
            }

            URL loadProductUrl = new URL(url + "loadProducts.php");
            HttpURLConnection httpURLConnection = (HttpURLConnection) loadProductUrl.openConnection();
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String data = URLEncoder.encode("statement", "UTF-8") + "=" + URLEncoder.encode(st, "UTF-8");
            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String jsonString;
            while ((jsonString = bufferedReader.readLine()) != null) {
                stringBuilder.append(jsonString + "\n");
            }
            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();
            JSONObject jsonObject = new JSONObject(stringBuilder.toString().trim());
            JSONArray jsonArray = jsonObject.getJSONArray("result");
            int count = 0;
            while (count < jsonArray.length()) {
                JSONObject rs = jsonArray.getJSONObject(count);
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

                count++;
            }
            if(listProducts.size()==0)
            {
                Product product=new Product();
                product.setId(-1);
                listProducts.add(product);
            }
            return listProducts;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    public static ArrayList<Fair> loadFairs(int table) {

        System.out.println("Loading");
        ArrayList<Fair> listFairs = new ArrayList<>();
        String Url = url;
        try {
            URL loadFairUrl = new URL(url + "loadfairs.php");
            HttpURLConnection httpURLConnection = (HttpURLConnection) loadFairUrl.openConnection();
            httpURLConnection.setConnectTimeout(10000);
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String jsonString;
            while ((jsonString = bufferedReader.readLine()) != null) {
                stringBuilder.append(jsonString + "\n");
            }
            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();
            JSONObject jsonObject = new JSONObject(stringBuilder.toString().trim());
            JSONArray jsonArray = jsonObject.getJSONArray("result");
            int count = 0;
            while (count < jsonArray.length()) {
                JSONObject rs = jsonArray.getJSONObject(count);
                Fair fair = new Fair();
                fair.setId(rs.getInt("id"));
                fair.setDb_name(rs.getString("db_name"));
                fair.setTitle(rs.getString("title"));
                fair.setOrganizer(rs.getString("organizer"));
                fair.setLocation(rs.getString("location"));
                //System.out.println(rs.getString("start_date"));
                //System.out.println(new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(rs.getString("start_date")));
                fair.setStart_date(new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(rs.getString("start_date")));
                fair.setEnd_date(new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(rs.getString("end_date")));
                fair.setOpen_time(Time.valueOf(rs.getString("open_time")));
                fair.setClose_time(Time.valueOf(rs.getString("close_time")));
                fair.setMap_address(rs.getString("map_address"));

                System.out.println(fair.getMap_address());

                Calendar c = Calendar.getInstance();
                Date date = c.getTime();

                if (table == 1) {
                    if ((fair.getStart_date()).compareTo(date) == -1 && (fair.getEnd_date()).compareTo(date) == 1) listFairs.add(fair);
                } else if (table == 2) {
                    if (fair.getStart_date().compareTo(date) != -1) listFairs.add(fair);
                }
                count++;
            }
            if(listFairs.size()==0)
            {
                Fair fair=new Fair();
                fair.setId(-1);
                listFairs.add(fair);
            }
            return listFairs;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static ArrayList<Stall> loadSearchStall(String fair_db, String query) {

        ArrayList<Stall> listStalls = new ArrayList<>();
        String Url = url;
        //Statement st=null;
        String st = null;
        try {
            System.out.println("Connected\nQuery: " + query);

            if (query == null || query.equals("")) {
                st = "select * from " + fair_db + "_stalls WHERE stall_name is not null and location is not null and owner is not null and description is not null";

            } else {
                st = "select * from  " + fair_db + "_stalls where stall_name like '%" + query + "%' and stall_name is not null and location is not null and owner is not null and description is not null";
            }

            System.out.println("Statement");


            URL loadProductUrl = new URL(url + "loadStalls.php");
            HttpURLConnection httpURLConnection = (HttpURLConnection) loadProductUrl.openConnection();
            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String data = URLEncoder.encode("statement", "UTF-8") + "=" + URLEncoder.encode(st, "UTF-8");
            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String jsonString;
            while ((jsonString = bufferedReader.readLine()) != null) {
                stringBuilder.append(jsonString + "\n");
            }
            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();
            JSONObject jsonObject = new JSONObject(stringBuilder.toString().trim());
            JSONArray jsonArray = jsonObject.getJSONArray("result");
            int count = 0;
            while (count < jsonArray.length()) {
                JSONObject rs = jsonArray.getJSONObject(count);
                Stall stall = new Stall();
                stall.setId(rs.getInt("id"));
                stall.setStall(rs.getString("stall"));
                stall.setStall_name(rs.getString("stall_name"));
                stall.setOwner(rs.getString("owner"));
                stall.setDescription(rs.getString("description"));
                stall.setLocation(rs.getString("location"));
                listStalls.add(stall);

                count++;
            }
            if(listStalls.size()==0)
            {
                Stall stall=new Stall();
                stall.setId(-1);
                listStalls.add(stall);
            }
            return listStalls;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static ArrayList<Employee> loadEmployees(String fair_db, String stallname, String query) {
        ArrayList<Employee> listEmployees = new ArrayList<>();
        String Url = url;
        //Statement st=null;
        String st = null;
        try {


            if (query == null || query.equals("")) {
                st = "select * from " + fair_db + "_employees where stall='" + stallname + "'";
            } else {
                st = "select * from " + fair_db + "_employees where stall='" + stallname + "' and name like '%" + query + "%' ";
            }
            System.out.println("Statement");

            URL loadProductUrl = new URL(url + "loadEmployees.php");
            HttpURLConnection httpURLConnection = (HttpURLConnection) loadProductUrl.openConnection();
            System.out.println("Connected\nQuery: " + query);
            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String data = URLEncoder.encode("statement", "UTF-8") + "=" + URLEncoder.encode(st, "UTF-8");
            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String jsonString;
            while ((jsonString = bufferedReader.readLine()) != null) {
                stringBuilder.append(jsonString + "\n");
            }
            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();
            JSONObject jsonObject = new JSONObject(stringBuilder.toString().trim());
            JSONArray jsonArray = jsonObject.getJSONArray("result");
            int count = 0;
            while (count < jsonArray.length()) {
                JSONObject rs = jsonArray.getJSONObject(count);
                Employee employee = new Employee();
                employee.setId(rs.getInt("id"));
                employee.setStall(rs.getString("stall"));
                employee.setName(rs.getString("name"));
                employee.setDescription(rs.getString("description"));
                employee.setContact_no(rs.getString("contact_no"));
                employee.setPosition(rs.getString("position"));
                employee.setSalary(rs.getString("salary"));
                listEmployees.add(employee);
                count++;
            }
            if(listEmployees.size()==0)
            {
                Employee employee=new Employee();
                employee.setId(-1);
                listEmployees.add(employee);
            }
            return listEmployees;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static ArrayList<Sell> loadSells(String fair_db, String stallname, String query) {
        ArrayList<Sell> listSells = new ArrayList<>();
        String st = null;
        try {
            if (query == null || query.equals("")) {
                st = "select * from " + fair_db + "_sells where stall='"+stallname+"'";

            } else {
                st = "select * from " + fair_db + "_sells where stall='"+stallname+"' and employee_name like '%" + query + "%' ";
            }
            System.out.println("Statement");

            URL loadProductUrl = new URL(url + "loadSells.php");
            HttpURLConnection httpURLConnection = (HttpURLConnection) loadProductUrl.openConnection();
            System.out.println("Connected\nQuery: " + query);
            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String data = URLEncoder.encode("statement", "UTF-8") + "=" + URLEncoder.encode(st, "UTF-8");
            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String jsonString;
            while ((jsonString = bufferedReader.readLine()) != null) {
                stringBuilder.append(jsonString + "\n");
            }
            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();
            JSONObject jsonObject = new JSONObject(stringBuilder.toString().trim());
            JSONArray jsonArray = jsonObject.getJSONArray("result");
            int count = 0;
            while (count < jsonArray.length()) {
                JSONObject rs = jsonArray.getJSONObject(count);
                Sell sell = new Sell();
                sell.setId(rs.getInt("id"));
                sell.setStall(rs.getString("stall"));
                sell.setProduct_name(rs.getString("product_name"));
                sell.setEmployee_name(rs.getString("employee_name"));
                sell.setDate(rs.getString("date"));
                sell.setTime(rs.getString("time"));
                sell.setPrice(rs.getString("price"));
                sell.setDescription(rs.getString("description"));
                System.out.println(sell);
                listSells.add(sell);
                count++;
            }
            if(listSells.size()==0)
            {
                Sell sell=new Sell();
                sell.setId(-1);
                listSells.add(sell);
            }
            return listSells;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
