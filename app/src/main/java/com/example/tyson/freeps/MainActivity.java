package com.example.tyson.freeps;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private DatabaseReference mPostReference;
    String item = "hello";

    public static class Post {

        public String postid;

        public Post(String postid) {
            // ...
        }

    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context context = getApplicationContext();
        SharedPreferences item_interest = context.getSharedPreferences("iteminterest", MODE_PRIVATE);
        SharedPreferences.Editor edit = item_interest.edit();

        edit.clear();
        edit.putString("Test", "");
        edit.commit();
        Toast.makeText(context, "Item should be saved", Toast.LENGTH_SHORT).show();

        Button b = (Button)findViewById(R.id.testbutton);
        item = item_interest.getString("Test", "");
        b.setOnClickListener(new View.OnClickListener() {
             public void onClick(View view) {
                 Toast.makeText(getBaseContext(), item, Toast.LENGTH_SHORT).show();
             }
        });

    }

//    public void onStart() {
//        super.onStart();
//
//        // Add value event listener to the post
//        // [START post_value_event_listener]
//
//        ValueEventListener postListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // Get Post object and use the values to update the UI
//
//                Toast.makeText(getBaseContext(), item, Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Getting Post failed, log a message
//                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
//            }
//        };
//        mPostReference.addValueEventListener(postListener);
//    }
}
