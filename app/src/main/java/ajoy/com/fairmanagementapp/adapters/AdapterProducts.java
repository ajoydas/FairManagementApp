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

import java.util.ArrayList;

import ajoy.com.fairmanagementapp.anim.AnimationUtils;
import ajoy.com.fairmanagementapp.materialtest.R;
import ajoy.com.fairmanagementapp.pojo.Product;

/**
 * Created by ajoy on 5/19/16.
 */
public class AdapterProducts  extends RecyclerView.Adapter<AdapterProducts.ViewHolderProducts> {

    private ArrayList<Product> mListProducts = new ArrayList<>();
    private LayoutInflater mInflater;
    private int mPreviousPosition = 0;

    public AdapterProducts(Context context) {
        this.mInflater = LayoutInflater.from(context);;
    }

    public void setProducts(ArrayList<Product> listMovies) {
        this.mListProducts = listMovies;
        //update the adapter to reflect the new set of movies
        notifyDataSetChanged();
    }

    @Override
    public ViewHolderProducts onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.custom_product_list, parent, false);
        ViewHolderProducts viewHolder = new ViewHolderProducts(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolderProducts holder, int position) {
        Product currentProduct = mListProducts.get(position);

        holder.productTitle.setText(currentProduct.getTitle());
        holder.productPrice.setText(String.valueOf(currentProduct.getPrice()));
        holder.productThumbnail.setImageBitmap(StringToBitMap(currentProduct.getThumbnail()));

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

    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

    @Override
    public int getItemCount() {
        if(mListProducts==null)return 0;
        return mListProducts.size();
    }

    static class ViewHolderProducts extends RecyclerView.ViewHolder {

        ImageView productThumbnail;
        TextView productTitle;
        TextView productPrice;
        //RatingBar movieAudienceScore;

        public ViewHolderProducts(View itemView) {
            super(itemView);
            productThumbnail = (ImageView) itemView.findViewById(R.id.productThumbnail);
            productTitle = (TextView) itemView.findViewById(R.id.productTitle);
            productPrice = (TextView) itemView.findViewById(R.id.productPrice);
            //movieAudienceScore = (RatingBar) itemView.findViewById(R.id.movieAudienceScore);
        }
    }
}
