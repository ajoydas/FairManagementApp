package ajoy.com.fairmanagementapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import ajoy.com.fairmanagementapp.activities.ActivityFair;
import ajoy.com.fairmanagementapp.activities.ActivitySeller;
import ajoy.com.fairmanagementapp.activities.ActivityStallMap;
import ajoy.com.fairmanagementapp.activities.ActivityStallView;
import ajoy.com.fairmanagementapp.logging.L;
import ajoy.com.fairmanagementapp.materialtest.R;
import ajoy.com.fairmanagementapp.task.TaskLoadStallProducts;

/**
 * Created by ajoy on 5/29/16.
 */
public class FragmentStallViewDetails extends Fragment   implements  View.OnClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    //private DateFormat mFormatter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public View layout;


    public FragmentStallViewDetails() {
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
    public static FragmentStallViewDetails newInstance(String param1, String param2) {
        FragmentStallViewDetails fragment = new FragmentStallViewDetails();
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
        layout= inflater.inflate(R.layout.fragment_stall_view_details, container, false);
        L.t(getActivity(),"Inside Fair details");
        TextView title=(TextView) layout.findViewById(R.id.detailsstallname);
        title.setText(ActivityStallView.stall.getStall_name());
        TextView organizer=(TextView) layout.findViewById(R.id.detailsstallowner);
        organizer.setText(ActivityStallView.stall.getOwner());
        TextView location=(TextView) layout.findViewById(R.id.detailsstalldescription);
        location.setText(ActivityStallView.stall.getDescription());
        //Button bedit = (Button) layout.findViewById(R.id.bedit);
        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button bstallmap = (Button) getActivity().findViewById(R.id.bstallmap);
        bstallmap.setOnClickListener(this);
    }

    //Click listener for Add Button
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.bstallmap)
        {
            Intent i = new Intent(getActivity(), ActivityStallMap.class);
            i.putExtra("Location",ActivityStallView.stall.getLocation());
            i.putExtra("Stallname",ActivityStallView.stall.getStall_name());
            startActivity(i);
        }

    }


}
