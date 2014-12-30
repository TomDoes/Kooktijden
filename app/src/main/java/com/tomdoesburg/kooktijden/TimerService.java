package com.tomdoesburg.kooktijden;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.tomdoesburg.sqlite.MySQLiteHelper;

import java.util.Locale;

/**
 * Created by FrankD on 13-9-2014.
 */
public class TimerService extends Service {

    private final static String TAG = "TimerService";
    public final static String KILL_SERVICE = "KILL_SERVICE";
    public final static String TIMER_DONE = "TIMER_DONE";
    public final static String VEGID = "VEGID";

    private MediaPlayer mediaPlayer;
    public static boolean alarmOn = false;
    public static boolean runningOnForeground = true; //indicates whether or not app is visible to user (true) or working in background (false)
    public static final int notificationID = 1;
    public static int timerReadyID = 2;
    private  NotificationCompat.Builder mBuilder;
    private boolean firstNotification = true; //lets us know if we can re-use (false) a notification or create a new one (true)
    private boolean killHandlerCalled = false;

    //used to keep the service running when phone goes to sleep
    private PowerManager powerManager;
    private WakeLock wakeLock;

    //vegetable database, used for displaying vegetable names in notifications
    public static MySQLiteHelper db;

    public static boolean timer1Running = false;
    public static boolean timer2Running = false;
    public static boolean timer3Running = false;
    public static boolean timer4Running = false;
    public static boolean timer5Running = false;
    public static boolean timer6Running = false;

    //if a timer is done, the appropriate finished variable is set to true
    public static boolean timer1Finished = false;
    public static boolean timer2Finished = false;
    public static boolean timer3Finished = false;
    public static boolean timer4Finished = false;
    public static boolean timer5Finished = false;
    public static boolean timer6Finished = false;

    //time left before end of alarm in seconds
    public static int deadline1 = 0;
    public static int deadline2 = 0;
    public static int deadline3 = 0;
    public static int deadline4 = 0;
    public static int deadline5 = 0;
    public static int deadline6 = 0;

    //additional time (set by using the +30 button)
    public static int deadline1Add = 0;
    public static int deadline2Add = 0;
    public static int deadline3Add = 0;
    public static int deadline4Add = 0;
    public static int deadline5Add = 0;
    public static int deadline6Add = 0;

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



    //handler for delayed killing of service
    Handler killHandler = new Handler();
    Runnable killRunnable = new Runnable() {

        @Override
        public void run() {
            killHandlerCalled = false;

            if(doneCounting() || onlyPausedStates()) {
                killService();
            }
        }
    };

