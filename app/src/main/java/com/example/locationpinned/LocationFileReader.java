package com.example.locationpinned;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;

public class LocationFileReader {

    public static void readLocationsFromFile(Context context, MyDatabaseHelper dbHelper) {
        try {
            // Open the text file for reading using the appropriate resource identifier
            InputStream inputStream = context.getResources().openRawResource(R.raw.geocoding);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Split the line into latitude and longitude
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    double latitude = Double.parseDouble(parts[0]);
                    double longitude = Double.parseDouble(parts[1]);

                    // Now, you can pass the latitude and longitude to your Geocoder method
                    String address = getCompleteAddress(context, latitude, longitude);
                    Log.d("Address", address);

                    // Insert the data into the database
                    insertLocationIntoDatabase(dbHelper, address, latitude, longitude);
                }
            }

            // Close the reader
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void insertLocationIntoDatabase(MyDatabaseHelper dbHelper, String address, double latitude, double longitude) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("latitude", latitude);
        values.put("longitude", longitude);
        values.put("address", address);

        long newRowId = db.insert("location", null, values);

        if (newRowId == -1) {
            // Insertion failed
            Log.e("DatabaseInsert", "Error inserting data into the database");
        } else {
            // Insertion successful
            Log.i("DatabaseInsert", "Data inserted successfully with row ID: " + newRowId);
        }

        db.close();
    }

    private static String getCompleteAddress(Context context, double latitude, double longitude) {
        String address = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
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
}
