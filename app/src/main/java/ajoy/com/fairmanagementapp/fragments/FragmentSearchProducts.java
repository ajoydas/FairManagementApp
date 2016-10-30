package ajoy.com.fairmanagementapp.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Base64;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
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
import java.util.ArrayList;

import ajoy.com.fairmanagementapp.activities.ActivityFair;
import ajoy.com.fairmanagementapp.activities.ActivityMain;
import ajoy.com.fairmanagementapp.activities.ActivityProductMap;
import ajoy.com.fairmanagementapp.activities.ActivityStallMap;
import ajoy.com.fairmanagementapp.adapters.AdapterProducts;
import ajoy.com.fairmanagementapp.callbacks.ProductLoadedListener;
import ajoy.com.fairmanagementapp.database.DBFavProducts;
import ajoy.com.fairmanagementapp.extras.AsyncResponse;
import ajoy.com.fairmanagementapp.extras.ProductSorter;
import ajoy.com.fairmanagementapp.extras.SortListener;
import ajoy.com.fairmanagementapp.logging.L;
import ajoy.com.fairmanagementapp.application.MyApplication;
import ajoy.com.fairmanagementapp.application.R;
import ajoy.com.fairmanagementapp.objects.FavProduct;
import ajoy.com.fairmanagementapp.objects.Product;
import ajoy.com.fairmanagementapp.task.TaskLoadProducts;

/**
 * Created by ajoy on 5/24/16.
 */

public class FragmentSearchProducts extends Fragment implements AsyncResponse, SortListener, View.OnClickListener, ProductLoadedListener, SwipeRefreshLayout.OnRefreshListener {
    private static EditText searchView;
    private static RadioGroup radioGroup;

    private static final String STATE_STALL_PRODUCTS = "states_search_products";
    protected ArrayList<Product> mListProducts;
    private AdapterProducts mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private RecyclerView mRecyclerProducts;

    private ProductSorter mSorter = new ProductSorter();

    private String search;
    private int option;
    private String location;
    private String stallname;
    private boolean res = false;
    int pos;
    private ProgressBar mProgressBar;
    private TextView mTextError;

