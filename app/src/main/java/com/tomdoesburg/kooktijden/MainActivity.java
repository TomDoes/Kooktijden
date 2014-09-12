package com.tomdoesburg.kooktijden;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import com.tomdoesburg.kooktijden.vegetables.VegetableActivity;
import com.tomdoesburg.model.Vegetable;
import com.tomdoesburg.sqlite.MySQLiteHelper;
import com.viewpagerindicator.CirclePageIndicator;

public class MainActivity extends FragmentActivity {

    SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set default preference
        //PreferenceManager.setDefaultValues(this, R.xml.preferences, false);



        setContentView(R.layout.swipert);

        //Set the pager with an adapter
        ViewPager pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager()));

        //Bind the title indicator to the adapter
        CirclePageIndicator indicator = (CirclePageIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(pager);

        //clicking the lock icon toggles the ability to swipe between fragments
        ImageButton lockButton = (ImageButton) findViewById(R.id.lockButton);
        lockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageButton lockButton = (ImageButton) findViewById(R.id.lockButton);
                CirclePageIndicator indicator = (CirclePageIndicator)findViewById(R.id.indicator);
                if(MyViewPager.swipingEnabled){
                    MyViewPager.swipingEnabled = false;
                    lockButton.setImageResource(R.drawable.lock_locked);
                    Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_out);
                    indicator.startAnimation(anim);
                } else {
                    MyViewPager.swipingEnabled = true;
                    lockButton.setImageResource(R.drawable.brocolli);
                }
            }
        });




        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        MySQLiteHelper db = new MySQLiteHelper(this);

        Log.d("database", "Created the database connection");
        Log.d("database", "First start: " + (sharedPrefs.getBoolean("databaseLoaded",false)));

        // Add vegetables only on first launch
        if(!sharedPrefs.getBoolean("databaseLoaded",false)) {
            //TODO work with db versions

            Log.d("database", "adding stuff to the db");
            db.addVegetable(new Vegetable("Artichoke", "Artisjok", 25, 25, "Artichokes look more like a Flower than a vegetable. To prepare an artichoke you have to cut of the stem and remove the tougher leaves until only the soft remain.", ""));
            db.addVegetable(new Vegetable("Asparagus (whole)", "Asperges (heel)", 15, 15, "", ""));
            db.addVegetable(new Vegetable("Beans", "Sperziebonen", 8, 8, "", ""));
            db.addVegetable(new Vegetable("Beetroot", "Bieten", 40, 60, "", ""));
            db.addVegetable(new Vegetable("Bok choy", "Paksoi / Chinese kool", 10, 10, "", ""));
            db.addVegetable(new Vegetable("Broccoli", "Broccoli", 8, 8, "", ""));
            db.addVegetable(new Vegetable("Brussels sprouts", "Spruitjes", 10, 10, "", ""));
            db.addVegetable(new Vegetable("Cabbage", "Witte kool", 10, 15, "", ""));
            db.addVegetable(new Vegetable("Carrots", "Wortels", 10, 10, "", ""));
            db.addVegetable(new Vegetable("Cauliflower", "Bloemkool", 12, 12, "", ""));
            db.addVegetable(new Vegetable("Corn on the cob", "Maiskolf", 20, 20, "", ""));
            db.addVegetable(new Vegetable("Endive", "Witlof", 12, 15, "", ""));
            db.addVegetable(new Vegetable("Leek", "Prei", 15, 15, "", ""));
            db.addVegetable(new Vegetable("Peas", "Erwten", 5, 5, "", ""));
            db.addVegetable(new Vegetable("Potatoes (halves)", "Aardappelen (halven)", 10, 15, "", ""));
            db.addVegetable(new Vegetable("Potatoes (whole)", "Aardappelen (heel)", 15, 20, "", ""));
            db.addVegetable(new Vegetable("Red cabbage (shredded)", "Rodekool (gesneden)", 15, 20, "", ""));
            db.addVegetable(new Vegetable("Spinach", "Spinazie", 4, 5, "", ""));
            db.addVegetable(new Vegetable("Sweet potatoes (whole)", "Zoete aardappelen (heel)", 20, 20, "", ""));

            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putBoolean("databaseLoaded", true);
            editor.commit();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}