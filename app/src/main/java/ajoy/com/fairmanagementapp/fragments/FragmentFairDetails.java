package ajoy.com.fairmanagementapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.test.suitebuilder.TestMethod;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.Inflater;

import ajoy.com.fairmanagementapp.activities.ActivityFair;
import ajoy.com.fairmanagementapp.extras.Constants;
import ajoy.com.fairmanagementapp.logging.L;
import ajoy.com.fairmanagementapp.materialtest.R;

/**
 * Created by ajoy on 5/23/16.
 */
public class FragmentFairDetails extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    //private DateFormat mFormatter;
    private DateFormat mFormatter = new SimpleDateFormat("dd-MM-yyyy");

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public FragmentFairDetails() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentSearch.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentFairDetails newInstance(String param1, String param2) {
        FragmentFairDetails fragment = new FragmentFairDetails();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout= inflater.inflate(R.layout.fragment_fair_details, container, false);
        L.t(getActivity(),"Inside Fair details");



        TextView title=(TextView) layout.findViewById(R.id.detailstitle);
        title.setText((ActivityFair.fair.getTitle()));
        TextView organizer=(TextView) layout.findViewById(R.id.detailsorganizer);
        organizer.setText((ActivityFair.fair.getOrganizer()));
        TextView location=(TextView) layout.findViewById(R.id.detailslocation);
        location.setText((ActivityFair.fair.getLocation()));

        Date date = ActivityFair.fair.getStart_date();
        TextView startdate=(TextView) layout.findViewById(R.id.detailsstartdate);
        startdate.setText(mFormatter.format(date));

        date = ActivityFair.fair.getEnd_date();
        TextView enddate=(TextView) layout.findViewById(R.id.detailsenddate);
        enddate.setText(mFormatter.format(date));

        SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
        Time time=ActivityFair.fair.getOpen_time();

        TextView opentime=(TextView) layout.findViewById(R.id.detailsopentime);
        opentime.setText(_12HourSDF.format(time));

        time=ActivityFair.fair.getClose_time();

        TextView closetime=(TextView) layout.findViewById(R.id.detailsclosetime);
        closetime.setText(_12HourSDF.format(time));



        return layout;
    }
}
