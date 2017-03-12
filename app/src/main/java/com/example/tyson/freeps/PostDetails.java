package com.example.tyson.freeps;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ValueEventListener;

public class PostDetails extends AppCompatActivity {

    private static final String TAG = PostDetails.class.getSimpleName();

    Long PostCounter = 0L;
    Button submitButton;
    Button cancelButton;
    EditText inputTitle;
    EditText inputDescription;
    String PostKey;

    private TextView PostDetails;
    private String PostID;
    private FirebaseDatabase db;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

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

                // Check for already existed PostID
                if (TextUtils.isEmpty(PostID)) {
                    createPost(Title, Description);
                } else {
                    updatePost(Title, Description);
                }
            }
        });

    }

    private void createPost(String Title, String Description) {
        // TODO
        // In real apps this PostID should be fetched
        // by implementing firebase auth

        if (TextUtils.isEmpty(PostID)) {
            PostID = myRef.push().getKey();
            Log.d("Key Value", PostID);
        }

        Post Post = new Post(Title, Description);

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

    private void updatePost(String Title, String Description) {
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
}
