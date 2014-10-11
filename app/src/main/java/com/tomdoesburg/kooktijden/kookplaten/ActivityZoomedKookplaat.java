package com.tomdoesburg.kooktijden.kookplaten;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.tomdoesburg.kooktijden.TimerService;
import com.tomdoesburg.model.Vegetable;
import com.tomdoesburg.sqlite.MySQLiteHelper;

import java.util.Locale;

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
    private View kookplaat1;
    private ProgressBar progress;
    private TextView text;
    private int cookingTime = 0; //cooking time in minutes
    private int secondsLeft; //time left in seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.kookplaat_zoom,null);

        //get the kookplaat that was selected
        Intent intent = getIntent();
        kookPlaatID = intent.getStringExtra("kookPlaatID");
        vegID = intent.getIntExtra("vegetableID",0);

        //get vegetable from database
        MySQLiteHelper db = new MySQLiteHelper(this);
        Vegetable vegetable = db.getVegetable(vegID);

        //set cooking time
        this.cookingTime = db.getVegetable(vegID).getCookingTimeMin();

        //init progress bar and kookplaat
        this.kookplaat1 = view.findViewById(R.id.kookplaat1);
        this.progress = (ProgressBar) kookplaat1.findViewById(R.id.kookplaat);
        this.text = (TextView) kookplaat1.findViewById(R.id.kookplaatText);
        this.text.setText("");
        this.progress.setMax(this.cookingTime*60);

        //init buttons etc.
        pause = (ImageButton) view.findViewById(R.id.buttonTimerPause);
        stop = (ImageButton) view.findViewById(R.id.buttonTimerStop);
        plus = (Button) view.findViewById(R.id.buttonTimerPlus);
        vegetableName = (TextView) view.findViewById(R.id.vegetableName);

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

        //make all the buttons work

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pause the running timer
                //TODO pause the running timer
                if(timerRunning){
                    pauseTimer();
                    pause.setImageResource(R.drawable.icon_play);
                }else{
                    startTimerService();
                    pause.setImageResource(R.drawable.icon_pause);
                }

                //go back to previous activity
                //finish();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //stop the timer on this stove
                stopTimer();
                //go back to previous activity
                finish();
            }
        });

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //increase remaining time of this stove with 30 seconds
                //TODO increase remaining time of this stove with 30 seconds
                addTime();
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(br);
        Log.i(TAG, "Unregistered broacast receiver");
    }

    @Override
    public void onStop() {
        try {
            unregisterReceiver(br);
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
            Intent intent = new Intent(this, TimerService.class);
            intent.putExtra(this.kookPlaatID, this.secondsLeft);
            intent.putExtra("vegID", this.vegID);
            this.startService(intent);
        }
    }

    public void stopTimer(){
        int kookPlaatnum = getKookPlaatNum();

        switch (kookPlaatnum) {
            case 1:
                TimerService.timer1Running = false;
                TimerService.deadline1 = 0;
                break;
            case 2:
                TimerService.timer2Running = false;
                TimerService.deadline2 = 0;
                break;
            case 3:
                TimerService.timer3Running = false;
                TimerService.deadline3 = 0;
                break;
            case 4:
                TimerService.timer4Running = false;
                TimerService.deadline4 = 0;
                break;
            case 5:
                TimerService.timer5Running = false;
                TimerService.deadline5 = 0;
                break;
            case 6:
                TimerService.timer6Running = false;
                TimerService.deadline6 = 0;
                break;
        }
        Intent intent = new Intent(this, MainActivity.class);//return to main activity
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); //clear the back stack
        startActivity(intent);
        finish();
    }

    public void addTime(){
        int kookPlaatnum = getKookPlaatNum();
        this.secondsLeft += 30;

        switch(kookPlaatnum) {
            case 1:
                TimerService.deadline1 += 30;
                break;
            case 2:
                TimerService.deadline2 += 30;
                break;
            case 3:
                TimerService.deadline3 += 30;
                break;
            case 4:
                TimerService.deadline4 += 30;
                break;
            case 5:
                TimerService.deadline5 += 30;
                break;
            case 6:
                TimerService.deadline6 += 30;
                break;
        }

    }

    public void onTick(){
        //TODO complete function!
        if(this.timerRunning) {

            //this.secondsLeft--; //time in seconds
            this.secondsLeft = getTimerDeadline(getKookPlaatNum());
            /*TODO: store and adjust progressbar max value, probably in TimerService
            if(this.secondsLeft > progress.getMax()){ //may be the case if additional time is added
                progress.setMax(this.secondsLeft); //upgrade the max time
            }
            */

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

}
