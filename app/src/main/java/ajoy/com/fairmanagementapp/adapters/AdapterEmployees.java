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
import ajoy.com.fairmanagementapp.pojo.Employee;

/**
 * Created by ajoy on 5/31/16.
 */
public class AdapterEmployees extends RecyclerView.Adapter<AdapterEmployees.ViewHolderEmployees>{
    private ArrayList<Employee> mListEmployees = new ArrayList<>();
    private LayoutInflater mInflater;
    private int mPreviousPosition = 0;

    public AdapterEmployees(Context context) {
        this.mInflater = LayoutInflater.from(context);;
    }

    public void setEmployees(ArrayList<Employee> listEmployees) {
        System.out.println("Inside setEmployees");
        this.mListEmployees = listEmployees;
        System.out.println(mListEmployees);
        //update the adapter to reflect the new set of movies
        notifyDataSetChanged();
    }

    @Override
    public ViewHolderEmployees onCreateViewHolder(ViewGroup parent, int viewType) {
        System.out.println("Inside ViewHolderEmployees");
        View view = mInflater.inflate(R.layout.custom_employee_list, parent, false);
        ViewHolderEmployees viewHolder = new ViewHolderEmployees(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolderEmployees holder, int position) {

        System.out.println("Inside onBindViewHolder");
        Employee currentemployee = mListEmployees.get(position);

        holder.employeeName.setText(currentemployee.getName());
        holder.employeeContactNo.setText(currentemployee.getContact_no());
        holder.employeePosition.setText(currentemployee.getPosition());

        System.out.println("outside onBindViewHolder");
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
        if(mListEmployees==null)return 0;
        return mListEmployees.size();
    }

    static class ViewHolderEmployees extends RecyclerView.ViewHolder {

        TextView employeeName;
        TextView employeeContactNo;
        TextView employeePosition;

        public ViewHolderEmployees(View itemView) {
            super(itemView);
            employeeName = (TextView) itemView.findViewById(R.id.employeeName);
            employeeContactNo = (TextView) itemView.findViewById(R.id.employeeContactNo);
            employeePosition = (TextView) itemView.findViewById(R.id.employeePosition);
        }
    }
}
