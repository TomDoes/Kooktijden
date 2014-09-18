package com.tomdoesburg.kooktijden;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by Joost on 11-7-2014.
 */
public class TimerHelper {

    boolean timerRunning;
    MediaPlayer mediaPlayer;

    void init(final Context context, final ProgressBar progress, final TextView text,final Button plusButton, final Button minButton) {

        final int timeSeconds = 10;

        progress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Animation anim = AnimationUtils.loadAnimation(context, R.anim.highlight_zoom);
                progress.startAnimation(anim);

                plusButton.setVisibility(View.VISIBLE);
                minButton.setVisibility(View.VISIBLE);

                //start test-timer
                start(progress,text,timeSeconds);

                text.setText("START");

                /*
                TEMPORARILY DISABLED TO TEST TIMER FUNCTIONALITY

                Intent intent = new Intent(progress.getContext(), VegetableActivity.class);
                progress.getContext().startActivity(intent);
                Activity activity = (Activity)progress.getContext();
                activity.overridePendingTransition(R.anim.slide_right2left, R.anim.fade_out);
                */

            }
        });

        plusButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //TODO
                //increase time on timer
            }
        });

        minButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //TODO
                //reduce time on timer
            }
        });

    }

    void start(final ProgressBar progress, final TextView text, final int timeSeconds) {

        //set the cooking time as max for the progressbar
        progress.setMax(timeSeconds);

        CountDownTimer timer = new CountDownTimer(timeSeconds * 1000, 500) {
            // 500 means, onTick function will be called at every 500 milliseconds

            @Override
            public void onTick(long leftTimeInMilliseconds) {
                //calculate and set progress
                long seconds = leftTimeInMilliseconds / 1000;
                int barVal = (timeSeconds) - ((int) (seconds / 60 * 100) + (int) (seconds % 60));
                progress.setProgress(barVal);

                // format the textview to show the easily readable format
                text.setText(String.format("%02d", seconds / 60) + ":" + String.format("%02d", seconds % 60));


            }

            @Override
            public void onFinish() {
                if (text.getText().equals("00:00")) {
                    text.setText("Ready!");
                    mediaPlayer = MediaPlayer.create(progress.getContext(), R.raw.alarm);
                    mediaPlayer.start();
                    Vibrator vibrator = (Vibrator) progress.getContext().getSystemService(Context.VIBRATOR_SERVICE);
                    long[] pattern = {0, 500, 500};
                    // -1 vibrate once
                    // 0 vibrate indefinitely
                    vibrator.vibrate(pattern, -1);

                }
                timerRunning = false;
            }
        };
        if (!timerRunning) {
            timer.start();
            timerRunning = true;
        }
    }
}
