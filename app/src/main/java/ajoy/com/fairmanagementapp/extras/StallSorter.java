package ajoy.com.fairmanagementapp.extras;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ajoy.com.fairmanagementapp.objects.Stall;

/**
 * Created by ajoy on 5/29/16.
 */
public class StallSorter {
    public void sortStallsByName(ArrayList<Stall> stalls){
        Collections.sort(stalls, new Comparator<Stall>() {
            @Override
            public int compare(Stall lhs, Stall rhs) {

                return (lhs.getStall_name().toLowerCase()).compareTo(rhs.getStall_name().toLowerCase());
            }
        });
    }
    public void sortStallsByOwner(ArrayList<Stall> stalls){

        Collections.sort(stalls, new Comparator<Stall>() {
            @Override
            public int compare(Stall lhs, Stall rhs) {
                return (lhs.getOwner().toLowerCase()).compareTo(rhs.getOwner().toLowerCase());
            }
        });
    }

    public  void sortStallsById(ArrayList<Stall> stalls)
    {
        Collections.sort(stalls, new Comparator<Stall>() {
            @Override
            public int compare(Stall lhs, Stall rhs) {
                if(lhs.getId()>rhs.getId())
                {
                    return 1;
                }
                else
                    return -1;
            }
        });
    }
}
