package ajoy.com.fairmanagementapp.callbacks;

import java.util.ArrayList;

import ajoy.com.fairmanagementapp.pojo.Stall;

/**
 * Created by ajoy on 5/29/16.
 */
public interface StallLoadedListener {
    public void onStallLoaded(ArrayList<Stall> listStalls);
}
