package com.tomdoesburg.kooktijden;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.tomdoesburg.model.Vegetable;
import com.tomdoesburg.sqlite.MySQLiteHelper;

import java.util.List;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set correct interface
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        int cookertype = sharedPrefs.getInt("cookertype", 0);
        switch (cookertype) {
            case 1:
                setContentView(R.layout.kookplaat1pits);
                break;
            case 2:
                setContentView(R.layout.kookplaat2pits);
                break;
            default:
                //no cooker type selected, set interface where user can select one
                setContentView(R.layout.first_start_cookers);
        }


        MySQLiteHelper db = new MySQLiteHelper(this);

        db.addVegetable(new Vegetable("Wortel", 600, "Een wortel is oranje en heel gezond!"));
        db.addVegetable(new Vegetable("Brocolli", 300, "Een brocolli is groen en heel gezond!"));
        db.addVegetable(new Vegetable("Asperge", 300, "Een asperge is wit en heel gezond!"));

        TextView test = (TextView) findViewById(R.id.testveld);

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
