package com.tomdoesburg.kooktijden;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.tomdoesburg.kooktijden.kookplaten.ActivityZoomedKookplaat;
import com.tomdoesburg.kooktijden.vegetables.VegetableActivity;
import com.tomdoesburg.model.Vegetable;
import java.lang.ref.WeakReference;
import java.util.Locale;

/**
 * Created by FrankD on 24-1-2015.
 */
public class TimerHelper {
    private final String TAG = "TimerHelper";
    static TimerService mService;
    private int kookPlaatID; //1 to 6
    private Vegetable vegetable;
    private int cookingTime = 0; //cooking time in minutes

    //variables to keep track of current layout state
    TimerStates timerState = TimerStates.TIMER_PAUSED;
    VegetableStates vegetableState = VegetableStates.NO_VEGETABLE_SELECTED;

    //booleans
    private ProgressBar progress;
    private TextView text;
    private TextView firstLetterTV;
    private static WeakReference<Context> weakContext;

    private static Typeface typeFace;
    //animations
    private Animation highlight;

    public void init(Context context, final View kookplaatview, final int kookPlaatID) {
        this.weakContext = new WeakReference<Context>(context);
        this.progress = (ProgressBar) kookplaatview.findViewById(R.id.kookplaat);
        this.text = (TextView) kookplaatview.findViewById(R.id.kookplaatText);
        this.firstLetterTV = (TextView) kookplaatview.findViewById(R.id.firstLetterTV);
        firstLetterTV.setText("");
        this.kookPlaatID = kookPlaatID;

        typeFace = Typeface.createFromAsset(weakContext.get().getAssets(),"fonts/Roboto-Light.ttf");
        text.setTypeface(typeFace);

        highlight = AnimationUtils.loadAnimation(weakContext.get(), R.anim.highlight_zoom);

        highlight.setAnimationListener(new Animation.AnimationListener() {
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
                if(vegetableState == VegetableStates.NO_VEGETABLE_SELECTED) {
                    progress.startAnimation(highlight); //calls startVegetableActivity(); after animation
                }
                else if (vegetableState == VegetableStates.VEGETABLE_SELECTED && timerState == TimerStates.TIMER_PAUSED) {
                    startTimer();
                    updateUI();
                } else if (vegetableState == VegetableStates.VEGETABLE_SELECTED) {
                    //open a zoomed-in view of the selected kookplaat
                    Intent intent = new Intent(weakContext.get().getApplicationContext(), ActivityZoomedKookplaat.class);
                    intent.putExtra("kookPlaatID",kookPlaatID);
                    intent.putExtra("vegetableID", vegetable.getId());
                    weakContext.get().startActivity(intent);
                }
            }
        });
    }

    public void updateUI(){
        switch(timerState){
            case TIMER_PAUSED:
                if(vegetableState == VegetableStates.VEGETABLE_SELECTED){
                    text.setText(weakContext.get().getString(R.string.start));
                }
               break;
            case TIMER_RUNNING:
                break;
            case TIMER_FINISHED:
                text.setText("0:00");
                break;
            default:
                break;
        }
    }

    public void setTimerInService(){
        if(mService!=null) {
            VegetableAlarm alarm = new VegetableAlarm(vegetable);
            mService.setTimer(this.kookPlaatID, alarm);
        }
    }

    public void startTimer(){
        if(mService!=null){
            mService.startTimer(this.kookPlaatID);
            timerState = TimerStates.TIMER_RUNNING;
            updateUI();
        }
    }

    public void reset(){
        this.vegetable = null;
        vegetableState = VegetableStates.NO_VEGETABLE_SELECTED;
        timerState = TimerStates.TIMER_PAUSED;
        progress.setProgress(0);
        if(mService!=null){
            mService.removeTimer(kookPlaatID);
        }
    }

    public void startVegetableActivity(){
        if(vegetableState == VegetableStates.NO_VEGETABLE_SELECTED) {
            Context context = weakContext.get();
            Intent intent = new Intent(context.getApplicationContext(), VegetableActivity.class);
            intent.putExtra("kookPlaatID", kookPlaatID);
            ((MainActivity) context).startActivityForResult(intent, 9001);   //IT'S OVER NINE THOUSAND!
        }
    }

    public void setVegetable(Vegetable veg){
        vegetableState = VegetableStates.VEGETABLE_SELECTED;
        Log.v(TAG, "setvegetable timerhelper ID: " + veg.getId());
        String firstLetter = String.valueOf(getVegetableName(veg).toUpperCase().charAt(0));
        firstLetterTV.setText(firstLetter);
        this.vegetable = veg;
        this.cookingTime = veg.getCookingTimeMin();
        progress.setMax(this.cookingTime*60);
        text.setText(weakContext.get().getString(R.string.start));
        setTimerInService();
        updateUI();
    }

    private String getVegetableName(Vegetable veg){
        String language = Locale.getDefault().getDisplayLanguage();

        if(language.equals("Nederlands")){
            return veg.getNameNL();
        }else{
            return veg.getNameEN();
        }
    }

    public void onResume(){
        if(mService!=null && mService.hasAlarm(kookPlaatID)) {
            vegetableState = VegetableStates.VEGETABLE_SELECTED;
            VegetableAlarm vegAlarm = mService.getTimer(kookPlaatID);
            progress.setMax(vegAlarm.getCookingTime()*60);
            progress.setProgress(progress.getMax()-vegAlarm.getTimeLeft());

            this.vegetable = vegAlarm.getVegetable();
            String firstLetter = String.valueOf(getVegetableName(vegetable).toUpperCase().charAt(0));
            firstLetterTV.setText(firstLetter);

            if(vegAlarm.isRunning()){
                text.setText(formatTime(vegAlarm.getTimeLeft()));
            }else if(vegAlarm.isFinished()){
                timerState = TimerStates.TIMER_FINISHED;
                updateUI();
            }else  if(!vegAlarm.isRunning()){
                timerState = TimerStates.TIMER_PAUSED;
                updateUI();
            }
        }
    }

    public void onTick(){ //this function will be called by the timerService via mainactivity
        if(mService!=null && mService.hasAlarm(kookPlaatID)){
            vegetableState = VegetableStates.VEGETABLE_SELECTED;
            VegetableAlarm vegAlarm = mService.getTimer(kookPlaatID);
            progress.setProgress(progress.getMax() - vegAlarm.getTimeLeft());

            if(vegAlarm.isRunning()){
                text.setText(formatTime(vegAlarm.getTimeLeft()));
            }else if(vegAlarm.isFinished()){
                timerState = TimerStates.TIMER_FINISHED;
                updateUI();
            }else  if(!vegAlarm.isRunning()){
                timerState = TimerStates.TIMER_PAUSED;
                updateUI();
            }
        }
    }

    public String formatTime(int secondsLeft){
        String time = "";
        int hours = secondsLeft/3600;
        int minutes = (secondsLeft%3600)/60;
        int seconds = (secondsLeft % 3600)%60;

        String minutesString = "";
        String secondsString = "";
        if(minutes <10){
            minutesString = "0" + minutes;
        }else{
            minutesString = String.valueOf(minutes);
        }
        if(seconds < 10){
            secondsString = "0" + seconds;
        }else{
            secondsString = String.valueOf(seconds);
        }

        if(hours == 0){
            return minutesString + ":" + secondsString;
        }else{
            return hours + ":" + minutesString + ":" + secondsString;
        }
    }

    public enum VegetableStates{NO_VEGETABLE_SELECTED,VEGETABLE_SELECTED}
    public enum TimerStates{TIMER_RUNNING,TIMER_FINISHED,TIMER_PAUSED}

    public static void setService(TimerService service){
        mService = service;
    }

}
