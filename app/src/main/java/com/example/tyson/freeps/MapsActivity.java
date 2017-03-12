package com.example.tyson.freeps;

import android.Manifest;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


/**
 * This shows how to listen to some {@link GoogleMap} events.
 */
public class MapsActivity extends FragmentActivity
        implements AdapterView.OnItemSelectedListener, OnCameraIdleListener,
        OnMapReadyCallback, OnMarkerClickListener,
        ConnectionCallbacks,
        OnConnectionFailedListener, LocationListener  {

    private GoogleMap mMap;
    Marker marker;
    Button hostButton, home;
    LatLng hostPoint;
    ImageButton helpButton, postButton;
    ImageButton filterBarButton;

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;

    Long PostCounter;
    Double[] tempLocationLat;
    Double[] tempLocationLon;
    String[] tempTitle;
    String[] tempInfo;
    String[] tempTimeAndDate;
    String[] tempDescription;
    String[] tempClaimFlag;
    String[] tempNotThereFlag;
    String[] tempItemCat;

    Post p;
    String locationLat;
    String locationLon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        //hostButton = (Button) findViewById(R.id.hostButton);
        //hostButton = (Button) findViewById(R.id.hostButton);
        home = (Button) findViewById(R.id.home);
        helpButton = (ImageButton) findViewById(R.id.help_button);
        postButton = (ImageButton) findViewById(R.id.post_or_cancel_button);

        filterBarButton = (ImageButton) findViewById(R.id.filter_bar_button);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        helpButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                DialogFragment newFragment = new HelpDialogFragment();
                newFragment.show(getFragmentManager(), "helpDialog");
            }
        });
        FirebaseDatabase myRef = FirebaseDatabase.getInstance();
        Query q = myRef.getReference();
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                PostCounter = dataSnapshot.child("Posts").getChildrenCount();

                tempLocationLat = new Double[PostCounter.intValue()];
                tempLocationLon = new Double[PostCounter.intValue()];
                tempTitle = new String[PostCounter.intValue()];
                tempItemCat = new String[PostCounter.intValue()];
                tempTimeAndDate = new String[PostCounter.intValue()];
                tempDescription = new String[PostCounter.intValue()];
                tempClaimFlag = new String[PostCounter.intValue()];
                tempNotThereFlag = new String[PostCounter.intValue()];

                Log.d("location",String.valueOf(tempLocationLat.length));
                Integer i=0;

                for(DataSnapshot post : dataSnapshot.child("Posts").getChildren()){
                    p = post.getValue(Post.class);
                    locationLat = p.getLocationLat();
                    locationLon = p.getLocationLon();

                    //Log.d("locationLat",locationLat);
                    //Log.d("locationLon",locationLon);


                    tempLocationLat[i] = Double.parseDouble(locationLat);
                    tempLocationLon[i] = Double.parseDouble(locationLon);
                    tempTitle[i] = p.getTitle();
                    tempItemCat[i] = p.getItemCategory();
                    tempTimeAndDate[i] = p.getTimeAndDate();
                    tempDescription[i] = p.getDescription();
                    tempNotThereFlag[i] = p.getNotThereFlag();
                    tempClaimFlag[i] = p.getClaimFlag();
                    Log.d("title",tempTitle[i]);
                    i++;

                }

                for (Integer ii=0; ii < i; ii++){
                    /////////////////////////////////////////
                    // tempItemCat[i] returns itemCategory //
                    /////////////////////////////////////////
                    mMap.addMarker(new MarkerOptions().position(new LatLng(tempLocationLat[ii],tempLocationLon[ii])).title(tempTitle[ii]));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        postButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), AddPostActivity.class);
                startActivity(intent);
            }
        });

        filterBarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new FilterOptionDialogFragment();
                newFragment.show(getFragmentManager(), "filterDialog");

                Spinner spinner = (Spinner) findViewById(R.id.category_spinner);
                //spinner.setOnItemSelectedListener(this);
                // Create an ArrayAdapter using the string array and a default spinner layout
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                        R.array.category_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
                spinner.setAdapter(adapter);
            }
        });


    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(49.234221, -122.8145), 9));


        mMap.setOnCameraIdleListener(this);

        //mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        // Get Items from database
        // For each item
            // Make a map marker with respective location and catagory icon (couch or what ever)
            // Set marker options
                // Icon for respective type of item
            // Set marker tag to a value
            // Add {value: item info} to dictionary
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
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


    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission was granted.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            //You can add here other case statements according to your requirement.
        }
    }
}

