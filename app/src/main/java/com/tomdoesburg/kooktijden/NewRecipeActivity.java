package com.tomdoesburg.kooktijden;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

/**
 * Created by FrankD on 23-8-2015.
 */

public class NewRecipeActivity extends Activity {
    private final static String TAG = "NewRecipeActivity";
    private int kookPlaatID = 0;

    TextView titleTV;
    TextView minutesTV;
    EditText newVegNameET;
    EditText decriptionET;
    NumberPicker numberPicker;
    ImageButton storeButton;
    LinearLayout linearLayoutFocus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recipe);

        linearLayoutFocus = (LinearLayout) findViewById(R.id.linearlayout_focus); //dummy item to prevent soft keyboard from popping up

        titleTV = ((TextView) findViewById(R.id.new_recipe_title));
        titleTV.setVisibility(View.INVISIBLE);
        minutesTV = ((TextView) findViewById(R.id.minutesTitleTV));
        newVegNameET = (EditText) findViewById(R.id.newVegNameET);
        decriptionET = ((EditText) findViewById(R.id.descriptionET));

        Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");
        Typeface typeFaceLight = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");

        titleTV.setTypeface(typeFaceLight);
        minutesTV.setTypeface(typeFace);
        decriptionET.setTypeface(typeFace);

        numberPicker = (NumberPicker) findViewById(R.id.numberPicker);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(1000);
        numberPicker.setValue(1);
        numberPicker.setWrapSelectorWheel(false);

        storeButton = (ImageButton) findViewById(R.id.storeButton);
        storeButton.setVisibility(View.INVISIBLE);
        GradientDrawable circleShape = (GradientDrawable) storeButton.getBackground();
        circleShape.setColor(getResources().getColor(R.color.listcolor1));

    }

    @Override
    public void onResume() {
        super.onResume();

        //do not give the editbox focus automatically when activity starts
        newVegNameET.clearFocus();
        decriptionET.clearFocus();
        numberPicker.clearFocus();
        linearLayoutFocus.requestFocus();

        storeButton = (ImageButton) findViewById(R.id.storeButton);
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


}
