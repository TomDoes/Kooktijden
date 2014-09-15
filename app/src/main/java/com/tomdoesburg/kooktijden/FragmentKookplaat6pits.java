package com.tomdoesburg.kooktijden;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by Joost on 2-7-2014.
 */
// Instances of this class are fragments representing a single
// object in our collection.
public class FragmentKookplaat6pits extends Fragment {
    public static final String ARG_OBJECT = "object";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated properly.
        View rootView = inflater.inflate(R.layout.kookplaat6pits, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //kookplaat 1
        View kookplaat1view = getView().findViewById(R.id.kookplaat1);
        ProgressBar kookplaat1_progress = (ProgressBar) kookplaat1view.findViewById(R.id.kookplaat);
        TextView kookplaat1_text = (TextView) kookplaat1view.findViewById(R.id.kookplaatText);
        Button kookplaat1_plus = (Button) kookplaat1view.findViewById(R.id.buttonTimerPlus);
        Button kookplaat1_min = (Button) kookplaat1view.findViewById(R.id.buttonTimerMin);

        TimerHelper timerHelper1 = new TimerHelper();
        timerHelper1.init(kookplaat1_progress,kookplaat1_text,kookplaat1_plus,kookplaat1_min);


        //kookplaat 2
        View kookplaat2view = getView().findViewById(R.id.kookplaat2);
        ProgressBar kookplaat2_progress = (ProgressBar) kookplaat2view.findViewById(R.id.kookplaat);
        TextView kookplaat2_text = (TextView) kookplaat2view.findViewById(R.id.kookplaatText);
        Button kookplaat2_plus = (Button) kookplaat2view.findViewById(R.id.buttonTimerPlus);
        Button kookplaat2_min = (Button) kookplaat2view.findViewById(R.id.buttonTimerMin);

        TimerHelper timerHelper2 = new TimerHelper();
        timerHelper2.init(kookplaat2_progress,kookplaat2_text,kookplaat2_plus,kookplaat2_min);


        //kookplaat 3
        View kookplaat3view = getView().findViewById(R.id.kookplaat3);
        ProgressBar kookplaat3_progress = (ProgressBar) kookplaat3view.findViewById(R.id.kookplaat);
        TextView kookplaat3_text = (TextView) kookplaat3view.findViewById(R.id.kookplaatText);
        Button kookplaat3_plus = (Button) kookplaat3view.findViewById(R.id.buttonTimerPlus);
        Button kookplaat3_min = (Button) kookplaat3view.findViewById(R.id.buttonTimerMin);

        TimerHelper timerHelper3 = new TimerHelper();
        timerHelper3.init(kookplaat3_progress,kookplaat3_text,kookplaat3_plus,kookplaat3_min);


        //kookplaat 4
        View kookplaat4view = getView().findViewById(R.id.kookplaat4);
        ProgressBar kookplaat4_progress = (ProgressBar) kookplaat4view.findViewById(R.id.kookplaat);
        TextView kookplaat4_text = (TextView) kookplaat4view.findViewById(R.id.kookplaatText);
        Button kookplaat4_plus = (Button) kookplaat4view.findViewById(R.id.buttonTimerPlus);
        Button kookplaat4_min = (Button) kookplaat4view.findViewById(R.id.buttonTimerMin);

        TimerHelper timerHelper4 = new TimerHelper();
        timerHelper4.init(kookplaat4_progress,kookplaat4_text,kookplaat4_plus,kookplaat4_min);


        //kookplaat 5
        View kookplaat5view = getView().findViewById(R.id.kookplaat5);
        ProgressBar kookplaat5_progress = (ProgressBar) kookplaat5view.findViewById(R.id.kookplaat);
        TextView kookplaat5_text = (TextView) kookplaat5view.findViewById(R.id.kookplaatText);
        Button kookplaat5_plus = (Button) kookplaat5view.findViewById(R.id.buttonTimerPlus);
        Button kookplaat5_min = (Button) kookplaat5view.findViewById(R.id.buttonTimerMin);

        TimerHelper timerHelper5 = new TimerHelper();
        timerHelper5.init(kookplaat5_progress,kookplaat5_text,kookplaat5_plus,kookplaat5_min);


        //kookplaat 6
        View kookplaat6view = getView().findViewById(R.id.kookplaat6);
        ProgressBar kookplaat6_progress = (ProgressBar) kookplaat6view.findViewById(R.id.kookplaat);
        TextView kookplaat6_text = (TextView) kookplaat6view.findViewById(R.id.kookplaatText);
        Button kookplaat6_plus = (Button) kookplaat6view.findViewById(R.id.buttonTimerPlus);
        Button kookplaat6_min = (Button) kookplaat6view.findViewById(R.id.buttonTimerMin);

        TimerHelper timerHelper6 = new TimerHelper();
        timerHelper6.init(kookplaat6_progress,kookplaat6_text,kookplaat6_plus,kookplaat6_min);
    }
}