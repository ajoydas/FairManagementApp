package ajoy.com.fairmanagementapp.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import ajoy.com.fairmanagementapp.logging.L;
import ajoy.com.fairmanagementapp.objects.FavProduct;

public class FragmentFavourites  extends Fragment {

    private static final String STATE_FAV_PRODUCTS = "states_fav_products";
    private RecyclerView mRecyclerProducts;
    private AdapterFavProducts mAdapter;
    private ArrayList<FavProduct> mListFavProducts;
    private DateFormat mFormatter = new SimpleDateFormat("dd-MM-yyyy");
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

    private void editDialogShow(final int position) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setTitle("Product Details");
        dialog.setContentView(R.layout.dialog_fav_products_details);
        final TextView productName = (TextView) dialog.findViewById(R.id.dialog_stall_product_name);
        final TextView stallName = (TextView) dialog.findViewById(R.id.dialog_stall_name);
        final TextView productCompany = (TextView) dialog.findViewById(R.id.dialog_stall_product_company);
        final TextView productDescription = (TextView) dialog.findViewById(R.id.dialog_stall_product_description);
        final TextView productPrice = (TextView) dialog.findViewById(R.id.dialog_stall_product_price);
        final TextView productAvailability = (TextView) dialog.findViewById(R.id.dialog_stall_product_availability);
        final ImageView productImage = (ImageView) dialog.findViewById(R.id.dialog_stall_product_image);
        final TextView fairtitle=(TextView) dialog.findViewById(R.id.detailstitle);
        final TextView location=(TextView) dialog.findViewById(R.id.detailslocation);
        final TextView startdate=(TextView) dialog.findViewById(R.id.detailsstartdate);
        final TextView enddate=(TextView) dialog.findViewById(R.id.detailsenddate);
        final TextView opentime=(TextView) dialog.findViewById(R.id.detailsopentime);
        final TextView closetime=(TextView) dialog.findViewById(R.id.detailsclosetime);

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
                    if (MyApplication.getWritableDatabaseFavProduct().deleteIdentifier(mListFavProducts.get(position).getIdentifier()) ){
                        L.t(MyApplication.getAppContext(), "Removed!");
                        mListFavProducts = MyApplication.getWritableDatabaseFavProduct().readFavProducts();
                        mAdapter.setFavProducts(mListFavProducts);
                        dialog.dismiss();
                    } else {
                        bviewfav.setText("Saved");
                    }
                } catch (Exception e) {
                    L.t(MyApplication.getAppContext(), "Failed to remove!Try again.");
                }

            }
        });
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
