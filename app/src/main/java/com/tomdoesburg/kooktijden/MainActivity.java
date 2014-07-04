package com.tomdoesburg.kooktijden;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.tomdoesburg.model.Vegetable;
import com.tomdoesburg.sqlite.MySQLiteHelper;

import java.util.List;

public class MainActivity extends FragmentActivity {

    SharedPreferences sharedPrefs;

    // When requested, this adapter returns a DemoObjectFragment,
    // representing an object in the collection.
    MyFragmentPagerAdapter mFragmentPagerAdapter;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set default preferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        setContentView(R.layout.swipert);

        //ViewPager and its adapters use support library fragments, so use getSupportFragmentManager.
        mFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mFragmentPagerAdapter);

        //set the correct interface (if none set in preferences, this lets user choose one as default)
        setInterface(false);

        MySQLiteHelper db = new MySQLiteHelper(this);

        db.addVegetable(new Vegetable("Wortel", 600, "Een wortel is oranje en heel gezond!"));
        db.addVegetable(new Vegetable("Brocolli", 300, "Een brocolli is groen en heel gezond!"));
        db.addVegetable(new Vegetable("Asperge", 300, "Een asperge is wit en heel gezond!"));

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
            Intent intent = new Intent(this, VegetableActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //sets the correct cooker type (called from kookplaten_overview.xml)
    public void setKookplaatType(View view){

        //save cooker type in settings
        int type;
        switch(view.getId()) {
            case R.id.kookplaat1pits:
                type=1;
                break;
            case R.id.kookplaat2pits:
                type=2;
                break;
            case R.id.kookplaat3pits:
                type=3;
                break;
            case R.id.kookplaat4pits:
                type=4;
                break;
            case R.id.kookplaat5pits:
                type=5;
                break;
            case R.id.kookplaat6pits:
                type=6;
                break;
            default:
                throw new RuntimeException("Unknown button ID");
        }
        sharedPrefs.edit().putInt("aantalPitten",type).commit();

        //set the correct interface
        setInterface(false);
    }

    //sets the interface for a certain cooker
    public void setInterface(boolean reset){
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        int aantalPitten = sharedPrefs.getInt("aantalPitten", 0);

        //reset current value, so that user must select new cooker type
        if(reset){
            aantalPitten = 0;
        }

        //interface switch
        switch (aantalPitten) {
            case 1:
                setContentView(R.layout.kookplaat1pits);
                break;
            case 2:
                setContentView(R.layout.kookplaat2pits);
                break;
            default:
                //no cooker type selected, set interface where user can select one
                setContentView(R.layout.kookplaten_overview);
        }
    }
}
