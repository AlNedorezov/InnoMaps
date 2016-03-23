package com.innopolis.maps.innomaps.utils;

import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.innopolis.maps.innomaps.R;

import java.util.HashMap;
import java.util.Map;

/**
 * A collection of Google Analytics trackers. Fetch the tracker you need using
 * {@code AnalyticsTrackers.getInstance().get(...)}
 * <p/>
 * This code was generated by Android Studio but can be safely modified by
 * hand at this point.
 * <p/>
 */
public final class AnalyticsTrackers {

    public enum Target {
        APP, EVENTS, DETAILED, FAVOURITE, MAPS,
        // Add more trackers here if you need, and update the code in #get(Target) below
    }

    private static AnalyticsTrackers sInstance;

    public static synchronized void initialize(Context context) {
        if (sInstance != null) {
            throw new IllegalStateException("Extra call to initialize analytics trackers");
        }

        sInstance = new AnalyticsTrackers(context);
    }

    public static synchronized AnalyticsTrackers getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException("Call initialize() before getInstance()");
        }

        return sInstance;
    }

    private final Map<Target, Tracker> mTrackers = new HashMap<>();
    private final Context mContext;

    /**
     * Don't instantiate directly - use {@link #getInstance()} instead.
     */
    private AnalyticsTrackers(Context context) {
        mContext = context.getApplicationContext();
    }

    public synchronized Tracker get(Target target) {
        if (!mTrackers.containsKey(target)) {
            Tracker tracker;
            switch (target) {
                case APP:
                    tracker = GoogleAnalytics.getInstance(mContext).newTracker(R.xml.app_tracker);
                    break;
                case EVENTS:
                    tracker = GoogleAnalytics.getInstance(mContext).newTracker(R.xml.app_tracker);
                    break;
                case DETAILED:
                    tracker = GoogleAnalytics.getInstance(mContext).newTracker(R.xml.app_tracker);
                    break;
                case FAVOURITE:
                    tracker = GoogleAnalytics.getInstance(mContext).newTracker(R.xml.app_tracker);
                    break;
                case MAPS:
                    tracker = GoogleAnalytics.getInstance(mContext).newTracker(R.xml.app_tracker);
                    break;
                default:
                    throw new IllegalArgumentException("Unhandled analytics target " + target);
            }
            mTrackers.put(target, tracker);
        }

        return mTrackers.get(target);
    }
}