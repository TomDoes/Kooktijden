package com.tomdoesburg.kooktijden.vegetables;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import com.tomdoesburg.kooktijden.R;

/**
 * Created by Joost on 7-3-2015.
 */
public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        //get view and set controls to current settings
        View regular = getLayoutInflater().inflate(R.layout.activity_settings,null);

        final NumberPicker numberpicker = (NumberPicker)regular.findViewById(R.id.stoveTopsNumberPicker);
        String[] displayedValues = {"1", "2", "4", "5", "6"};
        numberpicker.setDisplayedValues(displayedValues);
        numberpicker.setValue(sharedPrefs.getInt("numberOfStoveTops",4));

        Button setButton = (Button)regular.findViewById(R.id.setButton);
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //save selected values
                int selected = numberpicker.getValue();
                sharedPrefs.edit().putInt("numberOfStoveTops",selected).commit();

                //go back to previous activity
                finish();
            }
        });


        //show the view to the user
        setContentView(regular);

    }
}
