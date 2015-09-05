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

    public VegetableAlarm(Vegetable veg){
        this.vegetable = veg;
        this.vegID = veg.getId();
        this.vegName = getVegetableName(veg);
        this.cookingTime = veg.getCookingTimeMin();
        this.timeLeft = cookingTime*60;
    }

    public VegetableAlarm(String state){ //state = vegid;vegName;cookingtimeMin;timeleft;isRunning;isFinished
        String[] parts = state.split(";");
        this.vegID = Integer.parseInt(parts[0]);
        this.vegName = parts[1];
        this.cookingTime = Integer.parseInt(parts[2]);
        this.timeLeft = Integer.parseInt(parts[3]);
        this.isRunning = Boolean.valueOf(parts[4]);
        this.isFinished = Boolean.valueOf(parts[5]);

        Vegetable veg = new Vegetable();
        veg.setNameNL(vegName);
        veg.setNameEN(vegName);
        veg.setId(vegID);
        veg.setCookingTimeMin(cookingTime);
        this.vegetable = veg;
    }

    public String getState(){ //state = vegid;vegName;cookingtimeMin;timeleft;isRunning;isFinished
        String state = vegetable.getId()+ ";" + getVegetableName(vegetable) + ";" +vegetable.getCookingTimeMin()+ ";" + this.timeLeft + ";" + this.isRunning() + ";" + this.isFinished();
        return state;
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

    public void addAdditionalTime(int time){
        timeLeft = timeLeft + time;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
    }
}