    //handler for repeating alarm when app is in background
    Handler alarmHandler = new Handler();
    Runnable alarmRunnable = new Runnable() {

        @Override
        public void run() {
            onTimerFinished();
            //  alarmHandler.postDelayed(this, 1000);
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
            if(deadline1 == 0){
                timer1Running = false;
                timer1Finished = true;
                onTimerFinished();
            }
        }
        if(deadline2 > 0 && timer2Running){
            deadline2 --;
            if(deadline2 == 0){
                timer2Running = false;
                timer2Finished = true;
                onTimerFinished();
            }
        }
        if(deadline3 > 0 && timer3Running){
            deadline3 --;
            if(deadline3 == 0){
                timer3Running = false;
                timer3Finished = true;
                onTimerFinished();
            }
        }
        if(deadline4 > 0 && timer4Running){
            deadline4 --;
            if(deadline4 == 0){
                timer4Running = false;
                timer4Finished = true;
                onTimerFinished();
            }
        }if(deadline5 > 0 && timer5Running){
            deadline5 --;
            if(deadline5 == 0){
                timer5Running = false;
                timer5Finished = true;
                onTimerFinished();
            }
        }if(deadline6 > 0 && timer6Running){
            deadline6 --;
            if(deadline6 == 0){
                timer6Finished = true;
                timer6Running = false;
                onTimerFinished();
            }
        }

        if(!runningOnForeground && timerActive()){
            showNotification(this.notificationID);
        }else{
            hideNotification(this.notificationID);
        }

        //method is in paused state and user has left the app.
        // killing service is more cpu efficient in this case
        if(!killHandlerCalled && !runningOnForeground && onlyPausedStates()){
            killHandlerCalled = true;
            killHandler.postDelayed(killRunnable,1000);
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

    //returns true if not a single timer is running
    public boolean onlyPausedStates(){
        boolean anyTimerRunning = (timer1Running || timer2Running || timer3Running
                || timer4Running || timer5Running || timer6Running);

        if(!anyTimerRunning){
            return true;
        }else{//anyTimerRunning == true
            return false;
        }
    }

    public void killService(){
        deadline1 = 0;
        deadline2 = 0;
        deadline3 = 0;
        deadline4 = 0;
        deadline5 = 0;
        deadline6 = 0;

        deadline1Add = 0;
        deadline2Add = 0;
        deadline3Add = 0;
        deadline4Add = 0;
        deadline5Add = 0;
        deadline6Add = 0;

        timer1Running = false;
        timer2Running = false;
        timer3Running = false;
        timer4Running = false;
        timer5Running = false;
        timer6Running = false;


        //hide notifications from screen
        hideNotification(this.notificationID);

        //remove handler callbacks
        timerHandler.removeCallbacks(timerRunnable);

        //release wakelock (if any was held)
        if (wakeLock.isHeld()){
            wakeLock.release();
        }

        //kill service
        this.stopSelf();
    }

    @Override
    public void onDestroy() {
        hideNotification(this.notificationID);
        timerHandler.removeCallbacks(timerRunnable);
        if (wakeLock.isHeld()){
            wakeLock.release();
        }
        Log.d(TAG, "Service comitted suicide Aaaah");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //a lot of try catch blocks. You never know how many alarms we have running and if they are initialized
        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock((PowerManager.PARTIAL_WAKE_LOCK), "TimerService");
        wakeLock.setReferenceCounted(false); //only 1 call to acquire is enough to acquire, only 1 release required to release

        if(!wakeLock.isHeld()) {
            wakeLock.acquire();
        }
        try {
            Bundle extras = intent.getExtras();
            int deadline = extras.getInt("kookPlaat1");
            int vegID = extras.getInt("vegID");

            if(deadline > 0) {
                timer1Running = true;
                timer1Finished = false;
                deadline1 = deadline;
                deadline1Add = 0;
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
                timer2Running = true;
                timer2Finished = false;
                deadline2 = deadline;
                deadline2Add = 0;
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
                timer3Running = true;
                timer3Finished = false;
                deadline3 = deadline;
                deadline3Add = 0;
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
                timer4Running = true;
                timer4Finished = false;
                deadline4 = deadline;
                deadline4Add = 0;
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
                timer5Running = true;
                timer5Finished = false;
                deadline5 = deadline;
                deadline5Add = 0;
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
                timer6Running = true;
                timer6Finished = false;
                deadline6 = deadline;
                deadline6Add = 0;
                vegID6 = vegID;
            }
        }catch(NullPointerException e){
            //do nothing
        }

        try{
            Bundle extras = intent.getExtras();
            boolean kill = extras.getBoolean(KILL_SERVICE);
            if(kill){
                killHandler.postDelayed(killRunnable,5000);
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

    private boolean timerActive(){
        //returns true if there is any timer currently running
        return(timer1Running || timer2Running || timer3Running
                ||timer4Running || timer5Running || timer6Running);

    }

    private String getFirstAlarmTime(){
        //returns current time of the active alarm closest to zero
        int soonestDeadline = 0;

        if(timer1Running && (soonestDeadline > deadline1 || soonestDeadline == 0)){
            soonestDeadline = deadline1;
        }
        if(timer2Running && (soonestDeadline > deadline2 || soonestDeadline == 0)){
            soonestDeadline = deadline2;
        }
        if(timer3Running && (soonestDeadline > deadline3 || soonestDeadline == 0)){
            soonestDeadline = deadline3;
        }
        if(timer4Running && (soonestDeadline > deadline4 || soonestDeadline == 0)){
            soonestDeadline = deadline4;
        }
        if(timer5Running && (soonestDeadline > deadline5 || soonestDeadline == 0)){
            soonestDeadline = deadline5;
        }
        if(timer6Running && (soonestDeadline > deadline6 || soonestDeadline == 0)){
            soonestDeadline = deadline6;
        }

        String returnVal = String.format("%02d", soonestDeadline / 60) + ":" + String.format("%02d", soonestDeadline % 60);

        return returnVal;
    }

    private String getFirstAlarmText(){
        //returns current time of the active alarm closest to zero
        int soonestDeadline = 0;
        String vegetableName = "Timer";
        String language = Locale.getDefault().getDisplayLanguage();
        boolean dutch = language.equals("Nederlands");

        if(timer1Running && (soonestDeadline > deadline1 || soonestDeadline == 0)){
            soonestDeadline = deadline1;

            if(!db.equals(null)) {
                if(dutch){
                    vegetableName = db.getVegetable(vegID1).getNameNL();
                }else {
                    vegetableName = db.getVegetable(vegID1).getNameEN();
                }
            }

        }
        if(timer2Running && (soonestDeadline > deadline2 || soonestDeadline == 0)){
            soonestDeadline = deadline2;

            if(!db.equals(null)) {
                if(dutch){
                    vegetableName = db.getVegetable(vegID2).getNameNL();
                }else {
                    vegetableName = db.getVegetable(vegID2).getNameEN();
                }
            }

        }
        if(timer3Running && (soonestDeadline > deadline3 || soonestDeadline == 0)){
            soonestDeadline = deadline3;

            if(!db.equals(null)) {
                if(dutch){
                    vegetableName = db.getVegetable(vegID3).getNameNL();
                }else {
                    vegetableName = db.getVegetable(vegID3).getNameEN();
                }
            }
        }
        if(timer4Running && (soonestDeadline > deadline4 || soonestDeadline == 0)){
            soonestDeadline = deadline4;

            if(!db.equals(null)) {
                if(dutch){
                    vegetableName = db.getVegetable(vegID4).getNameNL();
                }else {
                    vegetableName = db.getVegetable(vegID4).getNameEN();
                }
            }
        }
        if(timer5Running && (soonestDeadline > deadline5 || soonestDeadline == 0)){
            soonestDeadline = deadline5;

            if(!db.equals(null)) {
                if(dutch){
                    vegetableName = db.getVegetable(vegID5).getNameNL();
                }else {
                    vegetableName = db.getVegetable(vegID5).getNameEN();
                }
            }
        }
        if(timer6Running && (soonestDeadline > deadline6 || soonestDeadline == 0)){
            soonestDeadline = deadline6;

            if(!db.equals(null)) {
                if(dutch){
                    vegetableName = db.getVegetable(vegID6).getNameNL();
                }else {
                    vegetableName = db.getVegetable(vegID6).getNameEN();
                }
            }
        }

        String returnVal = vegetableName + " " + getString(R.string.ready_in) + " " + String.format("%02d", soonestDeadline / 60) + ":" + String.format("%02d", soonestDeadline % 60);

        return returnVal;
    }

    public void showNotification(int ID){
        // Creates an explicit intent for an Activity in your app
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        if(ID == this.notificationID) {
            //notify user that a timer is running
            if(!this.firstNotification){//re-use mBuilder
                mBuilder.setSmallIcon(R.drawable.kooktijden_icon)
                        .setOnlyAlertOnce(true)
                        .setContentTitle(getString(R.string.timer))
                        //.setContentText(getString(R.string.notification_text) + " " + getFirstAlarmTime())
                        .setContentText(getFirstAlarmText())
                        .setContentIntent(pIntent);
            }else{ //create new instance of mBuilder
                this.firstNotification = false;

                mBuilder =
                        new NotificationCompat.Builder(this)
                                .setOnlyAlertOnce(true)
                                .setSmallIcon(R.drawable.kooktijden_icon)
                                .setContentTitle(getString(R.string.timer))
                                //.setContentText(getString(R.string.notification_text) + " " + getFirstAlarmTime())
                                .setContentText(getFirstAlarmText())
                                .setContentIntent(pIntent);
            }

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // mId allows you to update the notification later on.
            mNotificationManager.notify(ID, mBuilder.build());

        }else if(ID == this.timerReadyID){
            //notify user that timer is finished
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.kooktijden_icon)
                            .setContentTitle(getString(R.string.timer))
                            .setContentText(getString(R.string.timer_ready))
                            .setAutoCancel(true)
                            .setContentIntent(pIntent);

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // mId allows you to update the notification later on.
            mNotificationManager.notify(ID, mBuilder.build());
        }
    }

    public void wakeUpScreen(){
       //TODO: wake up screen without conflicting with existing wakelock
    }

    public void hideNotification(int ID){
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(ID);
    }

    public void onTimerFinished(){
        Log.v(TAG, "timer finished");

        //only play alarm when cooking plate is not visible
        if(!runningOnForeground) {
            //show timer ready notification!
            showNotification(this.timerReadyID);

            //turn on screen (if turned off)
            wakeUpScreen();


            //play sound
            mediaPlayer = MediaPlayer.create(this, R.raw.alarm);
            mediaPlayer.start();
            //SHAKE IT!
            Vibrator vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
            long[] pattern = {0, 500, 500};
            // -1 vibrate once
            // 0 vibrate indefinitely
            vibrator.vibrate(pattern, -1);

            if(!runningOnForeground){
                alarmHandler.postDelayed(alarmRunnable, 4000);
            }

        }
    }

}
