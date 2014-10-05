package com.tomdoesburg.kooktijden;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by FrankD on 13-9-2014.
 */
public class TimerService extends Service {

    private final static String TAG = "TimerService";
    public final static String KILL_SERVICE = "KILL_SERVICE";

    public static boolean timer1Running = true;
    public static boolean timer2Running = true;
    public static boolean timer3Running = true;
    public static boolean timer4Running = true;
    public static boolean timer5Running = true;
    public static boolean timer6Running = true;


    //time left before end of alarm in seconds
    public static int deadline1 = 0;
    public static int deadline2 = 0;
    public static int deadline3 = 0;
    public static int deadline4 = 0;
    public static int deadline5 = 0;
    public static int deadline6 = 0;

    //selected vegetable (comes in handy when returning to app)
    public static int vegID1;
    public static int vegID2;
    public static int vegID3;
    public static int vegID4;
    public static int vegID5;
    public static int vegID6;

    public static final String TIMER_SERVICE = "com.tomdoesburg.kooktijden.TimerService";
    Intent bi = new Intent(TIMER_SERVICE);

    @Override
    public void onCreate() {
        super.onCreate();
        //start handler that ticks every second
        timerHandler.postDelayed(timerRunnable, 0);
    }

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {

            tick();
            timerHandler.postDelayed(this, 1000);
        }
    };

    //subtracts time from deadlines every second
    public void tick(){

        bi.putExtra("deadline1", deadline1);
        bi.putExtra("deadline2", deadline2);
        bi.putExtra("deadline3", deadline3);
        bi.putExtra("deadline4", deadline4);
        bi.putExtra("deadline5", deadline5);
        bi.putExtra("deadline6", deadline6);

        sendBroadcast(bi);

        if(deadline1 > 0 && timer1Running){
            deadline1 --;
        }
        if(deadline2 > 0 && timer2Running){
            deadline2 --;
        }
        if(deadline3 > 0 && timer3Running){
            deadline3 --;
        }
        if(deadline4 > 0 && timer4Running){
            deadline4 --;
        }if(deadline5 > 0 && timer5Running){
            deadline5 --;
        }if(deadline6 > 0 && timer6Running){
            deadline6 --;
        }
        /*
        Log.v(TAG, "Deadline 1: " + deadline1);
        Log.v(TAG, "Deadline 2: " + deadline2);
        Log.v(TAG, "Deadline 3: " + deadline3);
        Log.v(TAG, "Deadline 4: " + deadline4);
        Log.v(TAG, "Deadline 5: " + deadline5);
        Log.v(TAG, "Deadline 6: " + deadline6);
        */
        if(doneCounting()){
            killService();
        }
    }

    //indicates whether or not we can stop the service
    public boolean doneCounting(){
        return deadline1 +
                deadline2 +
                deadline3 +
                deadline4 +
                deadline5 +
                deadline6
                == 0;
    }

    public void killService(){
        this.stopSelf();
    }

    @Override
    public void onDestroy() {
        timerHandler.removeCallbacks(timerRunnable);
        Log.i(TAG, "Service comitted suicide Aaaah");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //a lot of try catch blocks. You never know how many alarms we have running and if they are initialized
        try {
            Bundle extras = intent.getExtras();
            int deadline = extras.getInt("kookPlaat1");
            int vegID = extras.getInt("vegID");

            if(deadline > 0) {
                deadline1 = deadline;
                vegID1 = vegID;
            }

        }catch(NullPointerException e){
            //do nothing
        }

        try {
            Bundle extras = intent.getExtras();
            int deadline = extras.getInt("kookPlaat2");
            int vegID = extras.getInt("vegID");

            if(deadline > 0) {
                deadline2 = deadline;
                vegID2 = vegID;
            }
        }catch(NullPointerException e){
            //do nothing
        }

        try {
            Bundle extras = intent.getExtras();
            int deadline = extras.getInt("kookPlaat3");
            int vegID = extras.getInt("vegID");

            if(deadline > 0) {
                deadline3 = deadline;
                vegID3 = vegID;
            }

        }catch(NullPointerException e){
            //do nothing
        }

        try {
            Bundle extras = intent.getExtras();
            int deadline = extras.getInt("kookPlaat4");
            int vegID = extras.getInt("vegID");

            if(deadline > 0) {
                deadline4 = deadline;
                vegID4 = vegID;
            }
        }catch(NullPointerException e){
            //do nothing
        }

        try {
            Bundle extras = intent.getExtras();
            int deadline = extras.getInt("kookPlaat5");
            int vegID = extras.getInt("vegID");

            if(deadline > 0) {
                deadline5 = deadline;
                vegID5 = vegID;
            }
        }catch(NullPointerException e){
            //do nothing
        }

        try {
            Bundle extras = intent.getExtras();
            int deadline = extras.getInt("kookPlaat6");
            int vegID = extras.getInt("vegID");

            if(deadline > 0) {
                deadline6 = deadline;
                vegID6 = vegID;
            }
        }catch(NullPointerException e){
            //do nothing
        }

        try{
            Bundle extras = intent.getExtras();
            boolean kill = extras.getBoolean(KILL_SERVICE);
            if(kill){
                killService();
            }

        }catch(NullPointerException e){
            //do nothing
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}
