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
public class FragmentKookplaat2pits extends Fragment {
    public static final String ARG_OBJECT = "object";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated properly.
        View rootView = inflater.inflate(R.layout.kookplaat2pits, container, false);
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

    }
}