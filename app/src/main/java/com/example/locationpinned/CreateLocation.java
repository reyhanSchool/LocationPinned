package com.example.locationpinned;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;

public class CreateLocation extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_location);

        ImageButton fab = findViewById(R.id.imageButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the new activity
                Intent intent = new Intent(CreateLocation.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();

        Button createLocationButton = findViewById(R.id.createLocationButton);
        createLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("hi", "this is running");
                // take inputs from forms
                // Find the EditText widgets by their IDs
                EditText latitudeEditText = findViewById(R.id.editLatitude);
                EditText longitudeEditText = findViewById(R.id.editLongitude);

                // Extract the text entered by the user
                String latitude = latitudeEditText.getText().toString();
                String longitude = longitudeEditText.getText().toString();

                // throw error if title is empty
                if (latitude.isEmpty() || longitude.isEmpty()) {
                    // Set the visibility of error text to visible
                    TextView errorMessage = findViewById(R.id.ErrorMessage);
                    errorMessage.setVisibility(View.VISIBLE);
                } else {
                    // Get the note's key from the intent
                    int id = intent.getIntExtra("id", -1);

                    if (id > -1) {
//                        // Retrieve the id
//                        ImageView imageView = findViewById(R.id.selectedImageView);
//                        Bitmap updatedImage = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
//
//                        // Update the existing note in the database
//                        deleteNoteFromDatabase(key);
//                        updateNoteInDatabase(key, title, content, updatedImage, selectedColor);
//
//                        // Optionally, you can navigate back to the main activity or do something else
//                        Intent backToMainIntent = new Intent(create_note.this, MainActivity.class);
//                        startActivity(backToMainIntent);
                    }
                }

                // input into database
                MyDatabaseHelper dbHelper = new MyDatabaseHelper(CreateLocation.this); // Use your database helper class
                SQLiteDatabase db = dbHelper.getWritableDatabase(); // Open the database

                ContentValues values = new ContentValues();
                values.put("latitude", latitude);
                values.put("longitude", longitude);
                // get address
                values.put("address", MainActivity.reverseGeocode(CreateLocation.this, Double.valueOf(latitude), Double.valueOf(longitude)));

                long newRowId = db.insert("location", null, values);

                if (newRowId == -1) {
                    // Insertion failed
                    Log.e("DatabaseInsert", "Error inserting data into the database");
                } else {
                    // Insertion successful
                    Log.i("DatabaseInsert", "Data inserted successfully with row ID: " + newRowId);
                }

                db.close(); // Close the database

                // Start the new activity
                Intent intent = new Intent(CreateLocation.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

}
