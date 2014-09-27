package com.tomdoesburg.kooktijden;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

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

    Fragment kookPlaat1pits;
    Fragment kookPlaat2pits;
    Fragment kookPlaat4pits;
    Fragment kookPlaat5pits;
    Fragment kookPlaat6pits;

    @Override
    public Fragment getItem(int i) {
        Fragment fragment;

        switch(i){
            case 0:
                fragment = new FragmentKookplaat1pits();
                kookPlaat1pits = fragment;
                return fragment;
            case 1:
                fragment = new FragmentKookplaat2pits();
                kookPlaat2pits = fragment;
                return fragment;
            case 2:
                fragment = new FragmentKookplaat4pits();
                kookPlaat4pits = fragment;
                return fragment;
            case 3:
                fragment = new FragmentKookplaat5pits();
                kookPlaat5pits = fragment;
                return fragment;
            case 4:
                fragment = new FragmentKookplaat6pits();
                kookPlaat6pits = fragment;
                return fragment;
        }
        //if we get to this, something is wrong
        throw new IllegalArgumentException("no layout for this integer");
    }

    public Fragment getActiveFragment(int ID){
        switch(ID){
            case 0: return kookPlaat1pits;
            case 1: return kookPlaat2pits;
            case 2: return kookPlaat4pits;
            case 3: return kookPlaat5pits;
            case 4: return kookPlaat6pits;
            default: return null;
        }
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

