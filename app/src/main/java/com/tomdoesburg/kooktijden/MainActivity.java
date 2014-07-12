package com.tomdoesburg.kooktijden;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.tomdoesburg.model.Vegetable;
import com.tomdoesburg.sqlite.MySQLiteHelper;

import java.util.List;

public class MainActivity extends FragmentActivity {

    SharedPreferences sharedPrefs;


    // Hoi dit is een testje
    // When requested, this adapter returns a DemoObjectFragment,
    // representing an object in the collection.
    MyFragmentPagerAdapter mFragmentPagerAdapter;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set default preferences
        //PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        setContentView(R.layout.swipert);

        // ViewPager and its adapters use support library
        // fragments, so use getSupportFragmentManager.
        mFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mFragmentPagerAdapter);


        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        MySQLiteHelper db = new MySQLiteHelper(this);

        Log.d("database", "Created the database connection");
        Log.d("database", "First start: " + (sharedPrefs.getBoolean("databaseLoaded",false)));

        // Add vegetables only on first launch
        if(!sharedPrefs.getBoolean("databaseLoaded",false)) {
            //TODO work with db versions

            Log.d("database", "adding stuff to the db");
            db.addVegetable(new Vegetable("Wortel", 600, "Een wortel is oranje en heel gezond!"));
            db.addVegetable(new Vegetable("Brocolli", 300, "Een brocolli is groen en heel gezond!"));
            db.addVegetable(new Vegetable("Asperge", 300, "Een asperge is wit en heel gezond!"));

            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putBoolean("databaseLoaded", true);
            editor.commit();
        }

        List<Vegetable> vegetables = db.getAllVegetables();

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