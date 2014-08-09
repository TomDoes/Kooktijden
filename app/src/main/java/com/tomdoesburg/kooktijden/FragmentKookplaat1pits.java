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

    //Buttons & view declaration
    ProgressBar kookplaat1;
    TextView kookplaat1text;
    ImageButton kookplaat1Button1;  //button for setup of known vegetable time in database
    ImageButton kookplaat1Button2; //button for setup of custom time

    //Timer related variables
    TimerHelper timerHelper;
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
        kookplaat1 = (ProgressBar) getView().findViewById(R.id.kookplaat1);
        kookplaat1text = (TextView) getView().findViewById(R.id.kookplaat1text);

        //timer buttons (invisible initially)
        kookplaat1Button1 = (ImageButton) getView().findViewById(R.id.kookplaat1Button1);
        kookplaat1Button2 = (ImageButton) getView().findViewById(R.id.kookplaat1Button2);

        TimerHelper timerHelper = new TimerHelper();
        //timerHelper.init(kookplaat1, kookplaat1text);
        timerHelper.init(kookplaat1,kookplaat1text,kookplaat1Button1,kookplaat1Button2); //uses new init function
    }


}