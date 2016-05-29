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
import ajoy.com.fairmanagementapp.materialtest.R;
import ajoy.com.fairmanagementapp.pojo.Stall;

/**
 * Created by ajoy on 5/29/16.
 */
public class AdapterStalls extends RecyclerView.Adapter<AdapterStalls.ViewHolderStalls>{
    private ArrayList<Stall> mListStalls = new ArrayList<>();
    private LayoutInflater mInflater;
    private int mPreviousPosition = 0;
    private DateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd");

    public AdapterStalls(Context context) {
        this.mInflater = LayoutInflater.from(context);;
    }

    public void setStalls(ArrayList<Stall> listStalls) {
        this.mListStalls = listStalls;
        //update the adapter to reflect the new set of movies
        notifyDataSetChanged();
    }

    @Override
    public ViewHolderStalls onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.custom_stall_list, parent, false);
        ViewHolderStalls viewHolder = new ViewHolderStalls(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolderStalls holder, int position) {
        Stall currentstall = mListStalls.get(position);

        holder.stallName.setText(currentstall.getStall_name());
        holder.stallOwner.setText(currentstall.getOwner());

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

    @Override
    public int getItemCount() {
        if(mListStalls==null)return 0;
        return mListStalls.size();
    }

    static class ViewHolderStalls extends RecyclerView.ViewHolder {

        TextView stallName;
        TextView stallOwner;
        public ViewHolderStalls(View itemView) {
            super(itemView);
            stallName = (TextView) itemView.findViewById(R.id.stallTitle);
            stallOwner = (TextView) itemView.findViewById(R.id.stallowner);

        }
    }
}
