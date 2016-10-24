package ajoy.com.fairmanagementapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ajoy.com.fairmanagementapp.activities.ActivityFair;
import ajoy.com.fairmanagementapp.anim.AnimationUtils;
import ajoy.com.fairmanagementapp.application.MyApplication;
import ajoy.com.fairmanagementapp.application.R;
import ajoy.com.fairmanagementapp.objects.FavProduct;
import ajoy.com.fairmanagementapp.objects.Product;

/**
 * Created by hp on 24-10-2016.
 */

public class AdapterFavProducts extends RecyclerView.Adapter<AdapterFavProducts.ViewHolderFavProducts> {

    private ArrayList<FavProduct> mListFavProducts = new ArrayList<>();
    private LayoutInflater mInflater;
    private int mPreviousPosition = 0;


    public AdapterFavProducts(Context context) {
        this.mInflater = LayoutInflater.from(context);
        ;
    }

    public void setFavProducts(ArrayList<FavProduct> listMovies) {
        this.mListFavProducts = listMovies;
        //update the adapter to reflect the new set of movies
        notifyDataSetChanged();
    }

    @Override
    public AdapterFavProducts.ViewHolderFavProducts onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.custom_product_list, parent, false);
        AdapterFavProducts.ViewHolderFavProducts viewHolder = new AdapterFavProducts.ViewHolderFavProducts(view);
        return viewHolder;
    }



    @Override
    public void onBindViewHolder(AdapterFavProducts.ViewHolderFavProducts holder, int position) {
        FavProduct currentFavProduct = mListFavProducts.get(position);

        System.out.println("Inside View holder"+currentFavProduct);
        holder.FavProductTitle.setText(currentFavProduct.getName());
        holder.FavProductCompany.setText("Company/Author: " + currentFavProduct.getCompany());
        holder.FavProductPrice.setText("Price:Tk " + currentFavProduct.getPrice());
        holder.FavProductAvailability.setText("Available: " + currentFavProduct.getAvailability());

        Glide.with(MyApplication.getAppContext()).load(currentFavProduct.getImage()).into(holder.FavProductImage);

        //holder.productImage.setImageBitmap(StringToBitMap(currentProduct.getImage()));

        if (position > mPreviousPosition) {
            AnimationUtils.animateSunblind(holder, true);
//            AnimationUtils.animateSunblind(holder, true);
//            AnimationUtils.animate1(holder, true);
//            AnimationUtils.animate(holder,true);
        } else {
            AnimationUtils.animateSunblind(holder, false);
//            AnimationUtils.animateSunblind(holder, false);
//            AnimationUtils.animate1(holder, false);
//            AnimationUtils.animate(holder, false);
        }
        mPreviousPosition = position;
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
    public int getItemCount() {
        if (mListFavProducts == null) return 0;
        return mListFavProducts.size();
    }

    static class ViewHolderFavProducts extends RecyclerView.ViewHolder {

        ImageView FavProductImage;
        TextView FavProductTitle;
        TextView FavProductCompany;
        TextView FavProductPrice;
        TextView FavProductAvailability;


        public ViewHolderFavProducts(View itemView) {
            super(itemView);
            FavProductImage = (ImageView) itemView.findViewById(R.id.productImage);
            FavProductTitle = (TextView) itemView.findViewById(R.id.productTitle);
            FavProductCompany = (TextView) itemView.findViewById(R.id.productCompany);
            FavProductPrice = (TextView) itemView.findViewById(R.id.productPrice);
            FavProductAvailability = (TextView) itemView.findViewById(R.id.productAvailability);

        }
    }
}
