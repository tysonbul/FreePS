package com.example.tyson.freeps;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ValueEventListener;

public class PostDetails extends AppCompatActivity {
    Long PostCounter = 0L;
    Button submitButton;
    Button cancelButton;
    EditText title;
    EditText description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        // get view fields
        submitButton = (Button) findViewById(R.id.submitButton);
        title = (EditText) findViewById(R.id.title);
        description = (EditText) findViewById(R.id.description);


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


}
