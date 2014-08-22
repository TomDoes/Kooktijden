package com.tomdoesburg.kooktijden.vegetables;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.tomdoesburg.kooktijden.R;

import com.tomdoesburg.model.Vegetable;
import com.tomdoesburg.sqlite.MySQLiteHelper;

/**
 * A fragment representing a single Vegetable detail screen.
 * This fragment is either contained in a {@link VegetableActivity}
 * in two-pane mode (on tablets) or a {@link VegetableDetailActivity}
 * on handsets.
 */
public class VegetableDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    private Vegetable vegetable;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public VegetableDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            MySQLiteHelper db = new MySQLiteHelper(this.getActivity());
            vegetable = db.getVegetable(getArguments().getInt(ARG_ITEM_ID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_vegetable_detail, container, false);

        if (vegetable != null) {
            ((TextView) rootView.findViewById(R.id.vegetable_detail)).setText(vegetable.getDescription());
        }

        return rootView;
    }
}
