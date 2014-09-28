package com.tomdoesburg.kooktijden.kookplaten;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

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
public class FragmentKookplaat6pits extends Fragment {
    public static final String ARG_OBJECT = "object";

    //timerHelper instances
    TimerHelper timerHelper1;
    TimerHelper timerHelper2;
    TimerHelper timerHelper3;
    TimerHelper timerHelper4;
    TimerHelper timerHelper5;
    TimerHelper timerHelper6;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated properly.
        View rootView = inflater.inflate(R.layout.kookplaat6pits, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // analytics
        Tracker t = ((KooktijdenApplication) getActivity().getApplication()).getTracker(KooktijdenApplication.TrackerName.APP_TRACKER);
        t.setScreenName("Kookplaten");
        t.send(new HitBuilders.AppViewBuilder().setCustomDimension(2, "6 pit").build());

        //kookplaat 1
        View kookplaat1view = getView().findViewById(R.id.kookplaat1);
        ProgressBar kookplaat1_progress = (ProgressBar) kookplaat1view.findViewById(R.id.kookplaat);
        TextView kookplaat1_text = (TextView) kookplaat1view.findViewById(R.id.kookplaatText);
        Button kookplaat_plus = (Button) view.findViewById(R.id.buttonTimerPlus);

        timerHelper1 = new TimerHelper();
        timerHelper1.init(getActivity(),kookplaat1_progress,kookplaat1_text,kookplaat_plus,"kookPlaat1");


        //kookplaat 2
        View kookplaat2view = getView().findViewById(R.id.kookplaat2);
        ProgressBar kookplaat2_progress = (ProgressBar) kookplaat2view.findViewById(R.id.kookplaat);
        TextView kookplaat2_text = (TextView) kookplaat2view.findViewById(R.id.kookplaatText);

        timerHelper2 = new TimerHelper();
        timerHelper2.init(getActivity(),kookplaat2_progress,kookplaat2_text,kookplaat_plus,"kookPlaat2");


        //kookplaat 3
        View kookplaat3view = getView().findViewById(R.id.kookplaat3);
        ProgressBar kookplaat3_progress = (ProgressBar) kookplaat3view.findViewById(R.id.kookplaat);
        TextView kookplaat3_text = (TextView) kookplaat3view.findViewById(R.id.kookplaatText);

        timerHelper3 = new TimerHelper();
        timerHelper3.init(getActivity(),kookplaat3_progress,kookplaat3_text,kookplaat_plus,"kookPlaat3");


        //kookplaat 4
        View kookplaat4view = getView().findViewById(R.id.kookplaat4);
        ProgressBar kookplaat4_progress = (ProgressBar) kookplaat4view.findViewById(R.id.kookplaat);
        TextView kookplaat4_text = (TextView) kookplaat4view.findViewById(R.id.kookplaatText);

        timerHelper4 = new TimerHelper();
        timerHelper4.init(getActivity(),kookplaat4_progress,kookplaat4_text,kookplaat_plus,"kookPlaat4");


        //kookplaat 5
        View kookplaat5view = getView().findViewById(R.id.kookplaat5);
        ProgressBar kookplaat5_progress = (ProgressBar) kookplaat5view.findViewById(R.id.kookplaat);
        TextView kookplaat5_text = (TextView) kookplaat5view.findViewById(R.id.kookplaatText);

        timerHelper5 = new TimerHelper();
        timerHelper5.init(getActivity(),kookplaat5_progress,kookplaat5_text,kookplaat_plus,"kookPlaat5");


        //kookplaat 6
        View kookplaat6view = getView().findViewById(R.id.kookplaat6);
        ProgressBar kookplaat6_progress = (ProgressBar) kookplaat6view.findViewById(R.id.kookplaat);
        TextView kookplaat6_text = (TextView) kookplaat6view.findViewById(R.id.kookplaatText);

        timerHelper6 = new TimerHelper();
        timerHelper6.init(getActivity(),kookplaat6_progress,kookplaat6_text,kookplaat_plus,"kookPlaat6");
    }

    public void tick(){
        timerHelper1.onTick();
        timerHelper2.onTick();
        timerHelper3.onTick();
        timerHelper4.onTick();
        timerHelper5.onTick();
        timerHelper6.onTick();
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
            case 6:
                timerHelper6.setVegetable(veg);
                break;
        }
    }

}