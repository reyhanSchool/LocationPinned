package com.example.locationpinned;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;

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
        int id = intent.getIntExtra("id", -1);

        Button deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get the note id
                if (intent != null) {
                    if (id > -1) {
                        // delete note
                        deleteNoteFromDatabase(id);
                    }
                }
                Intent intent = new Intent(CreateLocation.this, MainActivity.class);
                startActivity(intent);
            }
        });

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
                        // Retrieve the id

                        // Update the existing location in the database
                        deleteNoteFromDatabase(id);
                        insertIntoDatabase(Double.valueOf(latitude), Double.valueOf(longitude));
                    } else {
                        // Insert a new location into the database
                        insertIntoDatabase(Double.valueOf(latitude), Double.valueOf(longitude));
                    }
                }

                    // Start the new activity
                Intent intent = new Intent(CreateLocation.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
    public void insertIntoDatabase(double latitude, double longitude){
        // input into database
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(CreateLocation.this); // Use your database helper class
        SQLiteDatabase db = dbHelper.getWritableDatabase(); // Open the database

        ContentValues values = new ContentValues();
        values.put("latitude", latitude);
        values.put("longitude", longitude);
        // get address
        values.put("address", getCompleteAddress(Double.valueOf(latitude), Double.valueOf(longitude)));

        long newRowId = db.insert("location", null, values);

        if (newRowId == -1) {
            // Insertion failed
            Log.e("DatabaseInsert", "Error inserting data into the database");
        } else {
            // Insertion successful
            Log.i("DatabaseInsert", "Data inserted successfully with row ID: " + newRowId);
        }

        db.close(); // Close the database

    }
    private String getCompleteAddress(double latitude, double longitude) {
        String address = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strToReturn = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strToReturn.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                address = strToReturn.toString();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return address;

    }
    private void deleteNoteFromDatabase(int noteId) {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(CreateLocation.this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int rowsAffected = db.delete(
                "location",
                "id = ?",
                new String[]{String.valueOf(noteId)}
        );

        db.close();

        if (rowsAffected > 0) {
            // The note has been deleted successfully
            Log.i("DeleteLocation", "Location deleted successfully");
        } else {
            // No rows were deleted; the note might not exist
            Log.e("DeleteLocation", "Error deleting the location");
        }
    }
}
