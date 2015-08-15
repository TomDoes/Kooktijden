package com.tomdoesburg.kooktijden;

import android.util.Log;

import com.tomdoesburg.model.Vegetable;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by FrankD on 14-8-2015.
 * this class is used to store the details of a single alarm
 */

public class VegetableAlarm {

    private static final String TAG = "VegetableAlarm";
    private Vegetable vegetable;
    private String vegName;
    private int vegID;
    private boolean isRunning = false;
    private boolean isFinished = false;

    private int cookingTime = 0; //cooking time
    private int timeLeft = 0;
    private int additionalTime = 0;

    public VegetableAlarm(Vegetable veg){
        this.vegetable = veg;
        this.vegID = veg.getId();
        this.vegName = getVegetableName(veg);
        this.cookingTime = veg.getCookingTimeMin();
        this.timeLeft = cookingTime*60;
    }

    private String getVegetableName(Vegetable veg){
        String language = Locale.getDefault().getDisplayLanguage();

        if(language.equals("Nederlands")){
            return veg.getNameNL();
        }else{
            return veg.getNameEN();
        }

    }

    public void tick(){
        if(isRunning){
            timeLeft--;

            if(timeLeft == 0){
                isRunning = false;
                setIsFinished(true);
            }
        }
    }

    /*
    *
    * GETTERS AND SETTERS
    *
    * */


    public Vegetable getVegetable() {
        return vegetable;
    }

    public void setVegetable(Vegetable vegetable) {
        this.vegetable = vegetable;
    }

    public String getVegName() {
        return vegName;
    }

    public void setVegName(String vegName) {
        this.vegName = vegName;
    }

    public int getVegID() {
        return vegID;
    }

    public void setVegID(int vegID) {
        this.vegID = vegID;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setIsRunning(boolean isRunning) {
        if(isRunning && this.timeLeft > 0) {
            this.isRunning = true;
        }else{
            this.isRunning = false;
        }
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setIsFinished(boolean isFinished) {
        this.isFinished = isFinished;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public void setCookingTime(int cookingTime) {
        this.cookingTime = cookingTime;
    }

    public int getAdditionalTime() {
        return additionalTime;
    }

    public void addAdditionalTime(int time){
        timeLeft = timeLeft + time;
    }

    public void setAdditionalTime(int additionalTime) {
        this.additionalTime = additionalTime;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
    }
}