    public static FragmentSearchProducts newInstance(String param1, String param2) {
        FragmentSearchProducts fragment = new FragmentSearchProducts();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View layout = inflater.inflate(R.layout.fragment_search_products, container, false);
        //L.t(getActivity(),"Inside stall Products!!!");
        searchView = (EditText) layout.findViewById(R.id.searchView);
        mProgressBar = (ProgressBar) layout.findViewById(R.id.progressBar);
        mTextError = (TextView) layout.findViewById(R.id.tError);
        /*EditText searchText = (EditText)
                searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        Typeface myCustomFont = Typeface.createFromAsset(getContext().getAssets(),"kalpurush.ttf");
        searchText.setTypeface(myCustomFont);*/
        option = 1;
        radioGroup = (RadioGroup) layout.findViewById(R.id.searchViewRadio);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                if (checkedId == R.id.byName) {
                    searchView.setHint("Search by Name");
                    option = 1;
                } else {
                    searchView.setHint("Search by Author/Company");
                    option = 2;
                }
            }
        });



        /*searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search = query;
                searchResult();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search = newText;
                return false;
            }
        });
*/

        mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipeProducts);
        mSwipeRefreshLayout.setOnRefreshListener(this);


        mRecyclerProducts = (RecyclerView) layout.findViewById(R.id.listProducts);
        //set the layout manager before trying to display data
        mRecyclerProducts.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new AdapterProducts(getActivity());
        mRecyclerProducts.setAdapter(mAdapter);

        if (savedInstanceState != null) {
            mListProducts = savedInstanceState.getParcelableArrayList(STATE_STALL_PRODUCTS);
        } else {
//            mListProducts = MyApplication.getWritableDatabaseProduct().readProducts();
//            if (mListProducts.isEmpty()) {
//                L.m("FragmentUpcoming: executing task from fragment");
            new TaskLoadProducts(this, ActivityFair.fair.getDb_name(), null, 0).execute();

            //}
        }
        mAdapter.setProducts(mListProducts);

        return layout;
    }

    @Override
    public void onSortByName() {
        mSorter.sortProductsByName(mListProducts);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSortByPrice() {
        mSorter.sortProductsByPrice(mListProducts);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSortByAvailability() {
        mSorter.sortProductsByAvailability(mListProducts);
        mAdapter.notifyDataSetChanged();
    }


    private void searchResult() {
        new TaskLoadProducts(this, ActivityFair.fair.getDb_name(), search, option).execute();
    }

    @Override
    public void onStart() {
        super.onStart();
        onRefresh();
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
    //Product Details dialog
    private void editDialogShow(final int position) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setTitle("Product Details");
        dialog.setContentView(R.layout.dialog_search_products_details);
        final TextView productName = (TextView) dialog.findViewById(R.id.dialog_stall_product_name);
        final TextView productCompany = (TextView) dialog.findViewById(R.id.dialog_stall_product_company);
        final TextView productDescription = (TextView) dialog.findViewById(R.id.dialog_stall_product_description);
        final TextView productPrice = (TextView) dialog.findViewById(R.id.dialog_stall_product_price);
        final TextView productAvailability = (TextView) dialog.findViewById(R.id.dialog_stall_product_availability);
        final ImageView productImage = (ImageView) dialog.findViewById(R.id.dialog_stall_product_image);

        //productImage.setImageBitmap(StringToBitMap(mListProducts.get(position).getImage()));
        try
        {
            Glide.with(MyApplication.getAppContext()).load(mListProducts.get(position).getImage()).into(productImage);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        productName.setText(mListProducts.get(position).getName());
        productCompany.setText(mListProducts.get(position).getCompany());
        productDescription.setText(mListProducts.get(position).getDescription());
        productPrice.setText(mListProducts.get(position).getPrice());
        productAvailability.setText(mListProducts.get(position).getAvailability());

        dialog.show();
        Button bcancel = (Button) dialog.findViewById(R.id.bcancel);
        Button bviewstallmap = (Button) dialog.findViewById(R.id.bviewStallMap);
        bviewfav = (Button) dialog.findViewById(R.id.bviewFav);

        try
        {
            if (MyApplication.getWritableDatabaseFavProduct().queryFavProducts(ActivityFair.fair.getDb_name() , String.valueOf(mListProducts.get(position).getId()))) {
                bviewfav.setText("Saved");
            } else {
                bviewfav.setText("Favourite");
            }
        }catch (Exception e)
        {
            System.out.println(e);
        }


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
                L.t(getActivity(), "Request Canceled");
                dialog.cancel();
            }
        });

        bviewstallmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pos = position;
                getLocation(position,1);
            }
        });

        bviewfav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(bviewfav.getText().toString());
                if (bviewfav.getText().toString().toLowerCase().equals("favourite")) {
                    getLocation(position,2);
                }
                else
                {
                    try {
                        if (MyApplication.getWritableDatabaseFavProduct().deleteIdentifier(ActivityFair.fair.getDb_name(), String.valueOf(mListProducts.get(position).getId()))) {
                            bviewfav.setText("Favourite");

                        } else {
                            bviewfav.setText("Saved");
                        }
                    }
                    catch (Exception e)
                    {
                        L.t(MyApplication.getAppContext(), "Failed to remove!Try again.");
                    }
                }
            }
        });
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
    /*
    private void loadImageFromStorage(String path)
    {

        try {
            File f=new File(path, "profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            ImageView img=(ImageView)findViewById(R.id.imgPicker);
            img.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }
*/

    private void getLocation(int position,int id) {


        class GetLocation extends AsyncTask<Void, Void, Boolean> {
            public AsyncResponse delegate = null;

            private int position;
            private int id;

            public GetLocation(int position,int id) {
                this.position = position;
                this.id = id;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }


            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                if(id==1) {
                    delegate.processFinish(result);
                }
                else if(id==2)
                {
                    try {
                        Glide.with(MyApplication.getAppContext()).load(mListProducts.get(position).getImage()).asBitmap().into(new SimpleTarget() {
                            @Override
                            public void onResourceReady(Object resource, GlideAnimation glideAnimation) {
                                FavProduct favProduct = new FavProduct();
                                favProduct.setTable(ActivityFair.fair.getDb_name());
                                favProduct.setProductid(String.valueOf(mListProducts.get(position).getId()));
                                favProduct.setFair(ActivityFair.fair.getTitle());
                                favProduct.setLocation(ActivityFair.fair.getLocation());
                                favProduct.setStart_date(ActivityFair.fair.getStart_date());
                                favProduct.setEnd_date(ActivityFair.fair.getEnd_date());
                                favProduct.setOpen_time(ActivityFair.fair.getOpen_time());
                                favProduct.setClose_time(ActivityFair.fair.getClose_time());
                                favProduct.setStall(stallname);
                                favProduct.setName(mListProducts.get(position).getName());
                                favProduct.setCompany(mListProducts.get(position).getCompany());
                                favProduct.setDescription(mListProducts.get(position).getDescription());
                                favProduct.setPrice(mListProducts.get(position).getPrice());
                                favProduct.setAvailability(mListProducts.get(position).getAvailability());
                                favProduct.setImage(saveToInternalStorage((Bitmap) resource));
                                favProduct.setStalllocation(location);
                                System.out.println(favProduct.getImage());
                                MyApplication.getWritableDatabaseFavProduct().insertFavProducts(favProduct, false);
                                L.t(MyApplication.getAppContext(), "Marked as favourite.");
                                bviewfav.setText("Saved");
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        L.t(MyApplication.getAppContext(), "Failed to mark!Try again.");
                    }
                }
            }

            @Override
            protected Boolean doInBackground(Void... params) {

                Integer result = 0;
                try {
                    String st = "Select * from " + ActivityFair.fair.getDb_name() + "_stalls where stall='" + mListProducts.get(position).getStall()+"'";
                    System.out.println("Statement is "+st);

                    URL loadProductUrl = new URL(ActivityMain.Server+"loadStalls.php");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) loadProductUrl.openConnection();
                    System.out.println("Connected");

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

                    JSONObject rs = jsonArray.getJSONObject(count);
                    location = rs.getString("location");
                    stallname = rs.getString("stall_name");
                    System.out.println(location + " " + stallname);
                    return true;

                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }

        GetLocation ui = new GetLocation(position,id);
        ui.delegate = this;
        ui.execute();
    }

    @Override
    public void processFinish(Boolean output) {
        if (output) {
            if (mListProducts.get(pos).getImage() != null) {
                Intent i = new Intent(getActivity(), ActivityProductMap.class);
                i.putExtra("Location", location);
                i.putExtra("Stallname", stallname);
                i.putExtra("Image", mListProducts.get(pos).getImage());
                startActivity(i);
            } else {
                Intent i = new Intent(getActivity(), ActivityStallMap.class);
                i.putExtra("Location", location);
                i.putExtra("Stallname", stallname);
                startActivity(i);
            }
        } else {
            L.t(getActivity(), "Connection Error");
        }

    }




    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //save the movie list to a parcelable prior to rotation or configuration change
        outState.putParcelableArrayList(STATE_STALL_PRODUCTS, mListProducts);
    }


    @Override
    public void onProductLoaded(ArrayList<Product> listProducts) {

       /* if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        mListProducts = listProducts;
        mAdapter.setProducts(listProducts);
*/
        mProgressBar.setVisibility(View.INVISIBLE);
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        if(listProducts==null)
        {
            mTextError.setText("Please check the connection.\nSwipe to refresh.");
            mTextError.setVisibility(View.VISIBLE);
            mListProducts=null;
            mAdapter.setProducts(mListProducts);
            return;
        }
        else if (listProducts.get(0).getId()==-1)
        {
            mTextError.setText("There is no product for this fair available.");
            mTextError.setVisibility(View.VISIBLE);
            mListProducts=null;
            mAdapter.setProducts(mListProducts);
            return;
        }
        mTextError.setVisibility(View.INVISIBLE);
        mListProducts=listProducts;
        mAdapter.setProducts(listProducts);

    }
    @Override
    public void onPause() {
        super.onPause();
        if(mProgressBar.getVisibility()==View.VISIBLE)
        {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button bsearchproduct = (Button) getActivity().findViewById(R.id.bsearchproduct);
        bsearchproduct.setOnClickListener(this);
    }

    @Override
    public void onRefresh() {
        //L.t(getActivity(), "Refreshing......");
        //load the whole feed again on refresh, dont try this at home :)
        new TaskLoadProducts(this, ActivityFair.fair.getDb_name(), null, 0).execute();

    }

    //Click listener for Add Button
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bsearchproduct) {
            InputMethodManager inputManager = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
            search = searchView.getText().toString();
            searchResult();
        }

    }


    //Touch
    public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
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
