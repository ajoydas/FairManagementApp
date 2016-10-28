package ajoy.com.fairmanagementapp.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ajoy.com.fairmanagementapp.activities.ActivityFair;
import ajoy.com.fairmanagementapp.activities.ActivityProductMap;
import ajoy.com.fairmanagementapp.activities.ActivityStallMap;
import ajoy.com.fairmanagementapp.adapters.AdapterFavProducts;
import ajoy.com.fairmanagementapp.application.MyApplication;
import ajoy.com.fairmanagementapp.application.R;
import ajoy.com.fairmanagementapp.callbacks.ProductLoadedListener;
import ajoy.com.fairmanagementapp.database.DBFavProducts;
import ajoy.com.fairmanagementapp.extras.AsyncResponse;
import ajoy.com.fairmanagementapp.logging.L;
import ajoy.com.fairmanagementapp.objects.FavProduct;
import ajoy.com.fairmanagementapp.objects.Product;
import ajoy.com.fairmanagementapp.task.TaskLoadProducts;

public class FragmentFavourites  extends Fragment {

    private static final String STATE_FAV_PRODUCTS = "states_fav_products";
    private RecyclerView mRecyclerProducts;
    private AdapterFavProducts mAdapter;
    private ArrayList<FavProduct> mListFavProducts;
    private DateFormat mFormatter = new SimpleDateFormat("dd-MM-yyyy");
    private Button bviewRefresh;
    private int pos=0;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View layout = inflater.inflate(R.layout.fragment_favourites, container, false);
        mRecyclerProducts = (RecyclerView) layout.findViewById(R.id.listProducts);
        //set the layout manager before trying to display data
        mRecyclerProducts.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new AdapterFavProducts(getActivity());
        mRecyclerProducts.setAdapter(mAdapter);
        if (savedInstanceState != null) {

            mListFavProducts = savedInstanceState.getParcelableArrayList(STATE_FAV_PRODUCTS);
        }
        else {
            mListFavProducts = MyApplication.getWritableDatabaseFavProduct().readFavProducts();
        }
        mAdapter.setFavProducts(mListFavProducts);
        return layout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerProducts.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), mRecyclerProducts, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                editDialogShow(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }



    boolean isImageFitToScreen = false;
    Button bviewfav;
    Dialog dialog;
    TextView productName;
    TextView stallName;
    TextView productCompany;
    TextView productDescription;
    TextView productPrice;
    TextView productAvailability;
    ImageView productImage;
    TextView fairtitle;
    TextView location;
    TextView startdate;
    TextView enddate;
    TextView opentime;
    TextView closetime;

    private void editDialogShow(final int position) {
        dialog = new Dialog(getActivity());
        dialog.setTitle("Product Details");
        dialog.setContentView(R.layout.dialog_fav_products_details);
        productName = (TextView) dialog.findViewById(R.id.dialog_stall_product_name);
        stallName = (TextView) dialog.findViewById(R.id.dialog_stall_name);
        productCompany = (TextView) dialog.findViewById(R.id.dialog_stall_product_company);
        productDescription = (TextView) dialog.findViewById(R.id.dialog_stall_product_description);
        productPrice = (TextView) dialog.findViewById(R.id.dialog_stall_product_price);
        productAvailability = (TextView) dialog.findViewById(R.id.dialog_stall_product_availability);
        productImage = (ImageView) dialog.findViewById(R.id.dialog_stall_product_image);
        fairtitle=(TextView) dialog.findViewById(R.id.detailstitle);
        location=(TextView) dialog.findViewById(R.id.detailslocation);
        startdate=(TextView) dialog.findViewById(R.id.detailsstartdate);
        enddate=(TextView) dialog.findViewById(R.id.detailsenddate);
        opentime=(TextView) dialog.findViewById(R.id.detailsopentime);
        closetime=(TextView) dialog.findViewById(R.id.detailsclosetime);

        //productImage.setImageBitmap(StringToBitMap(mListFavProducts.get(position).getImage()));
        Glide.with(MyApplication.getAppContext()).load(mListFavProducts.get(position).getImage()).into(productImage);
        productName.setText(mListFavProducts.get(position).getName());
        stallName.setText(mListFavProducts.get(position).getStall());
        productCompany.setText(mListFavProducts.get(position).getCompany());
        productDescription.setText(mListFavProducts.get(position).getDescription());
        productPrice.setText(mListFavProducts.get(position).getPrice());
        productAvailability.setText(mListFavProducts.get(position).getAvailability());

        fairtitle.setText(mListFavProducts.get(position).getFair());
        location.setText(mListFavProducts.get(position).getLocation());

        Date date = mListFavProducts.get(position).getStart_date();
        startdate.setText(mFormatter.format(date));

        date = mListFavProducts.get(position).getEnd_date();
        enddate.setText(mFormatter.format(date));

        SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
        Time time=mListFavProducts.get(position).getOpen_time();
        opentime.setText(_12HourSDF.format(time));

        time=mListFavProducts.get(position).getClose_time();
        closetime.setText(_12HourSDF.format(time));

        dialog.show();
        Button bcancel = (Button) dialog.findViewById(R.id.bcancel);
        Button bviewstallmap = (Button) dialog.findViewById(R.id.bviewStallMap);
        bviewfav = (Button) dialog.findViewById(R.id.bviewFav);
        bviewRefresh = (Button) dialog.findViewById(R.id.bviewRefresh);
        /*if (MyApplication.getWritableDatabaseFavProduct().queryFavProducts(ActivityFair.fair.getDb_name() + "_" + mListFavProducts.get(position).getId())) {
            bviewfav.setText("Saved");
        } else {
            bviewfav.setText("Favourite");
        }*/

        final android.widget.LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) productImage.getLayoutParams();

        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isImageFitToScreen) {
                    isImageFitToScreen = false;
                    productImage.setLayoutParams(params);
                    productImage.setAdjustViewBounds(true);
                } else {
                    isImageFitToScreen = true;
                    productImage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    productImage.setScaleType(ImageView.ScaleType.FIT_XY);
                }
            }
        });


        bcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                L.t(MyApplication.getAppContext(), "Request Canceled");
                dialog.cancel();
            }
        });

        bviewstallmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListFavProducts.get(position).getImage() != null) {
                    Intent i = new Intent(MyApplication.getAppContext(), ActivityProductMap.class);
                    i.putExtra("Location", mListFavProducts.get(position).getStalllocation());
                    i.putExtra("Stallname", mListFavProducts.get(position).getStall());
                    i.putExtra("Image", mListFavProducts.get(position).getImage());
                    startActivity(i);
                } else {
                    Intent i = new Intent(MyApplication.getAppContext(), ActivityStallMap.class);
                    i.putExtra("Location", mListFavProducts.get(position).getStalllocation());
                    i.putExtra("Stallname", mListFavProducts.get(position).getStall());
                    startActivity(i);
                }
            }
        });

        bviewfav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(bviewfav.getText().toString());

                try {
                    if (MyApplication.getWritableDatabaseFavProduct().deleteIdentifier(mListFavProducts.get(position).getTable(),mListFavProducts.get(position).getProductid()) ){
                        L.t(MyApplication.getAppContext(), "Removed!");
                        mListFavProducts = MyApplication.getWritableDatabaseFavProduct().readFavProducts();
                        mAdapter.setFavProducts(mListFavProducts);
                        dialog.dismiss();
                    }
                } catch (Exception e) {
                    L.t(MyApplication.getAppContext(), "Failed to remove!Try again.");
                }

            }
        });

        bviewRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = ProgressDialog.show(getActivity(), "Refreshing...", "Please wait...", true, true);
                refreshProduct(position);
            }
        });
    }


    private void refreshProduct(int position) {


        class RefreshProduct extends AsyncTask<Void, Void, Boolean> {

            FavProduct product;
            private int position;
            public RefreshProduct(int position) {
                this.position = position;
                product=new FavProduct();
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }


            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                if(result) {
                    try {
                        Glide.with(MyApplication.getAppContext()).load(product.getImage()).asBitmap().into(new SimpleTarget() {
                            @Override
                            public void onResourceReady(Object resource, GlideAnimation glideAnimation) {
                                mListFavProducts.get(position).setFair(product.getFair());
                                mListFavProducts.get(position).setLocation(product.getLocation());
                                mListFavProducts.get(position).setStart_date(product.getStart_date());
                                mListFavProducts.get(position).setEnd_date(product.getEnd_date());
                                mListFavProducts.get(position).setOpen_time(product.getOpen_time());
                                mListFavProducts.get(position).setClose_time(product.getClose_time());
                                mListFavProducts.get(position).setStall(product.getStall());
                                mListFavProducts.get(position).setName(product.getName());
                                mListFavProducts.get(position).setCompany(product.getCompany());
                                mListFavProducts.get(position).setDescription(product.getDescription());
                                mListFavProducts.get(position).setPrice(product.getPrice());
                                mListFavProducts.get(position).setAvailability(product.getAvailability());
                                try {
                                    new File(mListFavProducts.get(position).getImage()).delete();
                                }catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                                mListFavProducts.get(position).setImage(saveToInternalStorage((Bitmap) resource));
                                mListFavProducts.get(position).setStalllocation(product.getStalllocation());
                                System.out.println(mListFavProducts.get(position).getImage());
                                MyApplication.getWritableDatabaseFavProduct().updateFavProducts(mListFavProducts.get(position));

                                //productImage.setImageBitmap(StringToBitMap(mListFavProducts.get(position).getImage()));
                                Glide.with(MyApplication.getAppContext()).load(mListFavProducts.get(position).getImage()).into(productImage);
                                productName.setText(mListFavProducts.get(position).getName());
                                stallName.setText(mListFavProducts.get(position).getStall());
                                productCompany.setText(mListFavProducts.get(position).getCompany());
                                productDescription.setText(mListFavProducts.get(position).getDescription());
                                productPrice.setText(mListFavProducts.get(position).getPrice());
                                productAvailability.setText(mListFavProducts.get(position).getAvailability());

                                fairtitle.setText(mListFavProducts.get(position).getFair());
                                location.setText(mListFavProducts.get(position).getLocation());

                                Date date = mListFavProducts.get(position).getStart_date();
                                startdate.setText(mFormatter.format(date));

                                date = mListFavProducts.get(position).getEnd_date();
                                enddate.setText(mFormatter.format(date));

                                SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
                                Time time=mListFavProducts.get(position).getOpen_time();
                                opentime.setText(_12HourSDF.format(time));

                                time=mListFavProducts.get(position).getClose_time();
                                closetime.setText(_12HourSDF.format(time));


                                mAdapter.notifyDataSetChanged();
                                progressDialog.dismiss();
                                L.t(MyApplication.getAppContext(), "Refreshed.");
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        L.t(MyApplication.getAppContext(), "Failed to Refresh!Try again.");
                    }
                }
            }

            private String saveToInternalStorage(Bitmap bitmapImage){
                ContextWrapper cw = new ContextWrapper(MyApplication.getAppContext());
                // path to /data/data/yourapp/app_data/imageDir
                File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                // Create imageDir
                String filename="product_"+System.currentTimeMillis()+".jpg";
                File mypath=new File(directory,filename);

                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(mypath);
                    // Use the compress method on the BitMap object to write image to the OutputStream
                    bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return directory.getAbsolutePath()+"/"+filename;
            }

            @Override
            protected Boolean doInBackground(Void... params) {

                Integer result = 0;
                try {
                    String st =  "select * from " + mListFavProducts.get(position).getTable() + "_products where id = '" + mListFavProducts.get(position).getProductid() + "' ";
                    System.out.println("Statement is "+st);

                    URL loadProductUrl = new URL("http://buetian14.com/fairmanagementapp/loadProducts.php");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) loadProductUrl.openConnection();
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
                    String stall="";
                    while (count < jsonArray.length()) {
                        JSONObject rs = jsonArray.getJSONObject(count);
                        stall=rs.getString("stall");
                        product.setName(rs.getString("name"));
                        product.setCompany(rs.getString("company"));
                        product.setDescription(rs.getString("description"));
                        product.setPrice(rs.getString("price"));
                        product.setAvailability(rs.getString("availability"));
                        product.setImage(rs.getString("image"));
                        count++;
                    }
                    URL loadProductUrl2 = new URL("http://buetian14.com/fairmanagementapp/searchFair.php");
                    HttpURLConnection httpURLConnection2 = (HttpURLConnection) loadProductUrl2.openConnection();
                    httpURLConnection2.setRequestMethod("POST");
                    httpURLConnection2.setDoOutput(true);
                    httpURLConnection2.setDoInput(true);
                    OutputStream outputStream2 = httpURLConnection2.getOutputStream();
                    BufferedWriter bufferedWriter2 = new BufferedWriter(new OutputStreamWriter(outputStream2, "UTF-8"));
                    String data2 = URLEncoder.encode("db_name", "UTF-8") + "=" + URLEncoder.encode(mListFavProducts.get(position).getTable(), "UTF-8");
                    bufferedWriter2.write(data2);
                    bufferedWriter2.flush();
                    bufferedWriter2.close();
                    outputStream2.close();

                    InputStream inputStream2 = httpURLConnection2.getInputStream();
                    BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(inputStream2));
                    StringBuilder stringBuilder2 = new StringBuilder();
                    String jsonString2;
                    while ((jsonString2 = bufferedReader2.readLine()) != null) {
                        stringBuilder2.append(jsonString2 + "\n");
                    }
                    bufferedReader2.close();
                    inputStream2.close();
                    httpURLConnection2.disconnect();
                    JSONObject jsonObject2 = new JSONObject(stringBuilder2.toString().trim());
                    JSONArray jsonArray2 = jsonObject2.getJSONArray("result");
                    int count2 = 0;
                    while (count2 < jsonArray.length()) {
                        JSONObject rs = jsonArray2.getJSONObject(count2);
                        product.setFair(rs.getString("title"));
                        product.setLocation(rs.getString("location"));
                        product.setStart_date(new SimpleDateFormat("yyyy-mm-dd").parse(rs.getString("start_date")));
                        product.setEnd_date(new SimpleDateFormat("yyyy-mm-dd").parse(rs.getString("end_date")));
                        product.setOpen_time(Time.valueOf(rs.getString("open_time")));
                        product.setClose_time(Time.valueOf(rs.getString("close_time")));
                        count2++;
                    }

                    st = "Select * from " + mListFavProducts.get(position).getTable() + "_stalls where stall='"+stall+"'";

                    URL loadProductUrl3 = new URL("http://buetian14.com/fairmanagementapp/loadStalls.php");
                    HttpURLConnection httpURLConnection3 = (HttpURLConnection) loadProductUrl3.openConnection();
                    System.out.println("Connected");

                    httpURLConnection3.setRequestMethod("POST");
                    httpURLConnection3.setDoOutput(true);
                    httpURLConnection3.setDoInput(true);
                    OutputStream outputStream3 = httpURLConnection3.getOutputStream();
                    BufferedWriter bufferedWriter3 = new BufferedWriter(new OutputStreamWriter(outputStream3, "UTF-8"));
                    String data3 = URLEncoder.encode("statement", "UTF-8") + "=" + URLEncoder.encode(st, "UTF-8");
                    bufferedWriter3.write(data3);
                    bufferedWriter3.flush();
                    bufferedWriter3.close();
                    outputStream3.close();

                    InputStream inputStream3 = httpURLConnection3.getInputStream();
                    BufferedReader bufferedReader3 = new BufferedReader(new InputStreamReader(inputStream3));
                    StringBuilder stringBuilder3 = new StringBuilder();
                    String jsonString3;
                    while ((jsonString3 = bufferedReader3.readLine()) != null) {
                        stringBuilder3.append(jsonString3 + "\n");
                    }
                    bufferedReader3.close();
                    inputStream3.close();
                    httpURLConnection3.disconnect();
                    JSONObject jsonObject3 = new JSONObject(stringBuilder3.toString().trim());
                    JSONArray jsonArray3 = jsonObject3.getJSONArray("result");
                    int count3 = 0;

                    JSONObject rs = jsonArray3.getJSONObject(count3);
                    product.setLocation(rs.getString("location"));
                    product.setStall(rs.getString("stall_name"));
                    return true;

                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }

        new RefreshProduct(position).execute();
    }


    //Touch
    public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private FragmentFavourites.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final FragmentFavourites.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }

    }
}
