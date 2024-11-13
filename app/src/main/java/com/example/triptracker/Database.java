package com.example.triptracker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class Database {
    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://buddycartracker-default-rtdb.europe-west1.firebasedatabase.app/");
    private FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference userRef = database.getReference("users").child(currUser.getUid());

    public void initDatabase(final CallbackDB innitCB){;
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    userRef.child("Name").setValue(currUser.getDisplayName());
                }
                else{
                }
                innitCB.onInit();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                LogWriter.writeError("[DB]", error.toString());
            }
        });
    }

    public void getUserTrips(final CallbackDB callback){
        ArrayList<String> keys = new ArrayList<>();
        ArrayList<Trip> tripsDB = new ArrayList<>();
        DatabaseReference keysRef = database.getReference("users").child(currUser.getUid()).child("trips");
        keysRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    keys.add(ds.getKey());
                }
                for(String tripKey: keys){
                    DatabaseReference tripRef = database.getReference("users").child(currUser.getUid()).child("trips").child(tripKey);
                    tripRef.addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Trip trip = dataSnapshot.getValue(Trip.class);
                            tripsDB.add(trip);
                            if(tripsDB.size() == keys.size()){
                                callback.onSuccessTrips(tripsDB);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Failed to read value
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
    }
    public void addTrip(Trip trip){
        userRef.child("trips").child(String.format(Locale.ENGLISH,"%f%f%f%f", trip.getStartLongitude(),trip.getStartLatitude(),
                trip.getEndLongitude(),trip.getEndLatitude()).replace(".","")).setValue(trip);
    }

    public void removeTrip(Trip trip){
        userRef.child("trips").child(String.format(Locale.ENGLISH,"%f%f%f%f", trip.getStartLongitude(),trip.getStartLatitude(),
                trip.getEndLongitude(),trip.getEndLatitude()).replace(".","")).removeValue();
    }

    public void updateTripPosition(Trip trip){
        userRef.child("trips").child(String.format(Locale.ENGLISH,"%f%f%f%f", trip.getStartLongitude(),trip.getStartLatitude(),
                trip.getEndLongitude(),trip.getEndLatitude()).replace(".","")).child("position").setValue(trip.getPosition());
    }

}
