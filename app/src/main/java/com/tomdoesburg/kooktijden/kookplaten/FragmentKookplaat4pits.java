package com.tomdoesburg.kooktijden.kookplaten;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tomdoesburg.kooktijden.R;

/**
 * Created by Joost on 2-7-2014.
 */
// Instances of this class are fragments representing a single
// object in our collection.
public class FragmentKookplaat4pits extends Fragment {
    public static final String ARG_OBJECT = "object";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated properly.
        View rootView = inflater.inflate(R.layout.kookplaat4pits, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //kookplaat 1
        View kookplaat1view = getView().findViewById(R.id.kookplaat1);
        ProgressBar kookplaat1_progress = (ProgressBar) kookplaat1view.findViewById(R.id.kookplaat);
        TextView kookplaat1_text = (TextView) kookplaat1view.findViewById(R.id.kookplaatText);
        Button kookplaat_plus = (Button) view.findViewById(R.id.buttonTimerPlus);

        TimerHelper timerHelper1 = new TimerHelper();
        timerHelper1.init(getActivity(),kookplaat1_progress,kookplaat1_text,kookplaat_plus,"kookPlaat1");


        //kookplaat 2
        View kookplaat2view = getView().findViewById(R.id.kookplaat2);
        ProgressBar kookplaat2_progress = (ProgressBar) kookplaat2view.findViewById(R.id.kookplaat);
        TextView kookplaat2_text = (TextView) kookplaat2view.findViewById(R.id.kookplaatText);

        TimerHelper timerHelper2 = new TimerHelper();
        timerHelper2.init(getActivity(),kookplaat2_progress,kookplaat2_text,kookplaat_plus,"kookPlaat2");


        //kookplaat 3
        View kookplaat3view = getView().findViewById(R.id.kookplaat3);
        ProgressBar kookplaat3_progress = (ProgressBar) kookplaat3view.findViewById(R.id.kookplaat);
        TextView kookplaat3_text = (TextView) kookplaat3view.findViewById(R.id.kookplaatText);

        TimerHelper timerHelper3 = new TimerHelper();
        timerHelper3.init(getActivity(),kookplaat3_progress,kookplaat3_text,kookplaat_plus,"kookPlaat3");


        //kookplaat 4
        View kookplaat4view = getView().findViewById(R.id.kookplaat4);
        ProgressBar kookplaat4_progress = (ProgressBar) kookplaat4view.findViewById(R.id.kookplaat);
        TextView kookplaat4_text = (TextView) kookplaat4view.findViewById(R.id.kookplaatText);

        TimerHelper timerHelper4 = new TimerHelper();
        timerHelper4.init(getActivity(),kookplaat4_progress,kookplaat4_text,kookplaat_plus,"kookPlaat4");

    }
}