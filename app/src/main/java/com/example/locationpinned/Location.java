package com.example.locationpinned;

public class Location {
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private double latitude;
    private double longitude;
    private String address;
    private int id;

    public Location(String address, double latitude, double longitude, int id){
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.id = id;
    }
}
