package com.tomdoesburg.kooktijden;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

/**
 * Created by FrankD on 13-9-2014.
 */
public class TimerService extends Service {

    private final static String TAG = "TimerService";
    private HashMap<Integer,VegetableAlarm> vegAlarms = new HashMap<Integer, VegetableAlarm>();
    private final IBinder mBinder = new LocalBinder();
    public static final String TIMER_SERVICE = "com.tomdoesburg.kooktijden.TimerService";
    Intent broadcastIntent = new Intent(TIMER_SERVICE);

    public class LocalBinder extends Binder {
        public TimerService getService() {
            // Return this instance of TimerService so clients can call public methods
            return TimerService.this;
        }
    }

    public final static String TIMER_DONE = "TIMER_DONE"; //timer done.
    public final static String TIMER_TICK = "TIMER_TICK"; //timer done.

    private StateSaver stateSaver;
    private MediaPlayer mediaPlayer;
    public static boolean alarmOn = false;
    public static boolean activityWithoutStovesActive = false; // if stoves are invisible, but app is running on foreground, don't kill service
    public static boolean runningOnForeground = true; //indicates whether or not app is visible to user (true) or working in background (false)
    public static final int notificationID = 1;
    public static int timerReadyID = 2;
    private  NotificationCompat.Builder mBuilder;
    private boolean firstNotification = true; //lets us know if we can re-use (false) a notification or create a new one (true)
    private boolean alarmHandlerCalled = false;
    //used to keep the service running when phone goes to sleep
    private PowerManager powerManager;
    private WakeLock wakeLock;
    private WakeLock screenLock;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "oncreate()");
        //start handler that ticks every second
        timerHandler.postDelayed(timerRunnable, 1000);
        timerHandlerActive = true;
        killServiceHandler.postDelayed(killServiceRunnable,5000);
    }

    Handler timerHandler = new Handler();
    boolean timerHandlerActive = false;
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            if(anyTimerRunning()) {
                tick();
                timerHandlerActive = true;
                timerHandler.postDelayed(this, 1000);
            }else{
                timerHandlerActive = false;
            }
        }
    };

    Handler killServiceHandler = new Handler();
    Runnable killServiceRunnable = new Runnable() {
        @Override
        public void run() {
            if(!runningOnForeground && !activityWithoutStovesActive && !anyTimerRunning() && ! alarmHandlerCalled) {
                killService();
            }else{
                killServiceHandler.postDelayed(killServiceRunnable,5000);
            }
        }
    };

    //handler for repeating alarm when app is in background
    Handler alarmHandler = new Handler();
    Runnable alarmRunnable = new Runnable() {

        @Override
        public void run() {
           alarmHandlerCalled = false;

          if(!runningOnForeground) {
              onTimerFinished();
          }
        }
    };


    //subtracts time from deadlines every second
    public void tick(){
        //Log.v(TAG, "tick()");
        //it's possible to put extra's to broadcast bi
        if(!broadcastIntent.hasExtra(TIMER_TICK)) { //we only need to do this once
            broadcastIntent.putExtra(TIMER_TICK, TIMER_TICK);
        }


        for (HashMap.Entry<Integer, VegetableAlarm> entry : vegAlarms.entrySet()){
            VegetableAlarm curAlarm = entry.getValue();
            if(curAlarm.isRunning()){
                curAlarm.tick();
                if(curAlarm.isFinished()){
                    sendBroadcast(new Intent(TIMER_SERVICE).putExtra(TIMER_DONE,curAlarm.getVegName()));
                    onTimerFinished();
                }
            }
        }

        sendBroadcast(broadcastIntent);

      if(!runningOnForeground && anyTimerRunning()){
            showNotification(this.notificationID);
        }else{
            hideNotification(this.notificationID);
        }
    }

    public void setTimer(int ID, VegetableAlarm alarm){
        vegAlarms.put(ID, alarm);
    }

    public VegetableAlarm getTimer(int ID){
        if(vegAlarms.containsKey(ID)) {
            return vegAlarms.get(ID);
        }else{
            return null;
        }
    }

    public void startTimer(int ID){
        if(vegAlarms.containsKey(ID)) {
            vegAlarms.get(ID).setIsRunning(true);
            if(!timerHandlerActive){
                timerHandler.postDelayed(timerRunnable, 1000);
                timerHandlerActive = true;
            }
        }
    }

    public void stopTimer(int ID){
        if(vegAlarms.containsKey(ID)) {
            vegAlarms.get(ID).setIsRunning(false);
        }
    }

    public void removeTimer(int ID){
        if(vegAlarms.containsKey(ID)) {
            vegAlarms.remove(ID);
        }
    }

    public int getTimeLeft(int ID){
        if(vegAlarms.containsKey(ID)) {
            return vegAlarms.get(ID).getTimeLeft();
        }else{
            return -1;
        }
    }

    public boolean hasAlarm(int ID){
        return vegAlarms.containsKey(ID);
    }

    public boolean isRunning(int ID){
        if(vegAlarms.containsKey(ID)) {
            return vegAlarms.get(ID).isRunning();
        }else{
            return false;
        }
    }

    public void setFinished(int ID,boolean isFinished){
        if(vegAlarms.containsKey(ID)) {
            vegAlarms.get(ID).setIsFinished(isFinished);
        }
    }


    public void addAdditionalTime(int ID,int seconds){
        if(vegAlarms.containsKey(ID)) {
            vegAlarms.get(ID).addAdditionalTime(seconds);
            setFinished(ID, false);
        }
    }

    public boolean anyTimerRunning(){
        for (HashMap.Entry<Integer, VegetableAlarm> entry : vegAlarms.entrySet()){
            if(entry.getValue().isRunning()){
                return true;
            }
        }
        return false;
    }

    public boolean anyTimerSet(){
        if(vegAlarms.size()>0){
            return true;
        }else{
            return false;
        }
    }

    //resets all timers by clearing alarms
    public void resetAllTimers(){
        vegAlarms.clear();
        vegAlarms = new HashMap<Integer, VegetableAlarm>();
    }

    public void killService(){
        //hide notifications from screen
        hideNotification(this.notificationID);

        //remove handler callbacks
        timerHandler.removeCallbacks(timerRunnable);
        timerHandlerActive = false;

        //release wakelock (if any was held)
        if (wakeLock.isHeld()){
            wakeLock.release();
        }
        //kill service
        this.stopSelf();
    }

    public void saveState(){
        //only save state if there is an alarm that isn't finished yet
        StateSaver stateSaver = new StateSaver(this);
        stateSaver.saveStates(vegAlarms);
    }

    public void restoreState(){
        StateSaver stateSaver = new StateSaver(this);
        //only retrieve when there are no alarms
        if(vegAlarms.isEmpty()){ //if vegalarms is currently empty
            //check if there is a state to restore
            this.vegAlarms = stateSaver.retrieveStates();
        }
        stateSaver.clearStates(); //we only retrieve once, then delete history
    }

    @Override
    public void onDestroy() {
        saveState();//save current state
        hideNotification(this.notificationID);
        timerHandler.removeCallbacksAndMessages(null);
        timerHandlerActive = false;

        if (wakeLock.isHeld()){
            wakeLock.release();
        }

        if(mediaPlayer != null){
            mediaPlayer.release();
        }

        Log.d(TAG, "Service comitted suicide Aaaah");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //a lot of try catch blocks. You never know how many alarms we have running and if they are initialized
        restoreState();//try to restore state if there is any state to restore from

        if(wakeLock== null) {
            powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            wakeLock = powerManager.newWakeLock((PowerManager.PARTIAL_WAKE_LOCK), "TimerService");
            wakeLock.setReferenceCounted(false); //only 1 call to acquire is enough to acquire, only 1 release required to release
        }

        if(!wakeLock.isHeld()) {
            wakeLock.acquire();
        }

        return START_NOT_STICKY;
    }



    public void showNotification(int ID){
        // Creates an explicit intent for an Activity in your app
        Intent intent = new Intent(this.getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, intent, 0);

        if(ID == this.notificationID) {
            //notify user that a timer is running
            if(!this.firstNotification){//re-use mBuilder
                mBuilder.setSmallIcon(R.drawable.kooktijden_icon)
                        .setOnlyAlertOnce(true)
                        .setContentTitle(getString(R.string.timer))
                        .setContentText(getString(R.string.notification_text) + " " + getFirstAlarmTime())
                        .setContentIntent(pIntent);
            }else{ //create new instance of mBuilder
                this.firstNotification = false;

                mBuilder =
                        new NotificationCompat.Builder(this)
                                .setOnlyAlertOnce(true)
                                .setSmallIcon(R.drawable.kooktijden_icon)
                                .setContentTitle(getString(R.string.timer))
                                .setContentText(getString(R.string.notification_text) + " " + getFirstAlarmTime())
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

    public String getFirstAlarmTime(){
        int firstAlarm = -1;
        for (HashMap.Entry<Integer, VegetableAlarm> entry : vegAlarms.entrySet()){
            VegetableAlarm curAlarm = entry.getValue();
            if(curAlarm.isRunning()){
                int timeLeft = curAlarm.getTimeLeft();

                if(firstAlarm == -1){
                    firstAlarm = timeLeft;
                }else if(timeLeft < firstAlarm){
                    firstAlarm = timeLeft;
                }
            }
        }

        if(firstAlarm==-1){
            return "00:00";
        }else{
            return formatTime(firstAlarm);
        }
    }

    public void wakeUpScreen(){
       //TODO: wake up screen without conflicting with existing wakelock

        screenLock = ((PowerManager)getSystemService(POWER_SERVICE)).newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        screenLock.acquire();

        //later
        screenLock.release();
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
        }

        //play sound
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm);
        if(!mediaPlayer.isPlaying()){
            mediaPlayer.start();
        }
        //SHAKE IT!
        Vibrator vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 500, 500};
        // -1 vibrate once
        // 0 vibrate indefinitely
        vibrator.vibrate(pattern, -1);

        if(!runningOnForeground){
            alarmHandler.postDelayed(alarmRunnable, 4000);
            alarmHandlerCalled = true;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "Bound Service");
        sendBroadcast(broadcastIntent);
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        sendBroadcast(broadcastIntent);
        Log.d(TAG,"Rebind Service");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(TAG, "in onUnbind");
        return true;
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


}
