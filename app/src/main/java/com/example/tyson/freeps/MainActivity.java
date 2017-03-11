package com.example.tyson.freeps;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    int item;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context context = getApplicationContext();
        SharedPreferences item_interest = context.getSharedPreferences("iteminterest", MODE_PRIVATE);
        SharedPreferences.Editor edit = item_interest.edit();

        edit.clear();
        edit.putInt("Test", 1234);
        edit.commit();
        Toast.makeText(context, "Item 1234 should be saved", Toast.LENGTH_SHORT).show();

        Button b = (Button)findViewById(R.id.testbutton);
        item = item_interest.getInt("Test", 1234);
        b.setOnClickListener(new View.OnClickListener() {
             public void onClick(View view) {
                 Toast.makeText(getBaseContext(), Integer.toString(item), Toast.LENGTH_SHORT).show();
             }
        });

//        int item = item_interest.getInt("Test", 1234);
//        Toast.makeText(context, item, Toast.LENGTH_SHORT).show();
    }
}
