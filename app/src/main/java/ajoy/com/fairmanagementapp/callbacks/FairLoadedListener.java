package ajoy.com.fairmanagementapp.callbacks;

import java.util.ArrayList;

import ajoy.com.fairmanagementapp.objects.Fair;

/**
 * Created by ajoy on 5/22/16.
 */
public interface FairLoadedListener {
    public void  onFairLoaded(ArrayList<Fair> listFairs);
}
