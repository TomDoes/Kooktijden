package com.tomdoesburg.kooktijden;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Joost on 2-7-2014.
 */
// Instances of this class are fragments representing a single
// object in our collection.
public class FragmentKookplaat2pits extends Fragment {
    public static final String ARG_OBJECT = "object";

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated properly.
        View rootView = inflater.inflate(R.layout.kookplaat2pits, container, false);
        return rootView;
    }
}