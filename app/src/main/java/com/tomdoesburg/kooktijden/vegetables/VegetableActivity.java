package com.tomdoesburg.kooktijden.vegetables;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.tomdoesburg.kooktijden.R;

public class VegetableActivity extends Activity implements VegetableListFragment.Callbacks {

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vegetable_list);
        if (findViewById(R.id.vegetable_detail_container) != null) {

            mTwoPane = true;

            if (savedInstanceState == null) {
                getFragmentManager().beginTransaction()
                        .add(R.id.container, new VegetableListFragment())
                        .commit();

            }
        }

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
        if (id == R.id.action_settings) {
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
                startActivity(detailIntent);
            }
    }
}
