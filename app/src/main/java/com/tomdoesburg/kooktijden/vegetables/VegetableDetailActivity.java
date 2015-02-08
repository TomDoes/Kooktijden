package com.tomdoesburg.kooktijden.vegetables;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import com.tomdoesburg.kooktijden.R;

/**
 * An activity representing a single Vegetable detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link VegetableActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link VegetableDetailFragment}.
 */
public class VegetableDetailActivity extends Activity {
    private String kookPlaatID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vegetable_detail);
        // Show the Up button in the action bar.
        getActionBar().setDisplayHomeAsUpEnabled(true);

        //let's see if we can get the extra's sent with the intent that got us here
        //get intent which contains the ID of the kookPlaat we are using!
        try {
            Intent intent = getIntent();
            this.kookPlaatID = intent.getStringExtra("kookPlaatID");
        }catch(NullPointerException E){
            //this should not happen ever, but better safe than sorry!
        }
        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putInt(VegetableDetailFragment.ARG_ITEM_ID,
                    getIntent().getIntExtra(VegetableDetailFragment.ARG_ITEM_ID,-1));
            arguments.putString("kookPlaatID",this.kookPlaatID);

            VegetableDetailFragment fragment = new VegetableDetailFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .add(R.id.vegetable_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            Intent intent =  new Intent(this.getApplicationContext(), VegetableActivity.class);
            intent.putExtra("kookPlaatID",this.kookPlaatID);

            NavUtils.navigateUpTo(this,intent);
            overridePendingTransition(R.anim.fade_in, R.anim.slide_left2right);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.fade_in, R.anim.slide_left2right);
        super.onBackPressed();
    }
}