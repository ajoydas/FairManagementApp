package ajoy.com.fairmanagementapp.task;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ajoy.com.fairmanagementapp.callbacks.FairLoadedListener;
import ajoy.com.fairmanagementapp.callbacks.ProductLoadedListener;
import ajoy.com.fairmanagementapp.extras.MovieUtils;
import ajoy.com.fairmanagementapp.materialtest.MyApplication;
import ajoy.com.fairmanagementapp.pojo.Fair;
import ajoy.com.fairmanagementapp.pojo.Product;

/**
 * Created by ajoy on 5/22/16.
 */
public class TaskLoadFairs extends AsyncTask<Void,Void,ArrayList<Fair>>  {
    private FairLoadedListener myComponent;
    private int tab;

    public TaskLoadFairs(FairLoadedListener myComponent,int tab) {

        this.myComponent = myComponent;
        this.tab=tab;
    }


    @Override
    protected ArrayList<Fair> doInBackground(Void... params) {

        ArrayList<Fair> listFairs=  MovieUtils.loadFairs(tab);
/*
        Calendar c = Calendar.getInstance();
        Date date=c.getTime();
        System.out.println(date);

        for(int i=0;i<mallFairList.size();i++)
        {
            System.out.println("Outer ->"+mallFairList.get(i));
            System.out.println("Compare :"+(mallFairList.get(i).getStart_date()).compareTo(date));
            if(tab==1)
            {
                if((mallFairList.get(i).getStart_date()).compareTo(date)==-1)
                {
                    listFairs.add(mallFairList.get(i));
                    System.out.println("Inner ->"+mallFairList.get(i));
                }
            }
            else if(tab==2)
            {
                if((mallFairList.get(i).getStart_date()).compareTo(date)!=-1)
                {
                    listFairs.add(mallFairList.get(i));
                    System.out.println("Inner ->"+mallFairList.get(i));
                }
            }
        }
*/


        System.out.println("doing in background");
        return listFairs;
    }

    @Override
    protected void onPostExecute(ArrayList<Fair> listFairs) {
        if (myComponent != null) {
            myComponent.onFairLoaded(listFairs);
        }
    }

}
