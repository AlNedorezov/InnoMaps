package com.innopolis.maps.innomaps.app;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
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
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.innopolis.maps.innomaps.R;
import com.innopolis.maps.innomaps.database.DBHelper;
import com.innopolis.maps.innomaps.events.EventsFragment;
import com.innopolis.maps.innomaps.events.FavouriteFragment;
import com.innopolis.maps.innomaps.events.MapFragmentAskForRouteDialog;
import com.innopolis.maps.innomaps.maps.MapsFragment;
import com.innopolis.maps.innomaps.utils.AnalyticsTrackers;
import com.innopolis.maps.innomaps.utils.Utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String MAPS = "Maps";
    private final String FAV = "Favourite";
    private final String EVENTS = "Events";
    private final String ABOUT = "About";
    private final String DETAILED = "Detailed";

    private boolean doubleBackToExitPressedOnce = false;
    private boolean doubleBackToFinishRoute = false;
    public final List<SearchableItem> searchItems = new LinkedList<>();

    private Toolbar toolbar;
    private DrawerLayout drawer;

    // TODO: Check access level and add appropriate access modifiers
    DBHelper dbHelper;
    SQLiteDatabase database;
    NavigationView navigationView;
    SearchView searchView;

    CustomScrollView scrollView;
    FloatingActionButton routeButton;

    private static MainActivity mInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO: Split onCreate method into several logical part
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: Why do you need this code block?
        //for deep linking
        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        scrollView = (CustomScrollView) findViewById(R.id.bottom_sheet);
        routeButton = (FloatingActionButton) findViewById(R.id.goto_fab);
        final TextView locationText = (TextView) scrollView.findViewById(R.id.locationText);
        final TextView idPoi = (TextView) scrollView.findViewById(R.id.idPoi);

        routeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new MapFragmentAskForRouteDialog();
                Bundle bundle = new Bundle();
                // TODO: Extract string constants
                bundle.putString("dialogSource", "MapsFragment");
                if (idPoi.getText().toString().equals("event")) {
                    bundle.putString("type", "event");
                } else {
                    bundle.putString("type", "poi");
                    bundle.putString("id", idPoi.getText().toString());
                }

                bundle.putString("destination", locationText.getText().toString());
                newFragment.setArguments(bundle);
                newFragment.show(getSupportFragmentManager(), "FindRoute");
            }
        });

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mInstance = this;

        setToggle(toolbar);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
        Fragment fragment = new MapsFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.content_frame, fragment, MAPS).addToBackStack(MAPS);
        ft.commit();
        // TODO: Replace MAPS constant with string resource
        getSupportActionBar().setTitle(MAPS);
        dbHelper = new DBHelper(MainActivity.this);
        database = dbHelper.getReadableDatabase();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        /*
      ATTENTION: This was auto-generated to implement the App Indexing API.
      See https://g.co/AppIndexing/AndroidStudio for more information.
     */
        GoogleApiClient client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        AnalyticsTrackers.initialize(this);
        AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
    }

    public static synchronized MainActivity getInstance() {
        return mInstance;
    }

    // TODO: Extract all analytics into separate class
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
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (scrollView.getVisibility() == View.VISIBLE) {
            scrollView.setVisibility(View.GONE);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                // TODO: Replace this string with a constant
                final MapsFragment mapsFragment = (MapsFragment) getSupportFragmentManager().findFragmentByTag("Maps");

                if (mapsFragment.mapRoute != null && mapsFragment.mapRoute.hasCurrentPath) {

                    if (doubleBackToFinishRoute) {
                        mapsFragment.mapRoute.finishRoute(false);
                    }

                    this.doubleBackToFinishRoute = true;
                    // TODO: Replace this hard-coded string with a resource string
                    Snackbar.make(findViewById(android.R.id.content), "Please click BACK again to finish the route", Snackbar.LENGTH_LONG)
                            .setAction("FINISH", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mapsFragment.mapRoute.finishRoute(false);
                        }
                    }).setActionTextColor(Color.WHITE).show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            doubleBackToFinishRoute = false;
                        }
                        // Extract 2000 to a local constant
                    }, 2000);

                } else {
                    if (doubleBackToExitPressedOnce) {
                        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        super.onBackPressed();
                    }

                    this.doubleBackToExitPressedOnce = true;
                    // TODO: Replace this hard-coded string with a resource string
                    Snackbar.make(findViewById(android.R.id.content), "Please click BACK again to exit", Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE).show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce = false;
                        }
                    }, 2000);
                }

            } else {
                int lastEntry = getSupportFragmentManager().getBackStackEntryCount() - 1;
                FragmentManager.BackStackEntry last = getSupportFragmentManager().getBackStackEntryAt(lastEntry);
                if (last.getName().equals(DETAILED)) {
                    getSupportActionBar().setTitle(getSupportFragmentManager().getBackStackEntryAt(lastEntry - 1).getName());
                    getSupportFragmentManager().popBackStackImmediate();
                } else {
                    //TODO: handle back press to update events in EventsFragment
                    getSupportFragmentManager().popBackStackImmediate(MAPS, 0);
                    // TODO: Title strings should be from resources
                    getSupportActionBar().setTitle(MAPS);
                }
            }
            setToggle(toolbar);
            highlightItemDrawer(getSupportActionBar().getTitle().toString());
        }
    }

    public void setActivityDrawerToggle() {
        setToggle(toolbar);
    }

    private void setToggle(Toolbar toolbar) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        invalidateOptionsMenu();
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_toolbar, menu);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchItems.clear();
        
        // TODO: Unclear what do they mean, and how they are used
        Integer roomPois = 0;
        Integer pois = 1;
        SearchableItem.addEvents(searchItems, DBHelper.readUniqueEvents(this, false));
        SearchableItem.addPois(searchItems, DBHelper.readPois(database, roomPois));
        SearchableItem.addPois(searchItems, DBHelper.readPois(database, pois));
        final List<SearchableItem> adapterList = new LinkedList<>(searchItems);

        final SearchView.SearchAutoComplete searchBox = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        searchBox.setAdapter(new SuggestionAdapter(this, R.layout.complete_row, adapterList));
        searchBox.setThreshold(0);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapterList.clear();
                for (SearchableItem item : searchItems) {
                    if (item.getType().equals("room") && (item.getName().toLowerCase().contains(s.toString().toLowerCase()) ||
                            item.getBuilding().toLowerCase().contains(s.toString().toLowerCase()) ||
                            item.getFloor().toLowerCase().contains(s.toString().toLowerCase()) ||
                            item.getRoom().toLowerCase().contains(s.toString().toLowerCase()))) {
                        adapterList.add(item);
                    } else if (item.getName().toLowerCase().contains(s.toString().toLowerCase()) ||
                            item.getBuilding().toLowerCase().contains(s.toString().toLowerCase()) ||
                            item.getFloor().toLowerCase().contains(s.toString().toLowerCase())) {
                        adapterList.add(item);
                    }
                }
                ((SuggestionAdapter) searchBox.getAdapter()).refresh(adapterList);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (adapterList.size() == 0) {
                    Utils.hideKeyboard(MainActivity.this);
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
        // TODO: change title to tag
        String title = "";
        Fragment fragment = null;

        final MapsFragment mapsFragment = (MapsFragment) getSupportFragmentManager().findFragmentByTag("Maps");

        if (mapsFragment != null && mapsFragment.mapRoute != null && mapsFragment.mapRoute.hasCurrentPath) {
            mapsFragment.mapRoute.finishRoute(false);
        }

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

        // TODO: you already have a drawer from onCreate!
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.replace(R.id.content_frame, fragment, title).addToBackStack(title);
            ft.commit();
        }
        // TODO: title should always be from resources
        getSupportActionBar().setTitle(title);
        return true;
    }

    public void highlightItemDrawer(String title){
        // TODO: HashMap is not needed here, could be removed
        Map<String, Integer> hashmap = new HashMap<>();
        String [] titles = {MAPS,FAV,EVENTS,ABOUT};
        hashmap.put(MAPS,0);
        hashmap.put(FAV,1);
        hashmap.put(EVENTS,2);
        hashmap.put(ABOUT,3);
        navigationView.getMenu().getItem(hashmap.get(title)).setChecked(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Tracking the screen view
        MainActivity.getInstance().trackScreenView("Main Fragment");
    }
}
