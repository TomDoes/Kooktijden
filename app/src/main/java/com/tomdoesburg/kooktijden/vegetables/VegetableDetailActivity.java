package com.tomdoesburg.kooktijden.vegetables;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.tomdoesburg.kooktijden.KooktijdenDialog;
import com.tomdoesburg.kooktijden.KooktijdenDialogTwoButtons;
import com.tomdoesburg.kooktijden.MainActivity;
import com.tomdoesburg.kooktijden.R;
import com.tomdoesburg.kooktijden.TimerService;
import com.tomdoesburg.sqlite.MySQLiteHelper;

/**
 * An activity representing a single Vegetable detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link VegetableActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link VegetableDetailFragment}.
 */
public class VegetableDetailActivity extends Activity implements KooktijdenDialogTwoButtons.ActivityCommunicator{
    private final String TAG = "VegetableDetailActivity";
    private int kookPlaatID = 0;
    private int vegID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vegetable_detail);
        // Show the Up button in the action bar.
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Log.v(TAG, "VegetableDetailActivity");
        //let's see if we can get the extra's sent with the intent that got us here
        //get intent which contains the ID of the kookPlaat we are using!
        try {
            Intent intent = getIntent();
            this.kookPlaatID = intent.getIntExtra("kookPlaatID",-1);
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
            try{
                this.vegID = getIntent().getIntExtra(VegetableDetailFragment.ARG_ITEM_ID,-1);
            }catch (Exception E){};

            Bundle arguments = new Bundle();
            arguments.putInt(VegetableDetailFragment.ARG_ITEM_ID,getIntent().getIntExtra(VegetableDetailFragment.ARG_ITEM_ID,-1));
            arguments.putInt("kookPlaatID",this.kookPlaatID);

            VegetableDetailFragment fragment = new VegetableDetailFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .add(R.id.vegetable_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.veg_details_menu, menu);
        return true;
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
        }else if(id == R.id.delete_item){
            KooktijdenDialogTwoButtons dialog = new KooktijdenDialogTwoButtons(this,getResources().getString(R.string.dialog_delete_from_db_title),getResources().getString(R.string.dialog_delete_from_db));
            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume(){
        TimerService.activityWithoutStovesActive = true;
        super.onResume();
    }

    @Override
    public void onPause(){
        TimerService.activityWithoutStovesActive = false;
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.fade_in, R.anim.slide_left2right);
        super.onBackPressed();
    }

    @Override
    public void resetDialogYesClicked() {
        //remove item from db
        try {
            MySQLiteHelper db = new MySQLiteHelper(this);
            db.deleteVegetable(vegID);
        }catch (Exception E){}

        Intent intent = new Intent(this.getApplicationContext(),MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void resetDialogCancelClicked() {
        //do not remove item from db
    }
}