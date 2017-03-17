package com.example.tyson.freeps;

import android.*;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.tyson.freeps.R.id.imageView;

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
    private String ItemCategory;
    private String ClaimFlag;
    private String notThereFlag;
    private FirebaseDatabase db;
    private DatabaseReference myRef;

    private Button uploadButton;
    private ImageView imageView;
    private StorageReference storage;
    private ProgressDialog progress;
    public Uri photoURI;

    private static final int CAMERA_REQUEST_CODE = 1;

    String mCurrentPhotoPath;
    String pathToPic;
    Boolean camPermission;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    public boolean hasPermissionInManifest(Context context, String permissionName) {
        final String packageName = context.getPackageName();
        try {
            final PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            final String[] declaredPermisisons = packageInfo.requestedPermissions;
            if (declaredPermisisons != null && declaredPermisisons.length > 0) {
                for (String p : declaredPermisisons) {
                    if (p.equals(permissionName)) {
                        return true;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {

        }
        return false;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        if (savedInstanceState != null)
        {
            photoURI = savedInstanceState.getParcelable("photoURI");
        }

        storage = FirebaseStorage.getInstance().getReference();

        uploadButton = (Button) findViewById(R.id.upload);
        imageView = (ImageView) findViewById(R.id.imageView);

        progress = new ProgressDialog(this);

        Context context = getApplicationContext();



        camPermission = hasPermissionInManifest(context, "android.permission.CAMERA");
        Log.d("Permission", Boolean.toString(camPermission));
        uploadButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent, CAMERA_REQUEST_CODE);

                if(camPermission == true)
                    dispatchTakePictureIntent(savedInstanceState);
            }
        });

        buildGoogleApiClient();

        if(mGoogleApiClient!= null){
            mGoogleApiClient.connect();
        }
        else
            Toast.makeText(this, "Not connected...", Toast.LENGTH_SHORT).show();


        String itemCat[] = {"Furniture","Electronics","Books","Textbooks","Sports Gear","Clothing","Accessories","Other"};
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, itemCat);
        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(spinnerArrayAdapter);


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
                String Image = pathToPic;
                String Title = inputTitle.getText().toString();
                String Description = inputDescription.getText().toString();
                TimeAndDate = DateFormat.getDateTimeInstance().format(new Date());
                ItemCategory =  spinner.getSelectedItem().toString();
                ClaimFlag = "f";
                notThereFlag = "f";


                // Check for already existed PostID
                if (TextUtils.isEmpty(PostID)) {
                    createPost(Image, Title, Description, LocationLat, LocationLon, TimeAndDate, ItemCategory, ClaimFlag, notThereFlag);
                } else {
                    updatePost(Image, Title, Description, LocationLat, LocationLon, TimeAndDate, ItemCategory, ClaimFlag, notThereFlag);
                }
            }
        });

    }

    private void createPost(String Image, String Title, String Description, String LocationLat, String LocationLon, String TimeAndDate, String ItemCategory, String ClaimFlag, String notThereFlag) {
        // TODO
        // In real apps this PostID should be fetched
        // by implementing firebase auth

        if (TextUtils.isEmpty(PostID)) {
            PostID = myRef.push().getKey();
            Log.d("Key Value", PostID);
        }

        Post Post = new Post(Image, Title, Description, LocationLat, LocationLon, TimeAndDate, ItemCategory, ClaimFlag, notThereFlag);

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

    private void updatePost(String Image, String Title, String Description, String LocationLat, String LocationLon, String TimeAndDate, String ItemCategory, String ClaimFlag, String notThereFlag) {
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

    //FROM MAIN
    private void dispatchTakePictureIntent(Bundle savedInstanceState) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File...
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this, "com.example.android.fileprovider", photoFile);
                Log.d("Photo URI", photoURI.toString());

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("requestCode value", Integer.toString(requestCode));
        if(requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK){
            progress.setMessage("Uploading...");
            progress.show();

            StorageReference filepath = storage.child("Photos").child(photoURI.getLastPathSegment());
            Log.d("File Path", filepath.toString());
            pathToPic = filepath.toString();
            downloadImage();
            filepath.putFile(photoURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(PostDetails.this, "Upload Successful!", Toast.LENGTH_SHORT).show();
                    progress.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostDetails.this, "Upload Failed!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void downloadImage() {
//        StorageReference picRef = storage.child("Photos/JPEG_20170312_044159_666091275.jpg");
//        StorageReference picRef = storage.getReferenceFromUrl(pathToPic);
        String[] parts = pathToPic.split(".com/");
        String part2 = parts[1];
        Log.d("Image name", part2);

        StorageReference picRef = storage.child(part2);
        // ImageView in your Activity


        // Load the image using Glide
        Glide.with(this /* context */)
                .using(new FirebaseImageLoader())
                .load(picRef)
                .into(imageView);
    }

    protected void onSaveInstanceState(Bundle outState)
    {
        outState.putParcelable("photoURI", photoURI);
    }
}
