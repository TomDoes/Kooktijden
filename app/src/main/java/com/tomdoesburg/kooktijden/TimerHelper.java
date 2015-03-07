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

    private String kookPlaatID = ""; //can be kookPlaat1 up to kookPlaat6
    private Vegetable vegetable;
    private int cookingTime = 0; //cooking time in minutes
    private int secondsLeft; //time left in seconds

    //booleans
    private boolean vegetableSelected = false;
    private boolean timerRunning = false;
    private ProgressBar progress;
    private TextView text;
    private static WeakReference<Context> weakContext;

   private static Typeface typeFace;

    //animations
    private Animation highlight;

    public void init(Context context, final View kookplaatview, final String kookPlaatID) {
        this.weakContext = new WeakReference<Context>(context);
        this.progress = (ProgressBar) kookplaatview.findViewById(R.id.kookplaat);
        this.text = (TextView) kookplaatview.findViewById(R.id.kookplaatText);
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
                progress.startAnimation(highlight); //calls startVegetableActivity(); after animation

                if(vegetableSelected && !timerRunning && secondsLeft > 0){

                    if(text.getText().equals("Start")) {
                        startTimerService();
                       // ((MainActivity) getActivity()).lock();
                    }else{
                        //open a zoomed-in view of the selected kookplaat
                        Intent intent = new Intent(weakContext.get().getApplicationContext(), ActivityZoomedKookplaat.class);
                        intent.putExtra("kookPlaatID", kookPlaatID);
                        intent.putExtra("vegetableID", getVegID(getKookPlaatNum()));
                        weakContext.get().startActivity(intent);
                    }

                }else if(vegetableSelected){
                    //open a zoomed-in view of the selected kookplaat
                    Intent intent = new Intent(weakContext.get().getApplicationContext(), ActivityZoomedKookplaat.class);
                    intent.putExtra("kookPlaatID", kookPlaatID);
                    intent.putExtra("vegetableID", getVegID(getKookPlaatNum()));
                    weakContext.get().startActivity(intent);
                }
            }
        });

        resumeExistingTimer();

    }

    public void reset(){
        vegetable = null;
        cookingTime = 0; //cooking time in minutes
        secondsLeft = 0; //time left in seconds

        //booleans
        vegetableSelected = false;
        timerRunning = false;
        text.setText(weakContext.get().getString(R.string.pickfood));

    }

    public void startVegetableActivity(){
        if(!vegetableSelected) {
            Context context = weakContext.get();
            Intent intent = new Intent(context.getApplicationContext(), VegetableActivity.class);
            intent.putExtra("kookPlaatID", kookPlaatID);
            ((MainActivity) context).startActivityForResult(intent, 9001);   //IT'S OVER NINE THOUSAND!
        }
    }

    public void setVegetable(Vegetable veg){
        Log.v(TAG, "setvegetable timerhelper");
        this.timerRunning = false;
        this.vegetable = veg;
        this.cookingTime = veg.getCookingTimeMin();
        this.secondsLeft = this.cookingTime*60;
        this.vegetableSelected = true;
        progress.setMax(this.cookingTime*60);
        text.setText(weakContext.get().getString(R.string.start));
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
        text.setText(weakContext.get().getString(R.string.paused));
    }

    private String getVegetableName(Vegetable veg){
        String language = Locale.getDefault().getDisplayLanguage();

        if(language.equals("Nederlands")){
            return veg.getNameNL();
        }else{
            return veg.getNameEN();
        }

    }

    public void startTimerService(){ //starts the countdown for the cooking time of the vegetable
        int kookPlaatnum = getKookPlaatNum();

        if(this.secondsLeft > 0) {

            switch (kookPlaatnum) {
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
            Intent intent = new Intent(weakContext.get().getApplicationContext(), TimerService.class);
            intent.putExtra(this.kookPlaatID, this.secondsLeft);
            intent.putExtra("vegID", vegetable.getId());
            intent.putExtra("vegName", getVegetableName(vegetable)); //gets correct name based on display language
            weakContext.get().startService(intent);
        }
    }

    public void resumeExistingTimer(){ //used when returning to app and timer still running
        int kookPlaatNum = getKookPlaatNum();

        if(getTimerDeadline(kookPlaatNum) > 0){ //there is a timer for this kookplaat active in a service!
            //get vegetable ID from service and set vegetable in TimerHelper
            int vegID = getVegID(kookPlaatNum);

            Vegetable veg = ((MainActivity) weakContext.get()).getVegetableFromDB(vegID);
            setVegetable(veg);

            this.timerRunning = isTimerRunning(kookPlaatNum);
            //retrieve our deadline from service
            this.secondsLeft = getTimerDeadline(kookPlaatNum);
            //set new max value for the progress bar
            int additionalTime = getAdditionalTime(kookPlaatNum);
            if(additionalTime > 0){
                progress.setMax(this.cookingTime + additionalTime);
            }

            if(!timerRunning){ //paused state
                //this.secondsLeft--; //time in seconds
                this.secondsLeft = getTimerDeadline(getKookPlaatNum());
                int barVal = progress.getMax()-secondsLeft;
                progress.setProgress(barVal);

                // format the textview to show the easily readable format
                text.setText(String.format("%02d", secondsLeft / 60) + ":" + String.format("%02d", secondsLeft % 60));
            }
            onTick();
        }

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

    public int getTimerDeadline(int kookPlaatNum){
        switch(kookPlaatNum){
            case 1: return TimerService.deadline1;

            case 2: return TimerService.deadline2;

            case 3: return TimerService.deadline3;

            case 4: return TimerService.deadline4;

            case 5: return TimerService.deadline5;

            case 6: return TimerService.deadline6;

        }
        return 0;
    }

    public int getAdditionalTime(int kookPlaatNum){
        switch(kookPlaatNum){
            case 1: return TimerService.deadline1Add;

            case 2: return TimerService.deadline2Add;

            case 3: return TimerService.deadline3Add;

            case 4: return TimerService.deadline4Add;

            case 5: return TimerService.deadline5Add;

            case 6: return TimerService.deadline6Add;

        }
        return 0;
    }

    public int getVegID(int kookPlaatNum){
        switch(kookPlaatNum){
            case 1: return TimerService.vegID1;

            case 2: return TimerService.vegID2;

            case 3: return TimerService.vegID3;

            case 4: return TimerService.vegID4;

            case 5: return TimerService.vegID5;

            case 6: return TimerService.vegID6;

        }
        return 0;
    }

    public boolean isTimerRunning(int kookPlaatNum){
        switch(kookPlaatNum){
            case 1: return TimerService.timer1Running;

            case 2: return TimerService.timer2Running;

            case 3: return TimerService.timer3Running;

            case 4: return TimerService.timer4Running;

            case 5: return TimerService.timer5Running;

            case 6: return TimerService.timer6Running;
        }
        return false;
    }

    public boolean isTimerFinished(int kookPlaatNum){
        switch(kookPlaatNum){
            case 1: return TimerService.timer1Finished;

            case 2: return TimerService.timer2Finished;

            case 3: return TimerService.timer3Finished;

            case 4: return TimerService.timer4Finished;

            case 5: return TimerService.timer5Finished;

            case 6: return TimerService.timer6Finished;
        }
        return false;
    }

    public void onTick(){ //this function will be called by the timerService via mainactivity
        int kookPlaatNum = getKookPlaatNum();

        this.timerRunning = isTimerRunning(getKookPlaatNum());

        if(this.timerRunning) {
            //int kookPlaatNum = getKookPlaatNum();

            //update progressbar max value
            int additionalTime = getAdditionalTime(kookPlaatNum);
            if(additionalTime > 0){
                progress.setMax(this.cookingTime *60 + additionalTime);
            }

            //this.secondsLeft--; //time in seconds
            this.secondsLeft = getTimerDeadline(kookPlaatNum);
            int barVal = progress.getMax()-secondsLeft;
            progress.setProgress(barVal);

            // format the textview to show the easily readable format
            text.setText(String.format("%02d", secondsLeft / 60) + ":" + String.format("%02d", secondsLeft % 60));

        }else if(isTimerFinished(kookPlaatNum)){
            progress.setProgress(progress.getMax());
            text.setText("00:00");
            int vegID = getVegID(kookPlaatNum);
            Vegetable veg = ((MainActivity) weakContext.get()).getVegetableFromDB(vegID);
            this.vegetable = veg;
            this.vegetableSelected = true;
        }

    }

}
