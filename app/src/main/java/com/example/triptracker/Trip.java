package com.example.triptracker;

public class Trip {
    private double avgFuel;
    private String avgSpeed;
    private double distance;
    private String duration;
    private double fuelPrice;
    private double tripCost;
    private int position;

    private double startLongitude;
    private double startLatitude;
    private double endLongitude;
    private double endLatitude;

    public Trip(double avgFuel, String avgSpeed, double distance, String duration, double fuelPrice) {

        this.avgFuel = avgFuel;
        this.avgSpeed = avgSpeed;
        this.distance = distance;
        this.duration = duration;
        this.fuelPrice = fuelPrice;

        tripCost = ((avgFuel*distance)/100)*fuelPrice;

        this.position = MainActivity.adapterTrips.getItemCount();
    }
    public Trip(){}

    public double getAvgFuel() {
        return avgFuel;
    }

    public String getAvgSpeed() {
        return avgSpeed;
    }

    public double getDistance() {
        return distance;
    }

    public String getDuration() {
        return duration;
    }

    public double getFuelPrice() {
        return fuelPrice;
    }

    public double getTripCost() {
        return tripCost;
    }

    public int getPosition() {
        return position;
    }

    public void setAvgFuel(double avgFuel) {
        this.avgFuel = avgFuel;
    }

    public void setAvgSpeed(String avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setFuelPrice(double fuelPrice) {
        this.fuelPrice = fuelPrice;
    }

    public void setTripCost(double tripCost) {
        this.tripCost = tripCost;
    }
    public void setPosition(int position) {
        this.position = position;
    }


    public double getStartLongitude() {
        return startLongitude;
    }

    public void setStartLongitude(double startLongitude) {
        this.startLongitude = startLongitude;
    }

    public double getStartLatitude() {
        return startLatitude;
    }

    public void setStartLatitude(double startLatitude) {
        this.startLatitude = startLatitude;
    }

    public double getEndLongitude() {
        return endLongitude;
    }

    public void setEndLongitude(double endLongitude) {
        this.endLongitude = endLongitude;
    }

    public double getEndLatitude() {
        return endLatitude;
    }

    public void setEndLatitude(double endLatitude) {
        this.endLatitude = endLatitude;
    }
}
