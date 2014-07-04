package com.tomdoesburg.kooktijden;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Joost on 2-7-2014.
 */
// Since this is an object collection, use a FragmentStatePagerAdapter, and NOT a FragmentPagerAdapter.
public class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {
    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment;
        switch(i){
            case 0:
                fragment = new FragmentKookplaat1pits();
                return fragment;
            case 1:
                fragment = new FragmentKookplaat2pits();
                return fragment;
            case 2:
                fragment = new FragmentKookplaat3pits();
                return fragment;
        }
        //if we get to this, something is wrong
        throw new IllegalArgumentException("no layout for this integer");
        //henkasdf
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "OBJECT " + (position + 1);
    }
}
