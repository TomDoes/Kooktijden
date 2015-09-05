package com.tomdoesburg.kooktijden;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;

import com.tomdoesburg.kooktijden.R;

/**
 * Created by Joost on 7-3-2015.
 * Edited by Frank on 19-08-2015
 */
public class SettingsActivity extends Activity implements KooktijdenDialogTwoButtons.ActivityCommunicator{

    private final String TAG = "SettingsActivity";
    final String[] displayedValues = {"1", "2", "4", "5", "6"};
    private NumberPicker numberpicker;

    private SharedPreferences sharedPrefs;
    //
    TimerService mService;
    private boolean mServiceBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        //get view and set controls to current settings
        //View regular = getLayoutInflater().inflate(R.layout.activity_settings,null);
        //show the view to the user
        setContentView(R.layout.activity_settings);

        numberpicker = (NumberPicker) findViewById(R.id.stoveTopsNumberPicker);

        numberpicker.setMinValue(0);
        numberpicker.setMaxValue(4);
        numberpicker.setDisplayedValues(displayedValues);

        int curSetting = sharedPrefs.getInt("numberOfStoveTops", 4);
        int curSettingIndex = 0;

        for(int i = 0; i < displayedValues.length;i++){
            if(displayedValues[i].equals(String.valueOf(curSetting))){
                curSettingIndex = i;
            }
        }
        numberpicker.setValue(curSettingIndex);

        Button setButton = (Button) findViewById(R.id.setButton);
        GradientDrawable circleShape = (GradientDrawable)setButton.getBackground();
        circleShape.setColor(getResources().getColor(R.color.pink));

        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openResetDialog(); //the dialog will set the value for switchStove
            }
        });
    }

    private boolean anyTimerSet(){
        if(mService!=null){
            mService.anyTimerSet();
        }
        return false;
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

    @Override
    public void onResume() {
        Intent intent = new Intent(this.getApplicationContext(), TimerService.class);
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        mServiceBound = true;
        TimerService.activityWithoutStovesActive = true;
        super.onResume();
    }

    @Override
    public void onPause() {
        if (mServiceBound) {
            unbindService(mServiceConnection);
            mServiceBound = false;
        }
        TimerService.activityWithoutStovesActive = false;
        super.onPause();
    }

    public void openResetDialog(){
        KooktijdenDialogTwoButtons dialog = new KooktijdenDialogTwoButtons(this,getString(R.string.dialog_reset_switch_stove_title),getString(R.string.dialog_reset_switch_stove_message));
        dialog.show();
    }

    @Override
    public void resetDialogYesClicked() {
        if(mService!=null){
             mService.resetAllTimers();

            //save selected values
            int pos = numberpicker.getValue();
            int selectedValue = Integer.parseInt(displayedValues[pos]);
            sharedPrefs.edit().putInt("numberOfStoveTops", selectedValue).commit();

            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // call this to finish the current activity
        }
    }

    @Override
    public void resetDialogCancelClicked(){
    }

}
