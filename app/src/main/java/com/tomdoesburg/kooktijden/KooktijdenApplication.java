package com.tomdoesburg.kooktijden;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;

/**
 * Created by Tom on 27/9/14.
 */
public class KooktijdenApplication extends Application {

    // Enum for Analytics trackers
    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public KooktijdenApplication() {
        super();
    }

    public synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            if(trackerId == TrackerName.APP_TRACKER) {
                Tracker t = analytics.newTracker(R.xml.global_tracker);
                mTrackers.put(trackerId, t);
            }

        }
        return mTrackers.get(trackerId);
    }
}
