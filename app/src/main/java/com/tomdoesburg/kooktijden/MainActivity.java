package com.tomdoesburg.kooktijden;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tomdoesburg.model.Vegetable;
import com.tomdoesburg.sqlite.MySQLiteHelper;

import java.util.List;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String cookertype = sharedPrefs.getString("cookertype", "noneSelected");
        if (sharedPrefs.equals("noneSelected")) {
            //no cookertype selected, prompt user to select one
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new CookerTypeFragment())
                    .commit();
        }
        //TODO if 'cookertype' does contain a certain value, then set the correct interface for the user :)

        MySQLiteHelper db = new MySQLiteHelper(this);

        db.addVegetable(new Vegetable("Wortel", 600, "Een wortel is oranje en heel gezond!"));
        db.addVegetable(new Vegetable("Brocolli", 300, "Een brocolli is groen en heel gezond!"));
        db.addVegetable(new Vegetable("Asperge", 300, "Een asperge is wit en heel gezond!"));

        TextView test = (TextView)findViewById(R.id.testveld);

        List<Vegetable>vegetables = db.getAllVegetables();



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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

    public static class CookerTypeFragment extends Fragment {

        public CookerTypeFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.first_start_cookers, container, false);
            return rootView;
        }
    }
}
