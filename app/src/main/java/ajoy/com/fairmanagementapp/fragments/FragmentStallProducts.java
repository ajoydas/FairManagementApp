package ajoy.com.fairmanagementapp.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.SearchView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import ajoy.com.fairmanagementapp.activities.ActivityAddProducts;
import ajoy.com.fairmanagementapp.activities.ActivityEditProducts;
import ajoy.com.fairmanagementapp.activities.ActivityFair;
import ajoy.com.fairmanagementapp.activities.ActivityProductMap;
import ajoy.com.fairmanagementapp.activities.ActivitySeller;
import ajoy.com.fairmanagementapp.activities.ActivityStallMap;
import ajoy.com.fairmanagementapp.adapters.AdapterProducts;
import ajoy.com.fairmanagementapp.callbacks.ProductLoadedListener;
import ajoy.com.fairmanagementapp.extras.ProductSorter;
import ajoy.com.fairmanagementapp.logging.L;
import ajoy.com.fairmanagementapp.application.MyApplication;
import ajoy.com.fairmanagementapp.application.R;
import ajoy.com.fairmanagementapp.objects.Product;
import ajoy.com.fairmanagementapp.task.TaskLoadStallProducts;

/**
 * Created by ajoy on 5/27/16.
 */
public class FragmentStallProducts extends Fragment implements View.OnClickListener,ProductLoadedListener, SwipeRefreshLayout.OnRefreshListener{
    private static EditText searchView;
    private static RadioGroup radioGroup;

    private static final String STATE_STALL_PRODUCTS = "states_stall_products";
    protected ArrayList<Product> mListProducts;
    private AdapterProducts mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private RecyclerView mRecyclerProducts;
    private ProductSorter mSorter = new ProductSorter();

    private String search;
    private int option;

    private ProgressBar mProgressBar;
    private TextView mTextError;

    public static FragmentStallProducts newInstance(String param1, String param2) {
        FragmentStallProducts fragment = new FragmentStallProducts();
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
        final View layout = inflater.inflate(R.layout.fragment_stall_products, container, false);
        //L.t(getActivity(),"Inside stall Products!!!");
        searchView= (EditText) layout.findViewById(R.id.searchView);
        searchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        mProgressBar = (ProgressBar) layout.findViewById(R.id.progressBar);
        mTextError = (TextView) layout.findViewById(R.id.tError);
        option=1;
        radioGroup= (RadioGroup) layout.findViewById(R.id.searchViewRadio);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                if (checkedId==R.id.byName){
                    searchView.setHint("Search by Name");
                    option=1;
                }
                else {
                    searchView.setHint("Search by Author/Company");
                    option=2;
                }
            }
        });

       /* searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search = query;
                searchResult(search);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search=newText;
                return false;
            }
        });*/


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
            new TaskLoadStallProducts(this,ActivityFair.fair.getDb_name(), ActivitySeller.stall.getStall(),null,0).execute();
        }
        mAdapter.setProducts(mListProducts);

        return layout;
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    private void searchResult(String s)
    {
        new TaskLoadStallProducts(this,ActivityFair.fair.getDb_name(), ActivitySeller.stall.getStall(),search,option).execute();
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
    boolean isImageFitToScreen=false;

    //Product Details dialog
    private void editDialogShow(final int position) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setTitle("Product Details");
        dialog.setContentView(R.layout.dialog_stall_product_details);
        final TextView productName = (TextView) dialog.findViewById(R.id.dialog_stall_product_name);
        final TextView productCompany = (TextView) dialog.findViewById(R.id.dialog_stall_product_company);
        final TextView productDescription = (TextView) dialog.findViewById(R.id.dialog_stall_product_description);
        final TextView productPrice = (TextView) dialog.findViewById(R.id.dialog_stall_product_price);
        final TextView productAvailability = (TextView) dialog.findViewById(R.id.dialog_stall_product_availability);
        final ImageView productImage = (ImageView) dialog.findViewById(R.id.dialog_stall_product_image);

        //productImage.setImageBitmap(StringToBitMap(mListProducts.get(position).getImage()));
        Glide.with(MyApplication.getAppContext()).load(mListProducts.get(position).getImage()).into(productImage);
        productName.setText(mListProducts.get(position).getName());
        productCompany.setText(mListProducts.get(position).getCompany());
        productDescription.setText(mListProducts.get(position).getDescription());
        productPrice.setText(mListProducts.get(position).getPrice());
        productAvailability.setText(mListProducts.get(position).getAvailability());

        dialog.show();
        Button bedit= (Button) dialog.findViewById(R.id.bedit);
        Button bcancel= (Button) dialog.findViewById(R.id.bcancel);
        Button bviewstallmap= (Button) dialog.findViewById(R.id.bviewStallMap);
        final android.widget.LinearLayout.LayoutParams params= (LinearLayout.LayoutParams) productImage.getLayoutParams();

        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isImageFitToScreen) {
                    isImageFitToScreen=false;
                    productImage.setLayoutParams(params);
                    productImage.setAdjustViewBounds(true);
                }else{
                    isImageFitToScreen=true;
                    productImage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    productImage.setScaleType(ImageView.ScaleType.FIT_XY);
                }
            }
        });



        bedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), ActivityEditProducts.class);
                i.putExtra("Information",mListProducts.get(position));
                startActivity(i);
                dialog.cancel();
            }
        });

        bcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                L.t(getActivity(),"Request Canceled");
                dialog.cancel();
            }
        });

        bviewstallmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mListProducts.get(position).getImage()!=null) {
                    Intent i = new Intent(getActivity(), ActivityProductMap.class);
                    i.putExtra("Location", ActivitySeller.stall.getLocation());
                    i.putExtra("Stallname",  ActivitySeller.stall.getStall_name());
                    i.putExtra("Image", mListProducts.get(position).getImage());
                    startActivity(i);
                }
                else
                {
                    Intent i = new Intent(getActivity(), ActivityStallMap.class);
                    i.putExtra("Location", ActivitySeller.stall.getLocation());
                    i.putExtra("Stallname",  ActivitySeller.stall.getStall_name());
                    startActivity(i);
                }
            }
        });
    }

    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
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

        /*if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        mListProducts = listProducts;
        mAdapter.setProducts(listProducts);*/

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
        Button baddproducts = (Button) getActivity().findViewById(R.id.baddproducts);
        baddproducts.setOnClickListener(this);
        Button bsearchproduct = (Button) getActivity().findViewById(R.id.bsearchproduct);
        bsearchproduct.setOnClickListener(this);
    }

    @Override
    public void onRefresh() {
        //L.t(getActivity(), "Refreshing.....");
        //load the whole feed again on refresh, dont try this at home :)
        new TaskLoadStallProducts(this,ActivityFair.fair.getDb_name(), ActivitySeller.stall.getStall(),null,0).execute();

    }

    //Click listener for Add Button
    @Override
    public void onClick(View v) {
        //L.t(getActivity(),"Add button Clicked");
        if(v.getId()==R.id.baddproducts)
        {
            Intent i = new Intent(MyApplication.getAppContext(), ActivityAddProducts.class);
            i.putExtra("db_name",ActivityFair.fair.getDb_name());
            i.putExtra("stall", ActivitySeller.stall.getStall());
            startActivity(i);
        }

        if(v.getId()==R.id.bsearchproduct)
        {
            InputMethodManager inputManager = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
            search = searchView.getText().toString();
            searchResult(search);
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
