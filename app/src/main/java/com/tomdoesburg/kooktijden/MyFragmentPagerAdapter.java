package com.tomdoesburg.kooktijden;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.tomdoesburg.kooktijden.kookplaten.FragmentKookplaat1pits;
import com.tomdoesburg.kooktijden.kookplaten.FragmentKookplaat2pits;
import com.tomdoesburg.kooktijden.kookplaten.FragmentKookplaat4pits;
import com.tomdoesburg.kooktijden.kookplaten.FragmentKookplaat5pits;
import com.tomdoesburg.kooktijden.kookplaten.FragmentKookplaat6pits;

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
                fragment = new FragmentKookplaat4pits();
                return fragment;
            case 3:
                fragment = new FragmentKookplaat5pits();
                return fragment;
            case 4:
                fragment = new FragmentKookplaat6pits();
                return fragment;
        }
        //if we get to this, something is wrong
        throw new IllegalArgumentException("no layout for this integer");
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "OBJECT " + (position + 1);
    }

}

