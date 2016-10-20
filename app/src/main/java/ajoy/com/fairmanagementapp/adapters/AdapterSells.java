package ajoy.com.fairmanagementapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ajoy.com.fairmanagementapp.anim.AnimationUtils;
import ajoy.com.fairmanagementapp.application.R;
import ajoy.com.fairmanagementapp.objects.Sell;

/**
 * Created by ajoy on 6/2/16.
 */
public class AdapterSells extends RecyclerView.Adapter<AdapterSells.ViewHolderSells> {

    private ArrayList<Sell> mListEmployees = new ArrayList<>();
    private LayoutInflater mInflater;
    private int mPreviousPosition = 0;

    public AdapterSells(Context context) {
        this.mInflater = LayoutInflater.from(context);
        ;
    }

    public void setSells(ArrayList<Sell> listMovies) {
        this.mListEmployees = listMovies;
        //update the adapter to reflect the new set of movies
        notifyDataSetChanged();
    }

    @Override
    public ViewHolderSells onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.custom_sell_list, parent, false);
        ViewHolderSells viewHolder = new ViewHolderSells(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolderSells holder, int position) {
        Sell currentSell = mListEmployees.get(position);

        holder.sellProduct.setText(currentSell.getProduct_name());
        holder.sellEmployee.setText(currentSell.getEmployee_name());
        holder.sellPrice.setText(currentSell.getPrice());


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
        if (mListEmployees == null) return 0;
        return mListEmployees.size();
    }

    static class ViewHolderSells extends RecyclerView.ViewHolder {

        TextView sellProduct;
        TextView sellEmployee;
        TextView sellPrice;

        public ViewHolderSells(View itemView) {
            super(itemView);
            sellProduct = (TextView) itemView.findViewById(R.id.sellProduct);
            sellEmployee = (TextView) itemView.findViewById(R.id.sellEmployee);
            sellPrice = (TextView) itemView.findViewById(R.id.sellPrice);
        }
    }
}


