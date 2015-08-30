package com.tomdoesburg.kooktijden.kookplaten;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tomdoesburg.kooktijden.KooktijdenDialog;
import com.tomdoesburg.kooktijden.KooktijdenDialogTwoButtons;
import com.tomdoesburg.kooktijden.MainActivity;
import com.tomdoesburg.kooktijden.R;
import com.tomdoesburg.kooktijden.StateSaver;
import com.tomdoesburg.kooktijden.TimerService;
import com.tomdoesburg.kooktijden.VegetableAlarm;
import com.tomdoesburg.model.Vegetable;
import com.tomdoesburg.sqlite.MySQLiteHelper;

import java.util.Locale;
import java.util.Timer;

/**
 * Created by Joost on 6-10-2014.
 */
public class ActivityZoomedKookplaat extends Activity implements KooktijdenDialogTwoButtons.ActivityCommunicator{

    private static final String TAG = "ActivityZoomedKookplaat";

    //variable to keep track of current layout state
    TimerStates timerState = TimerStates.TIMER_RUNNING;
    //used for service connection
    TimerService mService;
    private boolean mServiceBound = false;

    private boolean timerRunning = true;
    private int kookPlaatID;
    private int vegID;
    private ImageButton pause;
    private ImageButton stop;
    private Button plus;
    private TextView vegetableName;
    private Vegetable vegetable;
    private ProgressBar progress;
    private TextView text;
    private TextView firstLetterTV;
    private int cookingTime = 0; //cooking time in minutes
    private int secondsLeft; //time left in seconds
    private StateSaver stateSaver;
    private Animation highlightNoListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.kookplaat_zoom,null);

        System.gc();
        highlightNoListener =  AnimationUtils.loadAnimation(this, R.anim.highlight_zoom);
        //get the kookplaat that was selected
        Intent intent = getIntent();
        kookPlaatID = intent.getIntExtra("kookPlaatID",0);
        vegID = intent.getIntExtra("vegetableID",0);

        Typeface typeFace = Typeface.createFromAsset(this.getAssets(), "fonts/Roboto-Thin.ttf");

        //get vegetable from database
        MySQLiteHelper db = new MySQLiteHelper(this);
        this.vegetable = db.getVegetable(vegID);
        //set cooking time
        this.cookingTime = db.getVegetable(vegID).getCookingTimeMin();

        if(vegID < 0) { //custom vegetable
            Vegetable veg = new Vegetable();
            veg.setId(-1);
            veg.setNameEN(getResources().getString(R.string.unknown));
            veg.setNameNL(getResources().getString(R.string.unknown));
            veg.setDescriptionNL("");
            veg.setDescriptionEN("");
            veg.setCookingTimeMax(10);
            veg.setCookingTimeMin(10);
            this.vegetable = veg;
            this.cookingTime = veg.getCookingTimeMin();
        }

        //init progress bar and kookplaat
        //this.kookplaat1 = view.findViewById(R.id.kookplaat1);
        this.progress = (ProgressBar) view.findViewById(R.id.kookplaat);
        this.text = (TextView) view.findViewById(R.id.kookplaatText);
        this.text.setText("");
        this.firstLetterTV = (TextView) view.findViewById(R.id.firstLetterTV);
        firstLetterTV.setText("");
        this.progress.setMax(this.cookingTime*60);


        //init buttons etc.
        pause = (ImageButton) view.findViewById(R.id.startStopButton);
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

        Intent intent = new Intent(this.getApplicationContext(), TimerService.class);
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        mServiceBound = true;

        this.registerReceiver(bReceiver, new IntentFilter(TimerService.TIMER_SERVICE));
        Log.i(TAG, "Registered broacast receiver");
        TimerService.runningOnForeground = true;
        TimerService.activityWithoutStovesActive = false;

        if(mService!=null){
            VegetableAlarm vegAlarm = mService.getTimer(kookPlaatID);
            progress.setMax(vegAlarm.getCookingTime() * 60);
            progress.setProgress(progress.getMax()-vegAlarm.getTimeLeft());

            this.vegetable = vegAlarm.getVegetable();
            this.cookingTime = vegetable.getCookingTimeMin();
            String firstLetter = String.valueOf(getVegetableName(vegetable).toUpperCase().charAt(0));
            firstLetterTV.setText(firstLetter);
        }
        //make all the buttons work

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(highlightNoListener);
                if(mService!=null){
                    if(!mService.isRunning(kookPlaatID) && mService.getTimeLeft(kookPlaatID) > 0) {
                        mService.startTimer(kookPlaatID);
                        timerState = TimerStates.TIMER_RUNNING;
                        updateUI();
                    }else{
                        mService.stopTimer(kookPlaatID);
                        timerState = TimerStates.TIMER_PAUSED;
                        updateUI();
                    }

                }
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(highlightNoListener);
                KooktijdenDialogTwoButtons dialog = new KooktijdenDialogTwoButtons(ActivityZoomedKookplaat.this,getString(R.string.stop_timer_title),getString(R.string.stop_timer_message));
                dialog.show();
                //createStopDialog();
            }
        });

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(highlightNoListener);
                if(mService!=null){
                    VegetableAlarm vegAlarm = mService.getTimer(kookPlaatID);
                    mService.addAdditionalTime(kookPlaatID,30);//add 30 seconds to timer
                    text.setText(formatTime(vegAlarm.getTimeLeft()));
                }
            }
        });

        Log.v(TAG, "timerRunning = " + timerRunning);
        //set current time

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

    public void updateUI(){
        switch(timerState){
            case TIMER_PAUSED:
                pause.setImageResource(R.drawable.icon_play);
                break;
            case TIMER_RUNNING:
                pause.setImageResource(R.drawable.icon_pause);
                break;
            case TIMER_FINISHED:
                text.setText("0:00");
                pause.setImageResource(R.drawable.icon_play);
                break;
            default:
                break;
        }
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TimerService.LocalBinder binder = (TimerService.LocalBinder) service;
            mService = binder.getService();
            mServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
        }
    };

    private BroadcastReceiver bReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //Log.v(TAG,"Received broadcast");

            if(intent.hasExtra(TimerService.TIMER_DONE)){
                //get vegetable name that is done
                String vegName = intent.getStringExtra(TimerService.TIMER_DONE);
                //open dialog
                String alarmText = getString(R.string.the_alarm_for) + " " + vegName + " " + getString(R.string.has_finished);
                new KooktijdenDialog(ActivityZoomedKookplaat.this,getString(R.string.timer_ready),alarmText).show();

            }else if(intent.hasExtra(TimerService.TIMER_TICK)){
                //update screen
                onTick();
            }
        }
    };



    private void createStopDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.stop_timer).setTitle(R.string.stop_timer_title);

        builder.setPositiveButton(R.string.stop, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

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

        unregisterReceiver(bReceiver);
        Log.i(TAG, "Unregistered broacast receiver");

        if (mServiceBound) {
            unbindService(mServiceConnection);
            mServiceBound = false;
        }


        Log.d(TAG,"ActivityZoomedKookplaat onPause() saving state");
        stateSaver =  new StateSaver(this.getApplicationContext());
        stateSaver.saveStates();

        Log.i(TAG, "Unregistered broacast receiver");
        TimerService.runningOnForeground = false;
    }




    private String getVegetableName(Vegetable veg){
        String language = Locale.getDefault().getDisplayLanguage();

        if(language.equals("Nederlands")){
            return veg.getNameNL();
        }else{
            return veg.getNameEN();
        }

    }

    public void onTick(){

        if(mService!=null && mService.hasAlarm(kookPlaatID)){
            VegetableAlarm vegAlarm = mService.getTimer(kookPlaatID);
            progress.setMax(vegAlarm.getCookingTime() * 60);
            progress.setProgress(progress.getMax() - vegAlarm.getTimeLeft());
            this.vegetable = vegAlarm.getVegetable();
            String firstLetter = String.valueOf(getVegetableName(vegetable).toUpperCase().charAt(0));
            firstLetterTV.setText(firstLetter);

            if(vegAlarm.isRunning()){
                text.setText(formatTime(vegAlarm.getTimeLeft()));
                timerState = TimerStates.TIMER_RUNNING;
                updateUI();
            }else if(vegAlarm.isFinished()){
                text.setText("0:00");
                timerState = TimerStates.TIMER_FINISHED;
                updateUI();
            }else  if(!vegAlarm.isRunning()){
                timerState = TimerStates.TIMER_PAUSED;
                updateUI();
            }
        }
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.fade_in, R.anim.slide_left2right);
        super.onBackPressed();
    }

    @Override
    public void resetDialogYesClicked() {
        if(mService!=null){
            mService.removeTimer(kookPlaatID);
        }
        onBackPressed();
    }

    @Override
    public void resetDialogCancelClicked(){
        //do nothing
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

    public enum TimerStates{TIMER_RUNNING,TIMER_FINISHED,TIMER_PAUSED}
}
