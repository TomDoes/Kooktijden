package com.tomdoesburg.kooktijden.vegetables;

import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import com.tomdoesburg.kooktijden.R;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

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
    private TextView titleView;
    private TextView timeView;
    private TextView descriptionView;
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
            titleView = ((TextView) rootView.findViewById(R.id.vegetable_detail_title));
            timeView = ((TextView) rootView.findViewById(R.id.vegetable_detail_time));
            descriptionView = ((TextView) rootView.findViewById(R.id.vegetable_detail_description));

            Typeface typeFace = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Thin.ttf");
            Typeface typeFaceLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.ttf");

            titleView.setTypeface(typeFaceLight);
            timeView.setTypeface(typeFace);
            descriptionView.setTypeface(typeFace);

            titleView.setText(vegetable.getNameEN());

            if(vegetable.getCookingTimeMin() == vegetable.getCookingTimeMax()) {
                timeView.setText("Cooking time: " + vegetable.getCookingTimeMin() + " min.");
            } else {
                timeView.setText("Cooking time: " + vegetable.getCookingTimeMin() + "-" + vegetable.getCookingTimeMax() + " min.");
            }

            descriptionView.setText(vegetable.getDescriptionEN());
            ImageButton setTimerBtn = (ImageButton)rootView.findViewById(R.id.timer_button);
            GradientDrawable circleShape = (GradientDrawable)setTimerBtn.getBackground();
            circleShape.setColor(getResources().getColor(R.color.pink));

            Animation btnAnimation = AnimationUtils.loadAnimation(this.getActivity(), R.anim.slide_righ2left_slow);
            Animation titleAnimation = AnimationUtils.loadAnimation(this.getActivity(), R.anim.slide_down);

            setTimerBtn.startAnimation(btnAnimation);
            titleView.startAnimation(titleAnimation);
        }

        AdView mAdView = (AdView)rootView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        return rootView;
    }
}
