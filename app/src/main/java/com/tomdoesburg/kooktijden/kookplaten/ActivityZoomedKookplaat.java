package com.tomdoesburg.kooktijden.kookplaten;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tomdoesburg.kooktijden.MainActivity;
import com.tomdoesburg.kooktijden.R;
import com.tomdoesburg.kooktijden.StateSaver;
import com.tomdoesburg.kooktijden.TimerService;
import com.tomdoesburg.model.Vegetable;
import com.tomdoesburg.sqlite.MySQLiteHelper;

import java.util.Locale;
import java.util.Timer;

/**
 * Created by Joost on 6-10-2014.
 */
public class ActivityZoomedKookplaat extends Activity{

    private static final String TAG = "ActivityZoomedKookplaat";
    private boolean timerRunning = true;
    private MediaPlayer mediaPlayer;
    private String kookPlaatID;
    private int vegID;
    private ImageButton pause;
    private ImageButton stop;
    private Button plus;
    private TextView vegetableName;
    private Vegetable vegetable;
    //private View kookplaat1;
    private ProgressBar progress;
    private TextView text;
    private int cookingTime = 0; //cooking time in minutes
    private int secondsLeft; //time left in seconds
    private StateSaver stateSaver;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.kookplaat_zoom,null);

        //get the kookplaat that was selected
        Intent intent = getIntent();
        kookPlaatID = intent.getStringExtra("kookPlaatID");
        vegID = intent.getIntExtra("vegetableID",0);

        Typeface typeFace = Typeface.createFromAsset(this.getAssets(), "fonts/Roboto-Thin.ttf");

        //get vegetable from database
        MySQLiteHelper db = new MySQLiteHelper(this);
        this.vegetable = db.getVegetable(vegID);

        //set cooking time
        this.cookingTime = db.getVegetable(vegID).getCookingTimeMin();

        //init progress bar and kookplaat
        //this.kookplaat1 = view.findViewById(R.id.kookplaat1);
        this.progress = (ProgressBar) view.findViewById(R.id.kookplaat1);
        this.text = (TextView) view.findViewById(R.id.kookplaatText);
        this.text.setText("");
        this.progress.setMax(this.cookingTime*60);

        //init buttons etc.
        pause = (ImageButton) view.findViewById(R.id.buttonTimerPause);
        stop = (ImageButton) view.findViewById(R.id.buttonTimerStop);
        plus = (Button) view.findViewById(R.id.buttonTimerPlus);
        vegetableName = (TextView) view.findViewById(R.id.vegetableName);
        vegetableName.setTypeface(typeFace);

        plus.setTypeface(typeFace);

        //set the vegetable name according to locale
        String language = Locale.getDefault().getDisplayLanguage();
        if(language.equals("Nederlands")) {
            vegetableName.setText(vegetable.getNameNL());
        } else {
            vegetableName.setText(vegetable.getNameEN());
        }

        //present view to user
        setContentView(view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.registerReceiver(br, new IntentFilter(TimerService.TIMER_SERVICE));
        Log.i(TAG, "Registered broacast receiver");
        TimerService.runningOnForeground = true;

        Intent intent = new Intent(this.getApplicationContext(), TimerService.class);
        startService(intent);

        //retrieve states only if timer not active
        if(!serviceActive()){
            stateSaver = new StateSaver(this.getApplicationContext());
            stateSaver.retrieveStates();
        }

        //make all the buttons work

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(timerRunning){
                    //pause the running timer
                    pauseTimer();
                    pause.setImageResource(R.drawable.icon_play);
                }else{
                    if(secondsLeft > 0) {
                        startTimerService();
                        pause.setImageResource(R.drawable.icon_pause);
                    }
                }

                //go back to previous activity
                //finish();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createStopDialog();
            }
        });

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //increase remaining time of this stove with 30 seconds
                addTime();
            }
        });

        int kookPlaatNum = getKookPlaatNum();
        //set pause button image (pause when timer is not running and play when running)
        this.timerRunning = isTimerRunning(kookPlaatNum);
        Log.v(TAG, "timerRunning = " + timerRunning);
        //set current time
        this.secondsLeft = getTimerDeadline(kookPlaatNum);

        //update progressbar max value
        int additionalTime = getAdditionalTime(kookPlaatNum);
        if(additionalTime > 0){
            progress.setMax(this.cookingTime *60 + additionalTime);
        }

        int barVal = progress.getMax()-secondsLeft;
        progress.setProgress(barVal);
        // format the textview to show the easily readable format
        Typeface typeFace = Typeface.createFromAsset(this.getAssets(), "fonts/Roboto-Thin.ttf");
        text.setTypeface(typeFace);
        text.setText(String.format("%02d", secondsLeft / 60) + ":" + String.format("%02d", secondsLeft % 60));

        if(timerRunning){
            pause.setImageResource(R.drawable.icon_pause);
        }else{
            pause.setImageResource(R.drawable.icon_play);
        }
    }

    private void createStopDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.stop_timer).setTitle(R.string.stop_timer_title);

        builder.setPositiveButton(R.string.stop, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                stopTimer();
                finish();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.create();

        AlertDialog alertDialog = builder.show();

        int titleDividerId = getResources().getIdentifier("titleDivider", "id", "android");
        int alertTitle = getResources().getIdentifier("alertTitle", "id", "android");
        TextView titleText = (TextView) alertDialog.findViewById(alertTitle);
        View titleDivider = alertDialog.findViewById(titleDividerId);
        if (titleDivider != null) {
            titleDivider.setBackgroundColor(getResources().getColor(R.color.teal));
        }
        if (titleText != null) {
            titleText.setTextColor(getResources().getColor(R.color.teal));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(br);

        Log.d(TAG,"ActivityZoomedKookplaat onPause() saving state");
        stateSaver =  new StateSaver(this.getApplicationContext());
        stateSaver.saveStates();

        Log.i(TAG, "Unregistered broacast receiver");
        TimerService.runningOnForeground = false;
    }

    @Override
    public void onStop() {
        try {
            unregisterReceiver(br);
            TimerService.runningOnForeground = false;
        } catch (Exception e) {
            // Receiver was probably already stopped in onPause()
        }
        super.onStop();
    }

    private BroadcastReceiver br = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            onTick();

        }
    };

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
            Intent intent = new Intent(this.getApplicationContext(), TimerService.class);
            intent.putExtra(this.kookPlaatID, this.secondsLeft);
            intent.putExtra("vegID", this.vegID);
            intent.putExtra("vegName", getVegetableName(vegetable));

            this.startService(intent);
        }
    }

    private String getVegetableName(Vegetable veg){
        String language = Locale.getDefault().getDisplayLanguage();

        if(language.equals("Nederlands")){
            return veg.getNameNL();
        }else{
            return veg.getNameEN();
        }

    }

    public void stopTimer(){
        int kookPlaatnum = getKookPlaatNum();

        switch (kookPlaatnum) {
            case 1:
                TimerService.timer1Running = false;
                TimerService.timer1Finished = false;
                TimerService.deadline1 = 0;
                break;
            case 2:
                TimerService.timer2Running = false;
                TimerService.timer2Finished = false;
                TimerService.deadline2 = 0;
                break;
            case 3:
                TimerService.timer3Running = false;
                TimerService.timer3Finished = false;
                TimerService.deadline3 = 0;
                break;
            case 4:
                TimerService.timer4Running = false;
                TimerService.timer4Finished = false;
                TimerService.deadline4 = 0;
                break;
            case 5:
                TimerService.timer5Running = false;
                TimerService.timer5Finished = false;
                TimerService.deadline5 = 0;
                break;
            case 6:
                TimerService.timer6Running = false;
                TimerService.timer6Finished = false;
                TimerService.deadline6 = 0;
                break;
        }
        Intent intent = new Intent(this.getApplicationContext(), MainActivity.class);//return to main activity
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); //clear the back stack
        startActivity(intent);
        finish();
    }

    public void addTime(){

        //if timer has ended we still should be able to add 30 seconds and continue running
        if(this.secondsLeft == 0 && !timerRunning){
            pause.setImageResource(R.drawable.icon_play);
        }

        int kookPlaatnum = getKookPlaatNum();
        this.secondsLeft += 30;

        switch(kookPlaatnum) {
            case 1:
                TimerService.deadline1 += 30;
                TimerService.deadline1Add += 30;
                TimerService.timer1Finished = false;
                break;
            case 2:
                TimerService.deadline2 += 30;
                TimerService.deadline2Add += 30;
                TimerService.timer2Finished = false;
                break;
            case 3:
                TimerService.deadline3 += 30;
                TimerService.deadline3Add += 30;
                TimerService.timer3Finished = false;
                break;
            case 4:
                TimerService.deadline4 += 30;
                TimerService.deadline4Add += 30;
                TimerService.timer4Finished = false;
                break;
            case 5:
                TimerService.deadline5 += 30;
                TimerService.deadline5Add += 30;
                TimerService.timer5Finished = false;
                break;
            case 6:
                TimerService.deadline6 += 30;
                TimerService.deadline6Add += 30;
                TimerService.timer6Finished = false;
                break;
        }

        //update the max value on the progress bar
        int curMax = progress.getMax();
        this.secondsLeft = getTimerDeadline(getKookPlaatNum());

        if(this.secondsLeft > curMax){
            progress.setMax(curMax + 30);
        }

        //set text
        text.setText(String.format("%02d", secondsLeft / 60) + ":" + String.format("%02d", secondsLeft % 60));
    }

    public void onTick(){
        //TODO complete function!
        if(this.timerRunning) {
            //get time left in seconds
            this.secondsLeft = getTimerDeadline(getKookPlaatNum());
            //update progress bar value
            int barVal = progress.getMax()-secondsLeft;
            progress.setProgress(barVal);

            // format the textview to show the easily readable format
            text.setText(String.format("%02d", secondsLeft / 60) + ":" + String.format("%02d", secondsLeft % 60));

            if (this.secondsLeft == 0) { //stop timer
                onTimerFinished();
            }
        }
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

    public void onTimerFinished(){
        Log.v(TAG, "timer finished");
        this.timerRunning = false;
        //play sound
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm);
        mediaPlayer.start();
        //SHAKE IT!
        Vibrator vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 500, 500};
        // -1 vibrate once
        // 0 vibrate indefinitely
        vibrator.vibrate(pattern, -1);

    }

    public boolean serviceActive(){
        return (TimerService.timer1Running || TimerService.timer2Running || TimerService.timer3Running
                ||TimerService.timer4Running||TimerService.timer5Running || TimerService.timer6Running);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this.getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.slide_left2right);
        super.onBackPressed();

    }

}
