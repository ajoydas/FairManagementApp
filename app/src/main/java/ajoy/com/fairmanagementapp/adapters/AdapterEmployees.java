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
import ajoy.com.fairmanagementapp.objects.Employee;

/**
 * Created by ajoy on 5/19/16.
 */
public class AdapterEmployees  extends RecyclerView.Adapter<AdapterEmployees.ViewHolderEmployees> {

    private ArrayList<Employee> mListEmployees = new ArrayList<>();
    private LayoutInflater mInflater;
    private int mPreviousPosition = 0;

    public AdapterEmployees(Context context) {
        this.mInflater = LayoutInflater.from(context);;
    }

    public void setEmployees(ArrayList<Employee> listMovies) {
        this.mListEmployees = listMovies;
        //update the adapter to reflect the new set of movies
        notifyDataSetChanged();
    }

    @Override
    public ViewHolderEmployees onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.custom_employee_list, parent, false);
        ViewHolderEmployees viewHolder = new ViewHolderEmployees(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolderEmployees holder, int position) {
        Employee currentEmployee = mListEmployees.get(position);

        holder.productTitle.setText(currentEmployee.getName());
        holder.productCompany.setText("Position: "+currentEmployee.getPosition());

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
        if(mListEmployees==null)return 0;
        return mListEmployees.size();
    }

    static class ViewHolderEmployees extends RecyclerView.ViewHolder {

        TextView productTitle;
        TextView productCompany;


        public ViewHolderEmployees(View itemView) {
            super(itemView);
            productTitle = (TextView) itemView.findViewById(R.id.productTitle);
            productCompany = (TextView) itemView.findViewById(R.id.productCompany);

        }
    }
}
