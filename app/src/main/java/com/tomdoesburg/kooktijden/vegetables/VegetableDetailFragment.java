package com.tomdoesburg.kooktijden.vegetables;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.tomdoesburg.kooktijden.KooktijdenApplication;
import com.tomdoesburg.kooktijden.R;
import com.tomdoesburg.model.Vegetable;
import com.tomdoesburg.sqlite.MySQLiteHelper;

import java.util.Locale;

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

    private String kookPlaatID = "";
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
        if(getArguments().containsKey("kookPlaatID")){
            this.kookPlaatID = getArguments().getString("kookPlaatID");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // analytics
        Tracker t = ((KooktijdenApplication) getActivity().getApplication()).getTracker(KooktijdenApplication.TrackerName.APP_TRACKER);
        t.setScreenName("Vegetable Detail");
        t.send(new HitBuilders.AppViewBuilder().setCustomDimension(1, vegetable.getNameEN()).build());

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
            String language = Locale.getDefault().getDisplayLanguage();
            if(language.equals("Nederlands")) {
                titleView.setText(vegetable.getNameNL());
                descriptionView.setText(vegetable.getDescriptionNL());
            } else {
                titleView.setText(vegetable.getNameEN());
                descriptionView.setText(vegetable.getDescriptionEN());
            }


            if(vegetable.getCookingTimeMin() == vegetable.getCookingTimeMax()) {
                timeView.setText(getString(R.string.cookingTime) + " " + vegetable.getCookingTimeMin() + " min.");
            } else {
                timeView.setText(getString(R.string.cookingTime) + " " + vegetable.getCookingTimeMin() + "-" + vegetable.getCookingTimeMax() + " min.");
            }

            ImageButton setTimerBtn = (ImageButton)rootView.findViewById(R.id.timer_button);
            GradientDrawable circleShape = (GradientDrawable)setTimerBtn.getBackground();
            circleShape.setColor(getResources().getColor(R.color.pink));

            setTimerBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //get time from database
                    int vegId = vegetable.getId();

                    //return the result
                    Intent intent = new Intent();
                    intent.putExtra("vegId",vegId);
                    intent.putExtra("kookPlaatID",kookPlaatID);
                    getActivity().setResult(Activity.RESULT_OK, intent);

                    //finish the details activity
                    getActivity().finish();
                }
            });
        }

        return rootView;
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ImageButton setTimerBtn = (ImageButton)getActivity().findViewById(R.id.timer_button);
        titleView = ((TextView) getActivity().findViewById(R.id.vegetable_detail_title));

        //final Animation btnAnimation = AnimationUtils.loadAnimation(this.getActivity(), R.anim.slide_right2left_slow);
        final Animation btnAnimation = AnimationUtils.loadAnimation(this.getActivity(), R.anim.slide_right2left);
        Animation titleAnimation = AnimationUtils.loadAnimation(this.getActivity(), R.anim.slide_down);

        //setTimerBtn.startAnimation(btnAnimation);
        titleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setTimerBtn.setVisibility(View.VISIBLE);
                setTimerBtn.startAnimation(btnAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        titleView.setVisibility(View.VISIBLE);
        titleView.startAnimation(titleAnimation);

    }
}