package com.example.tyson.freeps;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

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

    protected void onCreate(final Bundle savedInstanceState) {
        String item;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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



//        SharedPreferences item_interest = context.getSharedPreferences("iteminterest", MODE_PRIVATE);
//        SharedPreferences.Editor edit = item_interest.edit();
//
//        edit.clear();
//        edit.putString("Test", "");
//        edit.commit();
//        Toast.makeText(context, "Item should be saved", Toast.LENGTH_SHORT).show();
//
//        Button b = (Button)findViewById(R.id.testbutton);
//        item = item_interest.getString("Test", "");
//        b.setOnClickListener(new View.OnClickListener() {
//             public void onClick(View view) {
//                 Toast.makeText(getBaseContext(), item, Toast.LENGTH_SHORT).show();
//             }
//        });

    }

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
            filepath.putFile(photoURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(MainActivity.this, "Upload Successful!", Toast.LENGTH_SHORT).show();
                    progress.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "Upload Failed!", Toast.LENGTH_SHORT).show();
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
