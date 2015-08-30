package com.tomdoesburg.kooktijden;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.TextView;

import com.tomdoesburg.model.Vegetable;
import com.tomdoesburg.sqlite.MySQLiteHelper;

import java.util.List;

/**
 * Created by FrankD on 23-8-2015.
 */

public class NewRecipeActivity extends Activity{
    private final static String TAG = "NewRecipeActivity";
    public final static String UNKNOWN_VEGETABLE = "UNKNOWN_VEGETABLE";
    public final static String NEW_VEGETABLE = "NEW_VEGETABLE";
    public final static String ID = "ID";
    public final static String NAME = "NAME";
    public final static String DESCRIPTION = "DESCRIPTION";
    public final static String TIMEMINUTES = "TIMEMINUTES";

    private int kookPlaatID = 0;

    private ScrollView scrollView;
    private TextView titleTV;
    private TextView hoursTV;
    private TextView minutesTV;
    private CheckBox checkBox;
    private EditText newVegNameET;
    private EditText decriptionET;
    private NumberPicker numberPickerHours;
    private NumberPicker numberPickerMin;
    private Button storeButton;
    private LinearLayout linearLayoutFocus;
    boolean storeInDB = false;
    private MySQLiteHelper db;
    private List<Vegetable> vegetables;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recipe);

        try {
            Intent intent = getIntent();
            this.kookPlaatID = intent.getIntExtra("kookPlaatID",-1);
        }catch(NullPointerException E){
            //this should not happen ever, but better safe than sorry!
        }

        scrollView = (ScrollView) findViewById(R.id.scrollView);

        linearLayoutFocus = (LinearLayout) findViewById(R.id.linearlayout_focus); //dummy item to prevent soft keyboard from popping up

        titleTV = ((TextView) findViewById(R.id.new_recipe_title));
        titleTV.setVisibility(View.INVISIBLE);
        hoursTV = ((TextView) findViewById(R.id.hoursTV));
        minutesTV = ((TextView) findViewById(R.id.minutesTV));
        newVegNameET = (EditText) findViewById(R.id.newVegNameET);
        decriptionET = ((EditText) findViewById(R.id.descriptionET));

        newVegNameET.setVisibility(View.INVISIBLE);
        decriptionET.setVisibility(View.INVISIBLE);

        Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");
        Typeface typeFaceLight = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");

        titleTV.setTypeface(typeFaceLight);
        hoursTV.setTypeface(typeFaceLight);
        minutesTV.setTypeface(typeFaceLight);
        newVegNameET.setTypeface(typeFace);
        decriptionET.setTypeface(typeFace);

        numberPickerMin = (NumberPicker) findViewById(R.id.numberPickerMinutes);
        numberPickerMin.setMinValue(0);
        numberPickerMin.setMaxValue(59);
        numberPickerMin.setValue(0);
        numberPickerMin.setWrapSelectorWheel(false);

        numberPickerHours = (NumberPicker) findViewById(R.id.numberPickerHours);
        numberPickerHours.setMinValue(0);
        numberPickerHours.setMaxValue(23);
        numberPickerHours.setValue(0);
        numberPickerHours.setWrapSelectorWheel(false);

        checkBox = (CheckBox) findViewById(R.id.checkBox);
        checkBox.setTypeface(typeFaceLight);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                storeInDB = isChecked;
                newVegNameET = (EditText) findViewById(R.id.newVegNameET);
                decriptionET = ((EditText) findViewById(R.id.descriptionET));

                if (isChecked) {
                    newVegNameET.setVisibility(View.VISIBLE);
                    decriptionET.setVisibility(View.VISIBLE);
                    scrollDown();
                } else {
                    newVegNameET.setVisibility(View.INVISIBLE);
                    decriptionET.setVisibility(View.INVISIBLE);
                    scrollUp();
                }
            }
        });

        storeButton = (Button) findViewById(R.id.storeButton);
        storeButton.setTypeface(typeFaceLight);
        storeButton.setVisibility(View.INVISIBLE);
        GradientDrawable circleShape = (GradientDrawable) storeButton.getBackground();
        circleShape.setColor(getResources().getColor(R.color.listcolor1));
        storeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeButtonClicked();
            }
        });

    }

    private void storeButtonClicked(){
        clearFocus();
        int hours = numberPickerHours.getValue();
        int minutes = numberPickerMin.getValue();
        Log.v(TAG, "hours " + hours + " minutes " + minutes);
        String name = newVegNameET.getText().toString();
        String description = decriptionET.getText().toString();

        name = formatName(name);
        description = description.replace("  "," ");

        int timeMinutes = hours*60 + minutes;
        Log.v(TAG, "timeMinutes " + timeMinutes);

        if(timeMinutes == 0){
            KooktijdenDialog dialog = new KooktijdenDialog(this,getResources().getString(R.string.dialog_time_not_set_title),getResources().getString(R.string.dialog_time_not_set));
            dialog.show();
        }else if(storeInDB && (name.equals("") || name.equals(" "))) {
            KooktijdenDialog dialog = new KooktijdenDialog(this, getResources().getString(R.string.dialog_fill_in_name_title), getResources().getString(R.string.dialog_fill_in_name));
            dialog.show();
        }else if(storeInDB && nameExists(name)){
            KooktijdenDialog dialog = new KooktijdenDialog(this, getResources().getString(R.string.dialog_name_in_use_title), getResources().getString(R.string.dialog_name_in_use));
            dialog.show();
        }else{ //we have a time and a name
            setTimer(timeMinutes,name, description);
        }
    }

    private void setTimer(int timeMinutes,String name, String description){
        Log.v(TAG, "timeMinutes " + timeMinutes);

        Intent intent = new Intent(this.getApplicationContext(),MainActivity.class);
        intent.putExtra("kookPlaatID",kookPlaatID);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        if(storeInDB){
            Vegetable veg = new Vegetable();
            veg.setCookingTimeMin(timeMinutes);
            veg.setCookingTimeMax(timeMinutes);
            veg.setNameEN(name);
            veg.setNameNL(name);
            veg.setDescriptionEN(description);
            veg.setDescriptionNL(description);

            db.addVegetable(veg);
            intent.putExtra(NEW_VEGETABLE, NEW_VEGETABLE);
            intent.putExtra(TIMEMINUTES,timeMinutes);
            intent.putExtra(NAME,name);
            intent.putExtra(DESCRIPTION,description);
            int id = getVegIDFromDB(veg);
            intent.putExtra(ID,id);

            startActivity(intent);
            finish();
        }else{
            intent.putExtra(UNKNOWN_VEGETABLE,UNKNOWN_VEGETABLE);
            intent.putExtra(TIMEMINUTES,timeMinutes);
            intent.putExtra(ID,-1);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        TimerService.activityWithoutStovesActive = true;
        //do not give the editbox focus automatically when activity starts
        clearFocus();
        storeButton = (Button) findViewById(R.id.storeButton);
        titleTV = ((TextView) findViewById(R.id.new_recipe_title));

        //final Animation btnAnimation = AnimationUtils.loadAnimation(this.getActivity(), R.anim.slide_right2left_slow);
        final Animation btnAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_right2left);
        Animation titleAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_down);

        //setTimerBtn.startAnimation(btnAnimation);
        titleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                storeButton.setVisibility(View.VISIBLE);
                storeButton.startAnimation(btnAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        titleTV.setVisibility(View.VISIBLE);
        titleTV.startAnimation(titleAnimation);

        db = new MySQLiteHelper(this);
        vegetables = db.getAllVegetables();
    }

    private String formatName(String name){
        if(name!=null && name.length()>1) {
            String formattedName = name.toLowerCase(); //to lowercase
            formattedName = formattedName.replace("  ", " "); //remove double spaces
            formattedName = formattedName.substring(0, 1).toUpperCase() + formattedName.substring(1); //capitalize first letter
            return formattedName;
        }else {
            return name;
        }
    }

    @Override
    public void onPause(){
        db.close();
        TimerService.activityWithoutStovesActive = false;
        super.onPause();

    }

    private void scrollDown(){
        if(scrollView!=null){
            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            clearFocus();
        }
    }

    private void scrollUp(){
        if(scrollView!=null){
            scrollView.fullScroll(ScrollView.FOCUS_UP);
            clearFocus();
        }
    }

    private boolean nameExists(String name){ //returns true if vegetable name already exists, false otherwise
        for(Vegetable curVeg : vegetables){
            if(curVeg.getNameEN().toLowerCase().equals(name.toLowerCase())||curVeg.getNameNL().toLowerCase().equals(name.toLowerCase())){
                return true;
            }
        }
        return false;
    }

    private int getVegIDFromDB(Vegetable vegetable){
        vegetables = db.getAllVegetables(); //refresh list as we may have added a new item to the db
        for(Vegetable curVeg : vegetables){ //iterate list
            if(vegetable.getNameNL().equals(curVeg.getNameNL()) || vegetable.getNameEN().equals(curVeg.getNameEN())){ //if we find a match based on the name...
                return curVeg.getId();
            }
        }

        return -1;
    }


    private void clearFocus(){
        newVegNameET.clearFocus();
        decriptionET.clearFocus();
        numberPickerHours.clearFocus();
        numberPickerMin.clearFocus();
        linearLayoutFocus.requestFocus();
    }

}
