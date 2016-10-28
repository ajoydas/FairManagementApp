package ajoy.com.fairmanagementapp.task;

import android.os.AsyncTask;

import java.util.ArrayList;

import ajoy.com.fairmanagementapp.callbacks.SellLoadedListener;
import ajoy.com.fairmanagementapp.extras.FairUtils;
import ajoy.com.fairmanagementapp.objects.Sell;

/**
 * Created by ajoy on 6/2/16.
 */
public class TaskLoadSells extends AsyncTask<Void, Void, ArrayList<Sell>> {
    private SellLoadedListener myComponent;
    String fair_db;
    String stallname;
    String query;

    public TaskLoadSells(SellLoadedListener myComponent, String fair_db, String stallname, String query) {
        this.myComponent = myComponent;
        this.fair_db = fair_db;
        this.stallname = stallname;
        this.query = query;
    }

    @Override
    protected ArrayList<Sell> doInBackground(Void... params) {

        ArrayList<Sell> listSells = FairUtils.loadSells(fair_db, stallname, query);
        return listSells;
    }

    @Override
    protected void onPostExecute(ArrayList<Sell> listSells) {
        //if (myComponent != null) {
            myComponent.onSellLoaded(listSells);
        //}
    }
}

