package com.tomdoesburg.kooktijden.vegetables;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.tomdoesburg.kooktijden.MainActivity;
import com.tomdoesburg.kooktijden.R;

public class VegetableActivity extends Activity implements VegetableListFragment.Callbacks {

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.activity_vegetable_list,null);
        //setContentView(R.layout.activity_vegetable_list);

        getActionBar().setDisplayHomeAsUpEnabled(true);


        if (view.findViewById(R.id.vegetable_detail_container) != null) {

            mTwoPane = true;

            if (savedInstanceState == null) {

                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction
                        .replace(R.id.container, new VegetableListFragment())
                        .commit();

            }
        }

        AdView mAdView = (AdView)view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        setContentView(view);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.vegetable, menu);
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
            NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
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
                VegetableDetailFragment fragment = new VegetableDetailFragment();
                fragment.setArguments(arguments);
                getFragmentManager().beginTransaction()
                        .replace(R.id.vegetable_detail_container, fragment)
                        .commit();

            } else {
                // In single-pane mode, simply start the detail activity
                // for the selected item ID.
                Intent detailIntent = new Intent(this, VegetableDetailActivity.class);
                detailIntent.putExtra(VegetableDetailFragment.ARG_ITEM_ID, id);
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
                int timeSeconds = data.getIntExtra("timeSeconds",0);

                //pass that shit to the main activity!

                //return the result
                Intent intent = new Intent();
                intent.putExtra("timeSeconds",timeSeconds);
                this.setResult(Activity.RESULT_OK,intent);

                //finish the vegetable activity
                this.finish();
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.slide_left2right);
    }
}
