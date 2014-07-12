package com.tomdoesburg.kooktijden;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by Joost on 2-7-2014.
 */
// Instances of this class are fragments representing a single
// object in our collection.
public class FragmentKookplaat3pits extends Fragment {
    public static final String ARG_OBJECT = "object";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated properly.
        View rootView = inflater.inflate(R.layout.kookplaat3pits, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //kookplaat 1
        ProgressBar kookplaat1 = (ProgressBar) getView().findViewById(R.id.kookplaat1);
        TextView kookplaat1text = (TextView) getView().findViewById(R.id.kookplaat1text);
        TimerHelper timerHelper = new TimerHelper();
        timerHelper.init(kookplaat1, kookplaat1text);

        //kookplaat 2
        ProgressBar kookplaat2 = (ProgressBar) getView().findViewById(R.id.kookplaat2);
        TextView kookplaat2text = (TextView) getView().findViewById(R.id.kookplaat2text);
        TimerHelper timerHelper2 = new TimerHelper();
        timerHelper2.init(kookplaat2, kookplaat2text);

        //kookplaat 3
        ProgressBar kookplaat3 = (ProgressBar) getView().findViewById(R.id.kookplaat3);
        TextView kookplaat3text = (TextView) getView().findViewById(R.id.kookplaat3text);
        TimerHelper timerHelper3 = new TimerHelper();
        timerHelper3.init(kookplaat3, kookplaat3text);
    }
}