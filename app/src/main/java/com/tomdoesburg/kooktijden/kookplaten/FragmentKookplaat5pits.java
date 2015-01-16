package com.tomdoesburg.kooktijden.kookplaten;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.tomdoesburg.kooktijden.KooktijdenApplication;
import com.tomdoesburg.kooktijden.R;
import com.tomdoesburg.model.Vegetable;

/**
 * Created by Joost on 2-7-2014.
 */
// Instances of this class are fragments representing a single
// object in our collection.
public class FragmentKookplaat5pits extends FragmentKookplaat {
    public static final String ARG_OBJECT = "object";

    //timerHelper instances
    TimerHelper timerHelper1;
    TimerHelper timerHelper2;
    TimerHelper timerHelper3;
    TimerHelper timerHelper4;
    TimerHelper timerHelper5;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated properly.
        View rootView = inflater.inflate(R.layout.kookplaat5pits, container, false);
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
        timerHelper1 = new FragmentKookplaat.TimerHelper();
        // timerHelper1.init(getActivity(),kookplaat1view,"kookPlaat1");
        timerHelper1.init(kookplaat1view,"kookPlaat1");

        //kookplaat 2
        View kookplaat2view = getView().findViewById(R.id.kookplaat2);
        timerHelper2 = new FragmentKookplaat.TimerHelper();
        //timerHelper2.init(getActivity(),kookplaat2view,"kookPlaat2");
        timerHelper2.init(kookplaat2view,"kookPlaat2");


        //kookplaat 3
        View kookplaat3view = getView().findViewById(R.id.kookplaat3);
        timerHelper3 = new FragmentKookplaat.TimerHelper();
        timerHelper3.init(kookplaat3view,"kookPlaat3");


        //kookplaat 4
        View kookplaat4view = getView().findViewById(R.id.kookplaat4);
        timerHelper4 = new FragmentKookplaat.TimerHelper();
        timerHelper4.init(kookplaat4view,"kookPlaat4");


        //kookplaat 5
        View kookplaat5view = getView().findViewById(R.id.kookplaat5);
        timerHelper5 = new FragmentKookplaat.TimerHelper();
        timerHelper5.init(kookplaat5view,"kookPlaat5");
    }

    public void tick(){
        timerHelper1.onTick();
        timerHelper2.onTick();
        timerHelper3.onTick();
        timerHelper4.onTick();
        timerHelper5.onTick();
    }

    public void setVegetable(int kookPlaat, Vegetable veg){
        switch(kookPlaat){
            case 1:
                timerHelper1.setVegetable(veg);
                break;
            case 2:
                timerHelper2.setVegetable(veg);
                break;
            case 3:
                timerHelper3.setVegetable(veg);
                break;
            case 4:
                timerHelper4.setVegetable(veg);
                break;
            case 5:
                timerHelper5.setVegetable(veg);
                break;
        }
    }

}