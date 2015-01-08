package com.tomdoesburg.kooktijden.vegetables;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.tomdoesburg.kooktijden.MainActivity;
import com.tomdoesburg.kooktijden.R;
import com.tomdoesburg.kooktijden.StateSaver;

public class VegetableActivity extends Activity implements VegetableListFragment.Callbacks {
    private final String TAG = "VegetableActivity";
    private boolean mTwoPane;
    private String kookPlaatID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view_veg_list = getLayoutInflater().inflate(R.layout.activity_vegetable_list, null);


        //get intent which contains the ID of the kookPlaat we are using!
        try {
            Intent intent = getIntent();
            this.kookPlaatID = intent.getStringExtra("kookPlaatID");
            Log.v(TAG,this.kookPlaatID);
        }catch(NullPointerException E){
            //this should not happen ever, but better safe than sorry!
            Log.v(TAG,E.toString());
        }


        getActionBar().setDisplayHomeAsUpEnabled(true);


        if (view_veg_list.findViewById(R.id.vegetable_detail_container) != null) {

            mTwoPane = true;

            if (savedInstanceState == null) {

                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                VegetableDetailFragment frag = new VegetableDetailFragment();
                Bundle args = new Bundle();
                args.putString("kookPlaatID", this.kookPlaatID);
                args.putInt(VegetableDetailFragment.ARG_ITEM_ID, 1);

                Log.v(TAG,"argument is " + this.kookPlaatID);
                frag.setArguments(args);

                fragmentTransaction
                        .replace(R.id.vegetable_detail_container, frag)
                        .commit();

            }
        }

        if(!mTwoPane) {
            AdView mAdView = (AdView) view_veg_list.findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }



        final SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Log.v("firstStart", "Is this the first time the list is launched? - " + sharedPrefs.getBoolean("firstStart_pt2", false));
        if (!sharedPrefs.contains("firstStart_pt2")) {
            //the list is being launched for first time, show introduction

            //create empty frame
            final FrameLayout layout = new FrameLayout(this);
            setContentView(layout);

            //inflate our regular layout in the frame
            layout.addView(view_veg_list);

            //inflate and add instructional overlay
            final View instructional = getLayoutInflater().inflate(R.layout.instructional_overlay_activity_vegetable_list,null);
            layout.addView(instructional);
            Button instructions_close = (Button) instructional.findViewById(R.id.instructions_close);
            instructions_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    layout.removeView(instructional);
                    sharedPrefs.edit().putBoolean("firstStart_pt2", false).commit();
                }
            });

        } else {
            setContentView(view_veg_list);
        }
    }

    public String getKookPlaatID(){
        return this.kookPlaatID;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this.getApplicationContext(), MainActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.slide_left2right);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(int id) {
//        getFragmentManager().beginTransaction()
//                .add(R.id.container, new VegetableDetailFragment())
//                .commit();
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putInt(VegetableDetailFragment.ARG_ITEM_ID, id);
            arguments.putString("kookPlaatID", this.kookPlaatID);

            VegetableDetailFragment fragment = new VegetableDetailFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .replace(R.id.vegetable_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this.getApplicationContext(), VegetableDetailActivity.class);
            detailIntent.putExtra(VegetableDetailFragment.ARG_ITEM_ID, id);
            detailIntent.putExtra("kookPlaatID", this.kookPlaatID);
            startActivityForResult(detailIntent, 123);
            overridePendingTransition(R.anim.slide_right2left, R.anim.fade_out);
        }
    }

    // Call Back method to get the cooking time from the details Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        // check if the request code is same as what is passed here it is 9001
        if(requestCode==123){
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a vegetable
                int vegId = data.getIntExtra("vegId",0);
                this.kookPlaatID = data.getStringExtra("kookPlaatID");
                //pass that shit to the main activity!

                //return the result
                Intent intent = new Intent();
                intent.putExtra("vegId", vegId);
                intent.putExtra("kookPlaatID", this.kookPlaatID);
                this.setResult(Activity.RESULT_OK, intent);
                //finish the vegetable activity
                this.finish();
            }
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this.getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.slide_left2right);
        super.onBackPressed();

    }
}