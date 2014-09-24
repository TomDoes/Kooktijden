package com.tomdoesburg.kooktijden.kookplaten;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tomdoesburg.kooktijden.R;
import com.tomdoesburg.kooktijden.vegetables.VegetableActivity;

/**
 * Created by Joost on 11-7-2014.
 */
public class TimerHelper {

    boolean timerRunning;
    MediaPlayer mediaPlayer;
    ProgressBar progress;
    TextView text;

    void init(final Activity activity, final ProgressBar progress, final TextView text,final Button plusButton) {

        this.progress = progress;
        this.text = text;

        Typeface typeFace = Typeface.createFromAsset(activity.getAssets(),"fonts/Roboto-Light.ttf");
        text.setTypeface(typeFace);
        plusButton.setTypeface(typeFace);

        final Animation anim = AnimationUtils.loadAnimation(activity, R.anim.highlight_zoom);

        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(activity, VegetableActivity.class);
                //activity.startActivity(intent);
                activity.startActivityForResult(intent,9001);   //IT'S OVER NINE THOUSAND!
                //activity.overridePendingTransition(R.anim.slide_right2left, R.anim.fade_out);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        progress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progress.startAnimation(anim);

                //plusButton.setVisibility(View.VISIBLE);

                //start test-timer
                //start(progress,text,timeSeconds);
                //text.setText("START");

            }
        });

        plusButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //TODO
                //increase time on timer with 30 seconds
            }
        });

    }

    void start(final int timeSeconds) {

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

    void initKookplaat(int timeSeconds){
        //set the cooking time as max for the progressbar
        progress.setMax(timeSeconds);
        text.setText("YEEAH!");
    }
}
