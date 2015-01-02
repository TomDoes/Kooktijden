package com.tomdoesburg.kooktijden;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;


/**
 * Created by FrankD on 23-12-2014.
 */
public class StateSaver {
//this class is used to store system states to avoid using the service (for example: battery drainage due to paused timers)
    private final String TAG = "StateSaver";
    private Context context;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public StateSaver(Context context){
        this.context = context;
    }


    public void saveStates() {
        pref = context.getSharedPreferences("StateSaver",0);
        editor = pref.edit();

        //save state of kookplaat 1
        String kookplaat1State = TimerService.vegID1 + ";" + TimerService.deadline1 + ";" + TimerService.deadline1Add + ";" + TimerService.timer1Running + ";" + TimerService.timer1Finished + ";" + TimerService.vegName1;
        editor.putString("kookplaat1State",kookplaat1State);

        //save state of kookplaat 2
        String kookplaat2State = TimerService.vegID2 + ";" + TimerService.deadline2 + ";" + TimerService.deadline2Add + ";" + TimerService.timer2Running + ";" + TimerService.timer2Finished + ";" + TimerService.vegName2;
        editor.putString("kookplaat2State",kookplaat2State);

        //save state of kookplaat 3
        String kookplaat3State = TimerService.vegID3 + ";" + TimerService.deadline3 + ";" + TimerService.deadline3Add + ";" + TimerService.timer3Running + ";" + TimerService.timer3Finished + ";" + TimerService.vegName3;
        editor.putString("kookplaat3State",kookplaat3State);

        //save state of kookplaat 4
        String kookplaat4State = TimerService.vegID4 + ";" + TimerService.deadline4 + ";" + TimerService.deadline4Add + ";" + TimerService.timer4Running + ";" + TimerService.timer4Finished + ";" + TimerService.vegName4;
        editor.putString("kookplaat4State",kookplaat4State);

        //save state of kookplaat 5
        String kookplaat5State = TimerService.vegID5 + ";" + TimerService.deadline5 + ";" + TimerService.deadline5Add + ";" + TimerService.timer5Running + ";" + TimerService.timer5Finished + ";" + TimerService.vegName5;
        editor.putString("kookplaat5State",kookplaat5State);

        //save state of kookplaat 6
        String kookplaat6State = TimerService.vegID6 + ";" + TimerService.deadline6 + ";" + TimerService.deadline6Add + ";" + TimerService.timer6Running + ";" + TimerService.timer6Finished + ";" + TimerService.vegName6;
        editor.putString("kookplaat6State",kookplaat6State);

        editor.commit();

        /*
        Log.d(TAG,"StateSaver saveStates()");
        Log.d(TAG,"Saved state 1:"  + kookplaat1State);
        Log.d(TAG,"Saved state 2:"  + kookplaat2State);
        Log.d(TAG,"Saved state 3:"  + kookplaat3State);
        Log.d(TAG,"Saved state 4:"  + kookplaat4State);
        Log.d(TAG,"Saved state 5:"  + kookplaat5State);
        Log.d(TAG,"Saved state 6:"  + kookplaat6State);
        */

    }

        public void retrieveStates(){
            Log.d(TAG,"StateSaver retrieveStates()");
            try {
                //retrieve state of kookplaat 1
                pref = context.getSharedPreferences("StateSaver",0);
                String state = pref.getString("kookplaat1State",null);
                String[] states = state.split(";");

               // Log.d(TAG,"retrieved state 1:" + state);

                TimerService.vegID1 = Integer.parseInt(states[0]);
                TimerService.deadline1 = Integer.parseInt(states[1]);
                TimerService.deadline1Add = Integer.parseInt(states[3]);

                TimerService.timer1Running = Boolean.parseBoolean(states[4]);
                TimerService.timer1Finished = Boolean.parseBoolean(states[5]);
                TimerService.vegName1 = states[6];

            }catch(Exception E){
               // Log.d(TAG,E.toString());
            }

            //retrieve state of kookplaat 2
            try{
                pref = context.getSharedPreferences("StateSaver",0);
                String state = pref.getString("kookplaat2State",null);
                String[] states = state.split(";");

            //    Log.d(TAG,"retrieved state 2:" + state);

                TimerService.vegID2 = Integer.parseInt(states[0]);
                TimerService.deadline2 = Integer.parseInt(states[1]);
                TimerService.deadline2Add = Integer.parseInt(states[3]);

                TimerService.timer2Running = Boolean.parseBoolean(states[4]);
                TimerService.timer2Finished = Boolean.parseBoolean(states[5]);
                TimerService.vegName2 = states[6];

            }catch(Exception E){
               // Log.d(TAG,E.toString());
            }

            //retrieve state of kookplaat 3
            try{
                pref = context.getSharedPreferences("StateSaver",0);
                String state = pref.getString("kookplaat3State",null);
                String[] states = state.split(";");

            //    Log.d(TAG,"retrieved state 3:" + state);

                TimerService.vegID3 = Integer.parseInt(states[0]);
                TimerService.deadline3 = Integer.parseInt(states[1]);
                TimerService.deadline3Add = Integer.parseInt(states[3]);

                TimerService.timer3Running = Boolean.parseBoolean(states[4]);
                TimerService.timer3Finished = Boolean.parseBoolean(states[5]);
                TimerService.vegName3 = states[6];
            }catch(Exception E){
              //  Log.d(TAG,E.toString());
            }


            //retrieve state of kookplaat 4
            try{
                pref = context.getSharedPreferences("StateSaver",0);
                String state = pref.getString("kookplaat4State",null);
                String[] states = state.split(";");

            //    Log.d(TAG,"retrieved state 4:" + state);

                TimerService.vegID4 = Integer.parseInt(states[0]);
                TimerService.deadline4 = Integer.parseInt(states[1]);
                TimerService.deadline4Add = Integer.parseInt(states[3]);

                TimerService.timer4Running = Boolean.parseBoolean(states[4]);
                TimerService.timer4Finished = Boolean.parseBoolean(states[5]);
                TimerService.vegName4 = states[6];

            }catch(Exception E){
               // Log.d(TAG,E.toString());
            }

            try{
                pref = context.getSharedPreferences("StateSaver",0);
                String state = pref.getString("kookplaat5State",null);
                String[] states = state.split(";");

            //    Log.d(TAG,"retrieved state 5:" + state);

                TimerService.vegID5 = Integer.parseInt(states[0]);
                TimerService.deadline5 = Integer.parseInt(states[1]);
                TimerService.deadline5Add = Integer.parseInt(states[3]);

                TimerService.timer5Running = Boolean.parseBoolean(states[4]);
                TimerService.timer5Finished = Boolean.parseBoolean(states[5]);
                TimerService.vegName5 = states[6];
            }catch(Exception E){
              //  Log.d(TAG,E.toString());
            }

            //retrieve state of kookplaat 6
            try{
                pref = context.getSharedPreferences("StateSaver",0);
                String state = pref.getString("kookplaat6State",null);
                String[] states = state.split(";");

             //   Log.d(TAG,"retrieved state 6:" + state);

                TimerService.vegID6 = Integer.parseInt(states[0]);
                TimerService.deadline6 = Integer.parseInt(states[1]);
                TimerService.deadline6Add = Integer.parseInt(states[3]);

                TimerService.timer6Running = Boolean.parseBoolean(states[4]);
                TimerService.timer6Finished = Boolean.parseBoolean(states[5]);
                TimerService.vegName6 = states[6];
            }catch(Exception E){
             //   Log.d(TAG,E.toString());
            }

        }



}
