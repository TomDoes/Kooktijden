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

import com.tomdoesburg.kooktijden.MainActivity;
import com.tomdoesburg.kooktijden.R;
import com.tomdoesburg.kooktijden.TimerService;
import com.tomdoesburg.kooktijden.vegetables.VegetableActivity;
import com.tomdoesburg.model.Vegetable;

/**
 * Created by Joost on 11-7-2014.
 */
public class TimerHelper {


    private String kookPlaatID = ""; //can be kookPlaat1 up to kookPlaat6
    private Vegetable vegetable;
    private int cookingTime = 0; //cooking time in minutes
    private int secondsLeft; //time left in seconds
    private Activity activity;

    //booleans
    private boolean vegetableSelected = false;
    private boolean timerRunning = false;
    private MediaPlayer mediaPlayer;
    private ProgressBar progress;
    private TextView text;

    void init(final Activity activity, final ProgressBar progress, final TextView text,final Button plusButton, final String kookPlaatID) {
        this.activity = activity;
        this.kookPlaatID = kookPlaatID;
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
                startVegetableActivity();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        progress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress.startAnimation(anim); //calls startVegetableActivity(); after animation

                if(vegetableSelected && !timerRunning){
                    startTimerService();
                    ((MainActivity)activity).lock();
                }else if(vegetableSelected && timerRunning){
                    pauseTimer();
                }
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

    public void startVegetableActivity(){
        if(!vegetableSelected) {
            Intent intent = new Intent(activity, VegetableActivity.class);
            intent.putExtra("kookPlaatID", kookPlaatID);
            activity.startActivityForResult(intent, 9001);   //IT'S OVER NINE THOUSAND!
        }
    }

    public void setVegetable(Vegetable veg){
        this.timerRunning = false;
        this.vegetable = veg;
        this.cookingTime = veg.getCookingTimeMin();
        this.secondsLeft = this.cookingTime*60;
        this.vegetableSelected = true;
        progress.setMax(this.cookingTime*60);
        text.setText(activity.getString(R.string.start));
    }

    public void pauseTimer(){
        int kookPlaatnum = getKookPlaatNum();

        switch(kookPlaatnum) {
            case 1:
                TimerService.timer1Running = false;
                break;
            case 2:
                TimerService.timer2Running = false;
                break;
            case 3:
                TimerService.timer3Running = false;
                break;
            case 4:
                TimerService.timer4Running = false;
                break;
            case 5:
                TimerService.timer5Running = false;
                break;
            case 6:
                TimerService.timer6Running = false;
                break;
        }

        timerRunning = false;
        text.setText(activity.getString(R.string.paused));
    }

    public void startTimerService(){ //starts the countdown for the cooking time of the vegetable
        int kookPlaatnum = getKookPlaatNum();

        switch(kookPlaatnum) {
            case 1:
                TimerService.timer1Running = true;
                break;
            case 2:
                TimerService.timer2Running = true;
                break;
            case 3:
                TimerService.timer3Running = true;
                break;
            case 4:
                TimerService.timer4Running = true;
                break;
            case 5:
                TimerService.timer5Running = true;
                break;
            case 6:
                TimerService.timer6Running = true;
                break;
         }

        this.timerRunning = true;
        Intent intent = new Intent(activity, TimerService.class);
        intent.putExtra(this.kookPlaatID,this.secondsLeft);
        activity.startService(intent);

    }

    public int getKookPlaatNum(){
        if(this.kookPlaatID.endsWith("1")) {
            return 1;
        }else if(this.kookPlaatID.endsWith("2")){
            return 2;
        }else if(this.kookPlaatID.endsWith("3")){
            return 3;
        }else if(this.kookPlaatID.endsWith("4")){
            return 4;
        }else if(this.kookPlaatID.endsWith("5")){
            return 5;
        }else if(this.kookPlaatID.endsWith("6")){
            return 6;
        }
        return -1;
    }

    public void onTick(){ //this function will be called by the timerService via mainactivity

        if(this.timerRunning) {

            this.secondsLeft--; //time in seconds
            int barVal = (secondsLeft) - ((int) (secondsLeft / 60 * 100) + (int) (secondsLeft % 60));
            progress.setProgress(barVal);
            // format the textview to show the easily readable format
            text.setText(String.format("%02d", secondsLeft / 60) + ":" + String.format("%02d", secondsLeft % 60));

            if (this.secondsLeft == 0) { //stop timer
                onTimerFinished();
            }
        }

    }

    public void onTimerFinished(){

        this.timerRunning = false;
        //play sound
        mediaPlayer = MediaPlayer.create(progress.getContext(), R.raw.alarm);
        mediaPlayer.start();
        //SHAKE IT!
        Vibrator vibrator = (Vibrator) progress.getContext().getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 500, 500};
        // -1 vibrate once
        // 0 vibrate indefinitely
        vibrator.vibrate(pattern, -1);

    }

}
