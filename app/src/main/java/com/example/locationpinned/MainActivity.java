package com.example.locationpinned;

import static com.example.locationpinned.LocationFileReader.readLocationsFromFile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, LocationAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private MyDatabaseHelper dbHelper;
    private LocationAdapter locationAdapter;
    private List<Location> locations;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(this);

        FloatingActionButton fab = findViewById(R.id.floatingActionButtonAddLocation);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the new activity
                Intent intent = new Intent(MainActivity.this, CreateLocation.class);
                startActivity(intent);
            }
        });

        dbHelper = new MyDatabaseHelper(this);
        // readLocationsFromFile(MainActivity.this, dbHelper);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Read data from the database
        locations = readLocationsFromDatabase();
        locationAdapter = new LocationAdapter(locations);
        recyclerView.setAdapter(locationAdapter);
        locationAdapter.setOnItemClickListener(this);

        // String address = reverseGeocode(this, latitude, longitude); // Replace 'this' with your context
    }
    @Override
    public void onItemClick(int position) {
        // Handle the item click here
        Location clickedNote = locations.get(position);
//
//        // Start the new activity and pass the note data
        Intent intent = new Intent(MainActivity.this, CreateLocation.class);
        intent.putExtra("id", clickedNote.getId());
        startActivity(intent);
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        // This method is called when the user submits the search query.
        // You can implement your search logic here, if needed.
        if (query.isEmpty()){
            locations = readLocationsFromDatabase();
        }
        locations = filterLocations(query);
        renderLocations(locations);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // This method is called when the text in the search view changes (e.g., as the user types).
        if (newText.isEmpty()) {
            locations = readLocationsFromDatabase();
        }
        locations = filterLocations(newText);
        renderLocations(locations);
        return true;
    }
    private List<Location> filterLocations(String searchText) {
        List<Location> filteredNotes = new ArrayList<>();
        for (Location location : locations) {
            // You can customize the filtering criteria based on your requirements.
            // Here, we check if the title contains the search text (case-insensitive).
            if (location.getAddress().toLowerCase().contains(searchText.toLowerCase())) {
                filteredNotes.add(location);
            }
        }
        return filteredNotes;
    }

    public void renderLocations(List<Location> locations){
        // Read data from the database
        locationAdapter.updateData(locations);
        locationAdapter.notifyDataSetChanged();
    }

    private List<Location> readLocationsFromDatabase() {
        List<Location> locations = new ArrayList<>();

        // Get a readable database
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Specify the table name you want to check
        String tableName = "location";

        // Check if the table exists
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?", new String[]{tableName});

        if (cursor != null && cursor.moveToFirst()) {
            // The table exists, proceed to read data
            cursor.close();

            // Query the database to retrieve notes
            Cursor cursor2 = db.query(
                    tableName, // The table to query
                    new String[]{"id","address", "longitude", "latitude"}, // The columns to retrieve
                    null, // The columns for the WHERE clause (null here means all rows)
                    null, // The values for the WHERE clause
                    null, // Don't group the rows
                    null, // Don't filter by row groups
                    null  // The sort order (null for default)
            );

            // Iterate through the cursor to retrieve notes
            if (cursor2 != null) {
                while (cursor2.moveToNext()) {
                    String address = cursor2.getString(cursor2.getColumnIndexOrThrow("address"));
                    double latitude = cursor2.getDouble(cursor2.getColumnIndexOrThrow("latitude"));
                    double longitude = cursor2.getDouble(cursor2.getColumnIndexOrThrow("longitude"));
                    int id = cursor2.getInt(cursor2.getColumnIndexOrThrow("id"));

                    // Create a Note object and add it to the list
                    Location location = new Location(address, latitude, longitude, id);
                    locations.add(location);
                }

                cursor2.close();
            }
        }
        if (locations.isEmpty()){
            // Set the visibility of textView3
            TextView noLocationsFoundText = findViewById(R.id.noLocationsFoundText);
            noLocationsFoundText.setVisibility(View.VISIBLE);
        }

        db.close();

        return locations;
    }


}
