package com.tomdoesburg.kooktijden;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

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
        //PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        setContentView(R.layout.swipert);

        // ViewPager and its adapters use support library
        // fragments, so use getSupportFragmentManager.
        mFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mFragmentPagerAdapter);

        //set the correct interface (if none set in preferences, this lets user choose one as default)
        //setInterface(false);

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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void initTimer(View v) {

        final ProgressBar kookplaat;
        int id;

        switch (v.getId()) {
            case R.id.kookplaat1:
                kookplaat = (ProgressBar) findViewById(R.id.kookplaat1);
                break;
            case R.id.kookplaat2:
                kookplaat = (ProgressBar) findViewById(R.id.kookplaat2);
                break;
            case R.id.kookplaat3:
                kookplaat = (ProgressBar) findViewById(R.id.kookplaat3);
                break;
            default:
                throw new RuntimeException("Unknown kookplaat ID");
        }

        //TODO
        //create a selection thingy to selectwhich vegetable to put on this timer
        //something to initialize a timer on a given kookplaat for a given vegetable (separate thread using handler?)
        //something to start the actual timer when the water starts boiling
    }

}
