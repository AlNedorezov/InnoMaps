package com.innopolis.maps.innomaps.app;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.innopolis.maps.innomaps.R;
import com.innopolis.maps.innomaps.database.DBHelper;
import com.innopolis.maps.innomaps.events.Event;
import com.innopolis.maps.innomaps.events.EventsFragment;
import com.innopolis.maps.innomaps.events.FavouriteFragment;
import com.innopolis.maps.innomaps.utils.AnalyticsTrackers;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String MAPS = "Maps";
    private final String FAV = "Favourite";
    private final String EVENTS = "Events";
    private final String ABOUT = "About";
    private final String DETAILED = "Detailed";

    private Tracker mTracker;

    private boolean doubleBackToExitPressedOnce = false;

    DBHelper dbHelper;
    SQLiteDatabase database;

    SearchView searchView;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private static MainActivity mInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mInstance = this;


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Fragment fragment = new MapsFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.content_frame, fragment, MAPS).addToBackStack(MAPS);
        ft.commit();
        getSupportActionBar().setTitle(MAPS);
        dbHelper = new DBHelper(MainActivity.this);
        database = dbHelper.getReadableDatabase();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        AnalyticsTrackers.initialize(this);
        AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
    }

    public static synchronized MainActivity getInstance() {
        return mInstance;
    }

    public synchronized Tracker getGoogleAnalyticsTracker() {
        AnalyticsTrackers analyticsTrackers = AnalyticsTrackers.getInstance();
        return analyticsTrackers.get(AnalyticsTrackers.Target.APP);
    }

    /***
     * Tracking screen view
     *
     * @param mainActivity screen name to be displayed on GA dashboard
     */
    public void trackScreenView(String mainActivity) {
        Tracker t = getGoogleAnalyticsTracker();

        // Set screen name.
        t.setScreenName(mainActivity);

        // Send a screen view.
        t.send(new HitBuilders.ScreenViewBuilder().build());

        GoogleAnalytics.getInstance(this).dispatchLocalHits();
    }

    /***
     * Tracking exception
     *
     * @param e exception to be tracked
     */
    public void trackException(Exception e) {
        if (e != null) {
            Tracker tracker = getGoogleAnalyticsTracker();

            tracker.send(new HitBuilders.ExceptionBuilder()
                            .setDescription(
                                    new StandardExceptionParser(this, null)
                                            .getDescription(Thread.currentThread().getName(), e))
                            .setFatal(false)
                            .build()
            );
        }
    }

    /***
     * Tracking event
     *
     * @param category event category
     * @param action   action of the event
     * @param label    label
     */
    public void trackEvent(String category, String action, String label) {
        Tracker t = getGoogleAnalyticsTracker();

        // Build and send an Event.
        t.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).build());
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() == 1) {

                if (doubleBackToExitPressedOnce) {
                    finish();
                }

                this.doubleBackToExitPressedOnce = true;
                Snackbar.make(findViewById(android.R.id.content), "Please click BACK again to exit", Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);

            } else {
                int lastEntry = getSupportFragmentManager().getBackStackEntryCount() - 1;
                FragmentManager.BackStackEntry last = getSupportFragmentManager().getBackStackEntryAt(lastEntry);
                if (last.getName().equals(DETAILED)) {
                    getSupportActionBar().setTitle(getSupportFragmentManager().getBackStackEntryAt(lastEntry - 1).getName());
                    getSupportFragmentManager().popBackStackImmediate();
                } else {
                    getSupportFragmentManager().popBackStackImmediate(MAPS, 0);
                    getSupportActionBar().setTitle(MAPS);
                }
            }
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            invalidateOptionsMenu();
            toggle.setDrawerIndicatorEnabled(true);
            toggle.syncState();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();

        final List<Event> eventList = DBHelper.readUniqueEvents(this, false);
        final List<Event> eventsDB = new ArrayList<>(eventList);

        final SearchView.SearchAutoComplete searchBox = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        searchBox.setAdapter(new SuggestionAdapter(this, R.layout.complete_row, eventList));
        searchBox.setThreshold(0);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                eventList.clear();
                for (Event event : eventsDB) {
                    if (event.getSummary().toLowerCase().contains(s.toString().toLowerCase()) ||
                            event.getBuilding().toLowerCase().contains(s.toString().toLowerCase()) ||
                            event.getFloor().toLowerCase().contains(s.toString().toLowerCase()) ||
                            event.getRoom().toLowerCase().contains(s.toString().toLowerCase())) {
                        eventList.add(event);
                    }
                }
                DBHelper.readPois(database);
                ((SuggestionAdapter) searchBox.getAdapter()).refresh(eventList);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (eventList.size() == 0) {
                    Snackbar.make(findViewById(android.R.id.content), R.string.empty_search, Snackbar.LENGTH_LONG).show();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();
        String title = "";
        Fragment fragment = null;

        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            if (id == R.id.nav_maps) {
                fragment = new MapsFragment();
                title = MAPS;
            } else if (id == R.id.nav_favourite) {
                fragment = new FavouriteFragment();
                title = FAV;
            } else if (id == R.id.nav_event) {
                fragment = new EventsFragment();
                title = EVENTS;
            } else if (id == R.id.nav_about) {
                fragment = new About();
                title = ABOUT;
            }
            if (getSupportActionBar() != null) getSupportActionBar().setTitle(title);
        } else {
            if (id == R.id.nav_maps) {
                title = MAPS;
                if (getSupportFragmentManager().findFragmentByTag(MAPS) != null) {
                    getSupportFragmentManager().popBackStackImmediate(MAPS, 0);
                } else {
                    fragment = new MapsFragment();
                }
            } else if (id == R.id.nav_favourite) {
                title = FAV;
                if (getSupportFragmentManager().findFragmentByTag(FAV) != null) {
                    getSupportFragmentManager().popBackStackImmediate(FAV, 0);
                } else {
                    fragment = new FavouriteFragment();
                }
            } else if (id == R.id.nav_event) {
                title = EVENTS;
                if (getSupportFragmentManager().findFragmentByTag(EVENTS) != null) {
                    getSupportFragmentManager().popBackStackImmediate(EVENTS, 0);
                } else {
                    fragment = new EventsFragment();
                }

            } else if (id == R.id.nav_about) {
                title = ABOUT;
                if (getSupportFragmentManager().findFragmentByTag(ABOUT) != null) {
                    getSupportFragmentManager().popBackStackImmediate(ABOUT, 0);
                } else {
                    fragment = new About();
                }
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.replace(R.id.content_frame, fragment, title).addToBackStack(title);
            ft.commit();
        }
        getSupportActionBar().setTitle(title);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Tracking the screen view
        MainActivity.getInstance().trackScreenView("Main Fragment");
    }
}
