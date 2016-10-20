package ajoy.com.fairmanagementapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ajoy.com.fairmanagementapp.anim.AnimationUtils;
import ajoy.com.fairmanagementapp.extras.Constants;
import ajoy.com.fairmanagementapp.application.R;
import ajoy.com.fairmanagementapp.objects.Fair;

/**
 * Created by ajoy on 5/22/16.
 */
public class AdapterFairs extends RecyclerView.Adapter<AdapterFairs.ViewHolderFairs>{
    private ArrayList<Fair> mListFairs = new ArrayList<>();
    private LayoutInflater mInflater;
    private int mPreviousPosition = 0;
    private DateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd");

    public AdapterFairs(Context context) {
        this.mInflater = LayoutInflater.from(context);;
    }

    public void setFairs(ArrayList<Fair> listFairs) {
        this.mListFairs = listFairs;
        //update the adapter to reflect the new set of movies
        notifyDataSetChanged();
    }

    @Override
    public ViewHolderFairs onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.custom_fair_list, parent, false);
        ViewHolderFairs viewHolder = new ViewHolderFairs(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolderFairs holder, int position) {
        Fair currentfair = mListFairs.get(position);

        holder.fairTitle.setText(currentfair.getTitle());
        holder.fairLocation.setText(currentfair.getLocation());

        Date date = currentfair.getStart_date();
        if (date != null) {
            String formattedDate = mFormatter.format(date);
            holder.fairStartDate.setText(formattedDate);
        } else {
            holder.fairStartDate.setText(Constants.NA);
        }

        date = currentfair.getEnd_date();
        if (date != null) {
            String formattedDate = mFormatter.format(date);
            holder.fairEndDate.setText(formattedDate);
        } else {
            holder.fairEndDate.setText(Constants.NA);
        }

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

    /*public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }*/

    @Override
    public int getItemCount() {
        if(mListFairs==null)return 0;
        return mListFairs.size();
    }

    static class ViewHolderFairs extends RecyclerView.ViewHolder {

        TextView fairTitle;
        TextView fairLocation;
        TextView fairStartDate;
        TextView fairEndDate;

        public ViewHolderFairs(View itemView) {
            super(itemView);
            fairTitle = (TextView) itemView.findViewById(R.id.fairTitle);
            fairLocation = (TextView) itemView.findViewById(R.id.fairLocation);
            fairStartDate = (TextView) itemView.findViewById(R.id.fairStartDate);
            fairEndDate = (TextView) itemView.findViewById(R.id.fairEndDate);
        }
    }
}
