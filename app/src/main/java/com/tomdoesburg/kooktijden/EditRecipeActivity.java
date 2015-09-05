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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.TextView;
import com.tomdoesburg.model.Vegetable;
import com.tomdoesburg.sqlite.MySQLiteHelper;
import java.util.List;
import java.util.Locale;

/**
 * Created by FrankD on 5-9-2015.
 */


public class EditRecipeActivity extends Activity implements KooktijdenDialogTwoButtons.ActivityCommunicator{
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
    private EditText decriptionET;
    private NumberPicker numberPickerHours;
    private NumberPicker numberPickerMin;
    private Button storeButton;
    private LinearLayout linearLayoutFocus;
    private MySQLiteHelper db;
    private List<Vegetable> vegetables;

    private String language;
    private int vegID;
    private String vegName;
    private Vegetable vegetable;

    private int timeHours = 0;
    private int timeMinutes = 0;
    private int cookingTimeMinutes = 0;
    private String description = "";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recipe);
        this.language = Locale.getDefault().getDisplayLanguage();
        db = new MySQLiteHelper(this); //initialize db

        try {
            Intent intent = getIntent();
            this.kookPlaatID = intent.getIntExtra("kookPlaatID",-1); //get kookplaat number
            this.vegID = intent.getIntExtra("vegID", -1); //get vegetable number to overwrite
            this.vegetable = db.getVegetable(vegID);

            if(language.equals("Nederlands")){
                this.vegName = vegetable.getNameNL();
                this.description = vegetable.getDescriptionNL();
            }else{
                this.vegName = vegetable.getNameEN();
                this.description = vegetable.getDescriptionEN();
            }

        }catch(NullPointerException E){
            //this should not happen ever, but better safe than sorry!
            super.onBackPressed();
        }

        int cookingTimeMin = vegetable.getCookingTimeMin(); //get current cooking time
        if(cookingTimeMin >= 60) {
            timeHours = cookingTimeMin / 60;
            timeMinutes = cookingTimeMin - timeHours*60;
        }else{
            timeMinutes = cookingTimeMin;
        }

        scrollView = (ScrollView) findViewById(R.id.scrollView);

        linearLayoutFocus = (LinearLayout) findViewById(R.id.linearlayout_focus); //dummy item to prevent soft keyboard from popping up

        titleTV = ((TextView) findViewById(R.id.new_recipe_title));
        titleTV.setText(this.vegName);
        titleTV.setVisibility(View.INVISIBLE);
        hoursTV = ((TextView) findViewById(R.id.hoursTV));
        minutesTV = ((TextView) findViewById(R.id.minutesTV));
        decriptionET = ((EditText) findViewById(R.id.descriptionET));
        decriptionET.setText(this.description);

        Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");
        Typeface typeFaceLight = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");

        titleTV.setTypeface(typeFaceLight);
        hoursTV.setTypeface(typeFaceLight);
        minutesTV.setTypeface(typeFaceLight);
        decriptionET.setTypeface(typeFace);

        numberPickerMin = (NumberPicker) findViewById(R.id.numberPickerMinutes);
        numberPickerMin.setMinValue(0);
        numberPickerMin.setMaxValue(59);
        numberPickerMin.setValue(timeMinutes);
        numberPickerMin.setWrapSelectorWheel(false);

        numberPickerHours = (NumberPicker) findViewById(R.id.numberPickerHours);
        numberPickerHours.setMinValue(0);
        numberPickerHours.setMaxValue(23);
        numberPickerHours.setValue(timeHours);
        numberPickerHours.setWrapSelectorWheel(false);

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
        this.timeHours = numberPickerHours.getValue();
        this.timeMinutes = numberPickerMin.getValue();
        Log.v(TAG, "hours " + timeHours + " minutes " + timeMinutes);
        description = decriptionET.getText().toString();

        description = description.replace("  "," ");//replace double spaces by singles

        cookingTimeMinutes = timeHours*60 + timeMinutes;
        Log.v(TAG, "timeMinutes " + timeMinutes);

        if(cookingTimeMinutes == 0){
            KooktijdenDialog dialog = new KooktijdenDialog(this,getResources().getString(R.string.dialog_time_not_set_title),getResources().getString(R.string.dialog_time_not_set));
            dialog.show();
        }else { //show overwrite warning
            KooktijdenDialogTwoButtons dialog = new KooktijdenDialogTwoButtons(this, getResources().getString(R.string.dialog_overwrite_title), getResources().getString(R.string.dialog_overwrite));
            dialog.show();
        }
    }

    private void setTimer(int timeMinutes, String description){
        Log.v(TAG, "timeMinutes " + timeMinutes);

        vegetable.setCookingTimeMin(timeMinutes);
        vegetable.setCookingTimeMax(timeMinutes);
        vegetable.setDescriptionNL(description);
        vegetable.setDescriptionEN(description);

        db.updateVegetable(vegetable);
        onBackPressed();
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
    }

    @Override
    public void onPause(){
        db.close();
        TimerService.activityWithoutStovesActive = false;
        super.onPause();

    }

    private void clearFocus(){
        decriptionET.clearFocus();
        numberPickerHours.clearFocus();
        numberPickerMin.clearFocus();
        linearLayoutFocus.requestFocus();
    }

    @Override
    public void resetDialogYesClicked() {
        setTimer(cookingTimeMinutes,description);
    }

    @Override
    public void resetDialogCancelClicked() {

    }
}
