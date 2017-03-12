package com.example.tyson.freeps;

import android.*;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

public class PostDetails extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;

    private static final String TAG = PostDetails.class.getSimpleName();

    Long PostCounter = 0L;
    Button submitButton;
    Button cancelButton;
    EditText inputTitle;
    EditText inputDescription;
    String PostKey;

    private TextView PostDetails;
    private String PostID;
    private String LocationLat;
    private String LocationLon;
    private String TimeAndDate;
    private FirebaseDatabase db;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        buildGoogleApiClient();

        if(mGoogleApiClient!= null){
            mGoogleApiClient.connect();
        }
        else
            Toast.makeText(this, "Not connected...", Toast.LENGTH_SHORT).show();

        // get view fields
        PostDetails = (TextView) findViewById(R.id.post_details);
        submitButton = (Button) findViewById(R.id.submitButton);
        inputTitle = (EditText) findViewById(R.id.title);
        inputDescription = (EditText) findViewById(R.id.description);

        db = FirebaseDatabase.getInstance();
        myRef = db.getReference("Posts");

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Title = inputTitle.getText().toString();
                String Description = inputDescription.getText().toString();
                TimeAndDate = DateFormat.getDateTimeInstance().format(new Date());


                // Check for already existed PostID
                if (TextUtils.isEmpty(PostID)) {
                    createPost(Title, Description, LocationLat, LocationLon, TimeAndDate);
                } else {
                    updatePost(Title, Description, LocationLat, LocationLon, TimeAndDate);
                }
            }
        });

    }

    private void createPost(String Title, String Description, String LocationLat, String LocationLon, String TimeAndDate) {
        // TODO
        // In real apps this PostID should be fetched
        // by implementing firebase auth

        if (TextUtils.isEmpty(PostID)) {
            PostID = myRef.push().getKey();
            Log.d("Key Value", PostID);
        }

        Post Post = new Post(Title, Description, LocationLat, LocationLon, TimeAndDate);

        myRef.child(PostID).setValue(Post);
        
        addPostChangeListener();
    }

    private void addPostChangeListener() {
        // Posts data change listener
        myRef.child(PostID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post Post= dataSnapshot.getValue(Post.class);

                // Check for null
                if (Post == null) {
                    Log.e(TAG, "Post data is null!");
                    return;
                }

                Log.e(TAG, "Post data is changed!" + Post.Title + ", " + Post.Description);

                // Display newly updated name and email
                PostDetails.setText(Post.Title + ", " + Post.Description);

                // clear edit text
                inputTitle.setText("");
                inputDescription.setText("");

                toggleButton();

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read post", error.toException());
            }
        });
    }
    private void toggleButton() {
        if (TextUtils.isEmpty(PostID)) {
            submitButton.setText("Post");
        } else {
            submitButton.setText("Update");
        }

    }

    private void updatePost(String Title, String Description, String LocationLat, String LocationLon, String TimeAndDate) {
        if (!TextUtils.isEmpty(Title))
            myRef.child(PostID).child("Title").setValue(Title);
        if (!TextUtils.isEmpty(Description))
            myRef.child(PostID).child("Description").setValue(Description);
    }

    private void readPost(){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot.getValue());
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Failed to read post", error.toException());
            }
        });
    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        Toast.makeText(this, "Failed to connect...", Toast.LENGTH_SHORT).show();

    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onConnected(Bundle arg0) {

        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mLastLocation != null) {
            Toast.makeText(this,String.valueOf(mLastLocation.getLatitude()),Toast.LENGTH_LONG).show();
            LocationLat = String.valueOf(mLastLocation.getLatitude());
            LocationLon = String.valueOf(mLastLocation.getLongitude());
        }

    }

    @Override
    public void onConnectionSuspended(int arg0) {
        Toast.makeText(this, "Connection suspended...", Toast.LENGTH_SHORT).show();

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
}
