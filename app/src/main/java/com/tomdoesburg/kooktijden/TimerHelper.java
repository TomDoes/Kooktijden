package com.tomdoesburg.kooktijden;

import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by Joost on 11-7-2014.
 */
public class TimerHelper {

    boolean timerRunning;
    private boolean timeSet = false; //indicates a time has been chosen by user

    void init(final ProgressBar kookplaat, final TextView kookplaattext) {

        //TODO
        //create a selection thingy to select which vegetable to put on this timer
        //int vegetableID = vegetablePicker();
        //MySQLiteHelper db = new MySQLiteHelper(this);
        //final int timeSeconds = db.getVegetable(vegetableID).getCookingTime();
        final int timeSeconds = 10;

        kookplaat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start(kookplaat, kookplaattext, timeSeconds);
            }
        });

    }

    //this init function requires 2 additional buttons for setting a custom timer or selecting from the vegetable database
    void init(final ProgressBar kookplaat, final TextView kookplaattext,final ImageButton dbTimeButton, final ImageButton customTimeButton) {

        //TODO
        //create a selection thingy to select which vegetable to put on this timer
        //int vegetableID = vegetablePicker();
        //MySQLiteHelper db = new MySQLiteHelper(this);
        //final int timeSeconds = db.getVegetable(vegetableID).getCookingTime();
        final int timeSeconds = 10;

        kookplaat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //show buttons
                if(!timeSet) {
                    //show timer buttons
                    dbTimeButton.setVisibility(View.VISIBLE);
                    customTimeButton.setVisibility(View.VISIBLE);
                }else{
                    start(kookplaat, kookplaattext, timeSeconds);
                }

            }
        });

        dbTimeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //TODO
                //functie om te kiezen uit database met kooktijden

                dbTimeButton.setVisibility(View.INVISIBLE);
                customTimeButton.setVisibility(View.INVISIBLE);

                timeSet = true;
                kookplaattext.setText("START");
            }
        });

        customTimeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //TODO
                //functie zelf een kooktijd te kiezen

                dbTimeButton.setVisibility(View.INVISIBLE);
                customTimeButton.setVisibility(View.INVISIBLE);

                timeSet = true;
                kookplaattext.setText("START");
            }
        });

    }

    void start(final ProgressBar kookplaat, final TextView kookplaattext, final int timeSeconds) {

        //set the cooking time as max for the progressbar
        kookplaat.setMax(timeSeconds);

        CountDownTimer timer = new CountDownTimer(timeSeconds * 1000, 500) {
            // 500 means, onTick function will be called at every 500 milliseconds

            @Override
            public void onTick(long leftTimeInMilliseconds) {
                //calculate and set progress
                long seconds = leftTimeInMilliseconds / 1000;
                int barVal = (timeSeconds) - ((int) (seconds / 60 * 100) + (int) (seconds % 60));
                kookplaat.setProgress(barVal);

                // format the textview to show the easily readable format
                kookplaattext.setText(String.format("%02d", seconds / 60) + ":" + String.format("%02d", seconds % 60));


            }

            @Override
            public void onFinish() {
                if (kookplaattext.getText().equals("00:00")) {
                    kookplaattext.setText("FINISHED");
                }
                timerRunning = false;
                timeSet = false;
            }
        };
        if (!timerRunning) {
            timer.start();
            timerRunning = true;
        }
    }
}
