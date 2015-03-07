package com.tomdoesburg.kooktijden.kookplaten;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.tomdoesburg.kooktijden.KooktijdenApplication;
import com.tomdoesburg.kooktijden.MainActivity;
import com.tomdoesburg.kooktijden.R;
import com.tomdoesburg.kooktijden.TimerHelper;
import com.tomdoesburg.model.Vegetable;

/**
 * Created by Joost on 2-7-2014.
 * edited by Frank on 9-8-2014
 */
// Instances of this class are fragments representing a single
// object in our collection.
public class FragmentKookplaat1pits extends android.support.v4.app.Fragment  {
    private final String TAG = "FragmentKookplaat1pits";

    //Timer related variables
    TimerHelper timerHelper1;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated properly.
        View rootView = inflater.inflate(R.layout.kookplaat1pits, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // analytics
        Tracker t = ((KooktijdenApplication) getActivity().getApplication()).getTracker(KooktijdenApplication.TrackerName.APP_TRACKER);
        t.setScreenName("Kookplaten");
        t.send(new HitBuilders.AppViewBuilder().setCustomDimension(2, "1 pit").build());

        //kookplaat 1
        View kookplaat1view = getView().findViewById(R.id.kookplaat1);

        timerHelper1 = new TimerHelper();
        timerHelper1.init(getActivity(),kookplaat1view,"kookPlaat1");
    }

    public void tick(){
        timerHelper1.onTick();
    }

    public void reset(){

        timerHelper1.reset();
    }

    public void setVegetable(int kookPlaat, Vegetable veg){
        Log.v(TAG, "setVegetable Fragmentkookplaat, ID = " + kookPlaat + ", veg = " + veg.getNameNL());

        switch(kookPlaat){
            case 1:
                timerHelper1.setVegetable(veg);
                break;
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        timerHelper1.resumeExistingTimer();
    }

}