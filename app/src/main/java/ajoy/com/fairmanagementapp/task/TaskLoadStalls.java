package ajoy.com.fairmanagementapp.task;

import android.os.AsyncTask;

import java.util.ArrayList;

import ajoy.com.fairmanagementapp.callbacks.StallLoadedListener;
import ajoy.com.fairmanagementapp.extras.FairUtils;
import ajoy.com.fairmanagementapp.objects.Stall;

/**
 * Created by ajoy on 5/29/16.
 */
public class TaskLoadStalls extends AsyncTask<Void,Void,ArrayList<Stall>> {
    private StallLoadedListener myComponent;
    String fair_db;
    String query;

    public TaskLoadStalls(StallLoadedListener myComponent, String fair_db, String query) {
        this.myComponent = myComponent;
        this.fair_db = fair_db;
        this.query = query;
    }

    @Override
    protected ArrayList<Stall> doInBackground(Void... params) {

        ArrayList<Stall> listStalls = FairUtils.loadSearchStall(fair_db,query);
        return listStalls;
    }

    @Override
    protected void onPostExecute(ArrayList<Stall> listStalls) {
        if (myComponent != null) {
            myComponent.onStallLoaded(listStalls);
        }
    }
}
