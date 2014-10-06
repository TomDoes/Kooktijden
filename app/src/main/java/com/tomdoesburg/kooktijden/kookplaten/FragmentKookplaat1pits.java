package com.tomdoesburg.kooktijden.kookplaten;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.tomdoesburg.kooktijden.KooktijdenApplication;
import com.tomdoesburg.kooktijden.R;
import com.tomdoesburg.model.Vegetable;

/**
 * Created by Joost on 2-7-2014.
 * edited by Frank on 9-8-2014
 */
// Instances of this class are fragments representing a single
// object in our collection.
public class FragmentKookplaat1pits extends Fragment {
    private final String TAG = "FragmentKookplaat1pits";
    public static final String ARG_OBJECT = "object";

    //Timer related variables
    int timeSeconds;
    boolean timerRunning;
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
        ProgressBar kookplaat1_progress = (ProgressBar) kookplaat1view.findViewById(R.id.kookplaat);
        TextView kookplaat1_text = (TextView) kookplaat1view.findViewById(R.id.kookplaatText);

        timerHelper1 = new TimerHelper();
        timerHelper1.init(getActivity(),kookplaat1_progress,kookplaat1_text,"kookPlaat1");

    }

    public void tick(){
        timerHelper1.onTick();
    }

    public void setVegetable(int kookPlaat, Vegetable veg){
        switch(kookPlaat){
            case 1:
                timerHelper1.setVegetable(veg);
                break;
        }
    }

}