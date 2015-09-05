package com.tomdoesburg.kooktijden;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


/**
 * Created by FrankD on 23-12-2014.
 */
public class StateSaver {
    //this class is used to store system states to avoid using the service (for example: battery drainage due to paused timers)
    private final String TAG = "StateSaver";
    private final String KEYSETSTRING = "KEYSETSTRING";
    private final String STATEPREFIX = "kookplaatState";
    private Context context;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public StateSaver(Context context) {
        this.context = context; //only use ActivityContext
    }

    //we only save timers that were set, but not finished
    public void saveStates(HashMap<Integer, VegetableAlarm> vegAlarms) {
        pref = context.getSharedPreferences("StateSaver", 0);
        editor = pref.edit();

        Set<Integer> keySet = vegAlarms.keySet();

        Set<String> keySetString = new HashSet<String>(); //convert int keyset to String so we can store them
        for(int key:keySet){
            keySetString.add(String.valueOf(key));
        }

        editor.putStringSet(KEYSETSTRING,keySetString); //save indices

        for (int key : keySet) {
            VegetableAlarm curAlarm = vegAlarms.get(key);
            editor.putString(STATEPREFIX + String.valueOf(key), curAlarm.getState()); //save states
        }
        editor.commit();

    }

    public void clearStates() {
        pref = context.getSharedPreferences("StateSaver", 0);
        editor = pref.edit();
        Set<String> keySetString =  pref.getStringSet(KEYSETSTRING,null);
        try {
            if(keySetString!=null){
                for(String keyString:keySetString) {
                    editor.remove(STATEPREFIX + keyString);//remove states
                }
            }
        }catch (Exception E){
            Log.v(TAG, E.toString());
        }

        editor.remove("keySetString"); //remove keyset
        editor.commit();
    }

    public HashMap<Integer, VegetableAlarm> retrieveStates() {
        pref = context.getSharedPreferences("StateSaver",0);

        HashMap<Integer, VegetableAlarm> vegAlarms = new HashMap<Integer, VegetableAlarm>();
        Set<String> keySetString =  pref.getStringSet(KEYSETSTRING,null);
        try {
            if(keySetString!=null){
                for(String keyString:keySetString){
                    String state = pref.getString(STATEPREFIX+keyString,null);
                    if(state!=null){
                        VegetableAlarm alarm = new VegetableAlarm(state);
                        int key = Integer.parseInt(keyString);
                        vegAlarms.put(key,alarm);
                    }
                }
            }
        }catch (Exception E) {
            Log.v(TAG, E.toString());
        }
        return vegAlarms;
    }


}
