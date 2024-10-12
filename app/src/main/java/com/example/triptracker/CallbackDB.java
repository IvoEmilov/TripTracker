package com.example.triptracker;

import java.util.ArrayList;

public interface CallbackDB {
    void onInit();
    void onSuccessTrips(ArrayList<Trip> TripsDB);

}


