package com.tomdoesburg.kooktijden;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by Joost on 2-7-2014.
 * edited by Frank on 9-8-2014
 */
// Instances of this class are fragments representing a single
// object in our collection.
public class FragmentKookplaat1pits extends Fragment {
    public static final String ARG_OBJECT = "object";

    //Timer related variables
    int timeSeconds;
    boolean timerRunning;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated properly.
        View rootView = inflater.inflate(R.layout.kookplaat1pits, container, false);
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
    }


}