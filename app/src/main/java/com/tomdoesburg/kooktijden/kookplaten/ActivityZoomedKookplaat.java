package com.tomdoesburg.kooktijden.kookplaten;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tomdoesburg.kooktijden.R;
import com.tomdoesburg.model.Vegetable;
import com.tomdoesburg.sqlite.MySQLiteHelper;

import java.util.Locale;

/**
 * Created by Joost on 6-10-2014.
 */
public class ActivityZoomedKookplaat extends Activity{

    String kookPlaatID;
    int vegID;
    ImageButton pause;
    ImageButton stop;
    Button plus;
    TextView vegetableName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.kookplaat_zoom,null);

        //get the kookplaat that was selected
        Intent intent = getIntent();
        kookPlaatID = intent.getStringExtra("kookPlaatID");
        vegID = intent.getIntExtra("vegetableID",0);

        //get vegetable from database
        MySQLiteHelper db = new MySQLiteHelper(this);
        Vegetable vegetable = db.getVegetable(vegID);

        //init buttons etc.
        pause = (ImageButton) view.findViewById(R.id.buttonTimerPause);
        stop = (ImageButton) view.findViewById(R.id.buttonTimerStop);
        plus = (Button) view.findViewById(R.id.buttonTimerPlus);
        vegetableName = (TextView) view.findViewById(R.id.vegetableName);

        //set the vegetable name according to locale
        String language = Locale.getDefault().getDisplayLanguage();
        if(language.equals("Nederlands")) {
            vegetableName.setText(vegetable.getNameNL());
        } else {
            vegetableName.setText(vegetable.getNameEN());
        }

        //present view to user
        setContentView(view);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //make all the buttons work

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pause the running timer
                //TODO pause the running timer

                //set icon according to state
                //pause.setImageResource(R.drawable.icon_play);
                //pause.setImageResource(R.drawable.icon_pause);
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //stop the timer on this stove
                //TODO stop the timer on this stove

            }
        });

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //increase remaining time of this stove with 30 seconds
                //TODO increase remaining time of this stove with 30 seconds
            }
        });

    }
}
