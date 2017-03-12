package com.example.tyson.freeps;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.security.Permission;

public class PostDetails extends AppCompatActivity {
    Long PostCounter = 0L;
    Button submitButton;
    Button cancelButton;
    Button uploadButton;
    ImageView imageView;
    EditText title;
    EditText description;

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private StorageReference mStorage;
    private ProgressDialog mProgress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        // get view fields
        submitButton = (Button) findViewById(R.id.submitButton);
        title = (EditText) findViewById(R.id.title);
        description = (EditText) findViewById(R.id.description);
        uploadButton = (Button) findViewById(R.id.uploadButton);
        imageView = (ImageView) findViewById(R.id.imageView);

        mStorage = FirebaseStorage.getInstance().getReference("Posts");
        mProgress = new ProgressDialog(this);


        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(PostDetails.this, android.Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(PostDetails.this,
                            android.Manifest.permission.CAMERA)) {

                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.

                    } else {

                        // No explanation needed, we can request the permission.

                        ActivityCompat.requestPermissions(PostDetails.this,
                                new String[]{android.Manifest.permission.CAMERA},
                                101);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                }
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    Log.d("Failure: ","false");
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                }

            }
        });


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get an instance of Firebase
                FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference myRef = db.getReference("Posts");
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        PostCounter = dataSnapshot.getChildrenCount();
                        Log.d("postCount", String.valueOf(PostCounter));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                PostCounter++;
                Log.d("Count", String.valueOf(PostCounter));

                myRef.child(String.valueOf(PostCounter)).child("PostID").setValue(String.valueOf(PostCounter));
                myRef.child(String.valueOf(PostCounter)).child("Title").setValue(title.getText().toString());
                myRef.child(String.valueOf(PostCounter)).child("Description").setValue(description.getText().toString());
                myRef.child(String.valueOf(PostCounter)).child("ItemCategory").setValue("TBD");
                myRef.child(String.valueOf(PostCounter)).child("LocationLon").setValue("TBD");
                myRef.child(String.valueOf(PostCounter)).child("LocationLat").setValue("TBD");
                myRef.child(String.valueOf(PostCounter)).child("Time").setValue("TBD");
                myRef.child(String.valueOf(PostCounter)).child("Date").setValue("TBD");
                myRef.child(String.valueOf(PostCounter)).child("Photo").setValue("TBD");
                myRef.child(String.valueOf(PostCounter)).child("ClaimFlag").setValue("false");
                myRef.child(String.valueOf(PostCounter)).child("notThereFlag").setValue("false");

            }
        });



    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK){
//            mProgress.setMessage("Uploading image...");
//            mProgress.show();
//            Uri uri = data.getData();
//
//            StorageReference filepath = mStorage.child(String.valueOf(PostCounter)).child("Photo").child(uri.getLastPathSegment());
//            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    mProgress.dismiss();
//                    Toast.makeText(PostDetails.this,"Upload finish",Toast.LENGTH_LONG).show();
//                }
//            });
//
//        }
//    }
}
