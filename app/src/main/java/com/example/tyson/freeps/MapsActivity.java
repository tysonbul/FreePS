package com.example.tyson.freeps;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * This shows how to listen to some {@link GoogleMap} events.
 */
public class MapsActivity extends FragmentActivity
        implements OnMapClickListener, OnMapLongClickListener, OnCameraIdleListener,
        OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    Marker marker;
    Button hostButton, home;
    LatLng hostPoint;
    ImageButton helpButton, postButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //hostButton = (Button) findViewById(R.id.hostButton);
        home = (Button) findViewById(R.id.home);
        helpButton = (ImageButton) findViewById(R.id.help_button);
        postButton = (ImageButton) findViewById(R.id.post_or_cancel_button);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//        hostButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Log.d("hi","button clicked");
//                //Intent i = new Intent(MapsActivity.this, EventDetails.class);
//                //i.putExtra("point",hostPoint);
//                //startActivity(i);
//            }
//        });

//        home.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Intent i = new Intent(MapsActivity.this,UserAreaActivity.class);
//                //startActivity(i);
//            }
//        });

        helpButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                DialogFragment newFragment = new HelpDialogFragment();
                newFragment.show(getFragmentManager(), "helpDialog");
            }
        });

        postButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), AddPostActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(49.234221, -122.8145), 9));

        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnCameraIdleListener(this);

        // Get Items from database
        // For each item
            // Make a map marker with respective location and catagory icon (couch or what ever)
            // Set marker options
            // Set marker tag to a value
            // Add {value: item info} to dictionary
    }

    @Override
    public void onMapClick(LatLng point) {

        if (marker != null) {
            marker.remove();
        }
        marker = mMap.addMarker(new MarkerOptions().position(point));
        hostPoint = point;
    }

    @Override
    public void onMapLongClick(LatLng point) {

        if (marker != null) {
            marker.remove();
        }
        marker = mMap.addMarker(new MarkerOptions().position(point));
        hostPoint = point;

    }

    /** Called when the user clicks a marker. */
    @Override
    public boolean onMarkerClick(final Marker marker) {

        // Retrieve the data from the marker.
        Integer clickCount = (Integer) marker.getTag();

        // Get the markers respective item info from the dictionary

        // Call showItemInfoDialog(ItemInfo)


        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    @Override
    public void onCameraIdle() {
    }

    /*
        Call to show dialog for item description
     */
    public void showItemInfoDialog() {
        DialogFragment newFragment = new ItemInformationDialogFragment();
        newFragment.show(getFragmentManager(), "missles");
    }


}

