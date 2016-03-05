package com.innopolis.maps.innomaps.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.innopolis.maps.innomaps.R;
import com.innopolis.maps.innomaps.utils.Utils;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import android.support.v4.app.Fragment;
import android.widget.Toast;


public class DetailedEvent extends Fragment {

    Context context;
    DBHelper dbHelper;
    SQLiteDatabase database;

    TextView eventName;
    TextView timeLeft;
    TextView location;
    TextView dateTime;
    TextView description;
    TextView organizer;
    TextView duration;

    CheckedTextView favCheckBox;
    private GoogleMap mMap;
    private UiSettings mSettings;
    SupportMapFragment mSupportMapFragment;
    final private String NULL = "";

    String contactChecked, linkChecked, summary, htmlLink, start, end, descriptionStr, creator,
            telegram, telegramContact, eventID, building, floor, room, latitude, longitude, checked;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.detailed_menu_toolbar, menu);
        MenuItem item = menu.findItem(R.id.toolbar_share);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.toolbar_share:
                        Toast.makeText(DetailedEvent.this.context, "Hello World", Toast.LENGTH_SHORT).show();
                }
                return true;
            }

        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        ((DrawerLayout) getActivity().findViewById(R.id.drawer_layout)).setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = getActivity().getApplicationContext();
        View view = inflater.inflate(R.layout.event_desc, container, false);
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
        eventName = (TextView) view.findViewById(R.id.eventName);
        timeLeft = (TextView) view.findViewById(R.id.timeLeft);
        location = (TextView) view.findViewById(R.id.location);
        dateTime = (TextView) view.findViewById(R.id.dateTime);
        description = (TextView) view.findViewById(R.id.description);
        description.setMovementMethod(new ScrollingMovementMethod());
        organizer = (TextView) view.findViewById(R.id.organizer);
        duration = (TextView) view.findViewById(R.id.duration);
        final CheckBox favCheckBox = (CheckBox) view.findViewById(R.id.favCheckBox);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            eventID = bundle.getString("eventID", "");
        }
        final Cursor cursor = database.query(DBHelper.TABLE1, null, "eventID=?", new String[]{eventID}, null, null, null);
        cursor.moveToFirst();
        do {
            int summary, htmlLink, start, end, checked;
            summary = cursor.getColumnIndex(DBHelper.COLUMN_SUMMARY);
            htmlLink = cursor.getColumnIndex(DBHelper.COLUMN_LINK);
            start = cursor.getColumnIndex(DBHelper.COLUMN_START);
            end = cursor.getColumnIndex(DBHelper.COLUMN_END);
            checked = cursor.getColumnIndex(DBHelper.COLUMN_FAV);
            this.summary = cursor.getString(summary);
            this.htmlLink = cursor.getString(htmlLink);
            this.start = cursor.getString(start);
            this.end = cursor.getString(end);
            this.checked = cursor.getString(checked);
            String[] summaryArgs = new String[]{cursor.getString(summary)};
            Cursor cursor1 = database.query(DBHelper.TABLE2, null, "summary=?", summaryArgs, null, null, null);
            cursor1.moveToFirst();
            int description = cursor1.getColumnIndex("description");
            int creator_name = cursor1.getColumnIndex("creator_name");
            int telegram = cursor1.getColumnIndex(DBHelper.COLUMN_TELEGRAM_GROUP);
            int telegramContact = cursor1.getColumnIndex(DBHelper.COLUMN_TELEGRAM_CONTACT);
            this.descriptionStr = cursor1.getString(description);
            this.creator = cursor1.getString(creator_name);
            this.telegram = cursor1.getString(telegram);
            this.telegramContact = cursor1.getString(telegramContact);

            cursor1.close();
        } while (cursor.moveToNext());
        cursor.close();
        final String[] eventIDArgs = new String[]{eventID};
        Cursor locationC = database.query(DBHelper.TABLE3, null, "eventID=?", eventIDArgs, null, null, null);
        if (locationC.moveToFirst()) {
            building = locationC.getString(locationC.getColumnIndex(DBHelper.COLUMN_BUILDING));
            floor = locationC.getString(locationC.getColumnIndex(DBHelper.COLUMN_FLOOR));
            room = locationC.getString(locationC.getColumnIndex(DBHelper.COLUMN_ROOM));
            latitude = locationC.getString(locationC.getColumnIndex(DBHelper.COLUMN_LATITIDE));
            longitude = locationC.getString(locationC.getColumnIndex(DBHelper.COLUMN_LONGITUDE));
        }
        database.close();

        eventName.setText(summary);
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = Utils.googleTimeFormat.parse(start);
            endDate = Utils.googleTimeFormat.parse(end);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        timeLeft.setText(Utils.prettyTime.format(startDate));
        String[] locationText = {building, floor, room};
        location.setText(StringUtils.join(Utils.clean(locationText), ", "));
        dateTime.setText(Utils.commonTime.format(startDate));
        Long durationTime = TimeUnit.MILLISECONDS.toMinutes(endDate.getTime() - startDate.getTime());
        duration.setText("Duration: " + String.valueOf(durationTime) + "min");
        description.setText(descriptionStr);

        if (!telegramContact.equals(NULL) || !telegram.equals(NULL)) {
            organizer.setTextColor(Color.BLUE);

            if (telegram.equals(NULL) && !telegramContact.equals(NULL)) {
                contactChecked = checkContact(telegramContact);
                SpannableString content = new SpannableString(contactChecked);
                telegramTransfer(content, contactChecked, contactChecked);

            } else if (!telegram.equals(NULL)) {
                SpannableString content = new SpannableString("Group link");
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                final String chatLink = "group of event";
                linkChecked = checkLink(telegram);
                organizer.setText(content);
                telegramTransfer(content, chatLink, linkChecked);
            }
        }

        if (checked.equals("1")) {
            favCheckBox.setChecked(true);
        } else {
            favCheckBox.setChecked(false);
        }
        favCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String isFav = (favCheckBox.isChecked()) ? "1" : "0";
                ContentValues cv = new ContentValues();
                dbHelper = new DBHelper(context);
                database = dbHelper.getWritableDatabase();
                cv.put(DBHelper.COLUMN_FAV, isFav);
                database.update(DBHelper.TABLE1, cv, "eventID = ?", new String[]{eventID});
                dbHelper.close();
            }
        });


        FloatingActionButton fabButton = (FloatingActionButton) view.findViewById(R.id.fabButton);
        fabButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(DetailedEvent.this.context, "Hello World", Toast.LENGTH_SHORT).show();

            }
        });
        initializeMap(latitude, longitude);
        return view;
    }


    private static String cutter(String string, int checkIndex) {
        String link = string.substring(0, checkIndex);
        return link;
    }


    private static String checkLink(String string) {
        int spaceIndex = string.indexOf(" ", 12); //except "Group chat: "
        int paragraphIndex = string.indexOf("\n");
        int commaIndex = string.indexOf(",");
        if (spaceIndex != -1) return cutter(string, spaceIndex);
        else if (paragraphIndex != -1) return cutter(string, paragraphIndex);
        else if (commaIndex != -1) return cutter(string, commaIndex);
        else return string;
    }


    private static String checkContact(String string) {
        String checkContact = string.substring(9);
        return checkContact;  //except "Contact: "
    }

    private void telegramTransfer(SpannableString content, final String dialogText, final String telegramLink) {
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        organizer.setText(content);
        organizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TelegramOpenDialog();
                Bundle bundle = new Bundle();
                bundle.putString("dialogText", dialogText);
                bundle.putString("dialogUrl", telegramLink);
                newFragment.setArguments(bundle);
                newFragment.show(getActivity().getSupportFragmentManager(), "Telegram");
            }
        });
    }


    private void initializeMap(final String latitude, final String longitude) {
        mSupportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapDesc);
        if (mSupportMapFragment == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mSupportMapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.mapWrapper, mSupportMapFragment).commit();
        }
        if (mSupportMapFragment != null) {
            mSupportMapFragment.getMapAsync(new OnMapReadyCallback() {

                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;
                    mMap.getUiSettings().setMapToolbarEnabled(false);
                    mSettings = mMap.getUiSettings();
                    mSettings.setMyLocationButtonEnabled(false);
                    mMap.setMyLocationEnabled(true);
                    if (latitude != null && longitude != null) {
                        LatLng position = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
                        mMap.addMarker(new MarkerOptions().position(position).title(summary));
                    }

                    mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                        @Override
                        public void onInfoWindowClick(Marker marker) {
                            DialogFragment newFragment = new MapFragmentAskForRouteDialog();
                            Bundle bundle = new Bundle();
                            bundle.putString("summary", summary);
                            newFragment.setArguments(bundle);
                            newFragment.show(getActivity().getSupportFragmentManager(), "FindRoute");

                        }
                    });
                }
            });
        }
    }


}
