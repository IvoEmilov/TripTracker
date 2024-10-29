package com.example.triptracker;

import static com.mapbox.maps.plugin.locationcomponent.LocationComponentUtils.getLocationComponent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.extension.style.layers.properties.generated.TextAnchor;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImplKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin;
import com.mapbox.navigation.ui.maps.location.NavigationLocationProvider;

import java.text.DecimalFormat;
import java.util.List;

public class AdapterTrips extends RecyclerView.Adapter<AdapterTrips.ViewHolder> implements ItemTouchHelperAdapter {
    private List<Trip> tripsList;
    private Context context;
    private ItemTouchHelper itemTouchHelper;
    private int layout_resource;
    private MainActivity activity;
    private DecimalFormat df = new DecimalFormat("#0.00");
    private Database db;


    @Override
    public void onItemMove(int fromPosition, int toPosition) {

    }

    @Override
    public void onItemSwiped(int position) {
        db.removeTrip(MainActivity.trips.get(position));
        tripsList.remove(position);

        for(int i=position-1;i>=0;i--){
            MainActivity.trips.get(i).setPosition(MainActivity.trips.get(i).getPosition()-1);
            System.out.println(MainActivity.trips.get(i).getDistance() + " position changed to "+(MainActivity.trips.get(i).getPosition()));
            db.updateTripPosition(MainActivity.trips.get(i));
        }
        notifyItemRemoved(position);
    }

    public void setItemTouchHelper(ItemTouchHelper itemTouchHelper) {
        this.itemTouchHelper = itemTouchHelper;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener, GestureDetector.OnGestureListener {

        private TextView tvAvgFuel, tvAvgSpeed, tvDistance, tvDuration, tvTripCost, tvStartLocation, tvDestLocation;
        private CardView cardViewTrips;
        private GestureDetector gestureDetector;
        private MapView mapView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gestureDetector = new GestureDetector(itemView.getContext(), this);
            cardViewTrips = itemView.findViewById(R.id.cardViewTrips);

            mapView = itemView.findViewById(R.id.mapView);
            tvStartLocation = itemView.findViewById(R.id.tvStartLocation);
            tvDestLocation = itemView.findViewById(R.id.tvDestLocation);
            tvAvgFuel = itemView.findViewById(R.id.tvAvgFuel);
            tvAvgSpeed = itemView.findViewById(R.id.tvAvgSpeed);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvTripCost = itemView.findViewById(R.id.tvTripCost);
        }

        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {
            //itemTouchHelper.startDrag(this);
        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            gestureDetector.onTouchEvent(motionEvent);
            return true;
        }
    }

    public AdapterTrips(Context context, List<Trip> tripsList, int layout_resource, MainActivity activity) {
        this.context = context;
        this.tripsList = tripsList;
        this.layout_resource = layout_resource;
        this.activity = activity;
        db = new Database();
    }

    @NonNull
    @Override
    public AdapterTrips.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout_resource, parent, false);
        AdapterTrips.ViewHolder viewHolder = new AdapterTrips.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Trip trip = tripsList.get(position);
        holder.tvStartLocation.setText("Starting Location: -");
        holder.tvDestLocation.setText("Destination Location -");
//        try{
//            holder.tvStartLocation.setText(String.format("Starting Coordinates: %s, %s", trip.getStartCoordinates().getLatitude(), trip.getStartCoordinates().getLongitude()));
//            holder.tvDestLocation.setText(String.format("Destination Coordinates: %s, %s", trip.getEndCoordinates().getLatitude(), trip.getEndCoordinates().getLongitude()));
//        }
//        catch (NullPointerException e){
//            LogWriter.writeError("[ERROR_LOCATION]: ","Unable to obtain location coordinates.");
//            holder.tvStartLocation.setText("Starting Location: -");
//            holder.tvDestLocation.setText("Destination Location -");
//        }
        holder.tvAvgFuel.setText(String.format("Average Fuel Consumption: %s l/100km", df.format(trip.getAvgFuel())));
        holder.tvAvgSpeed.setText(String.format("Average Vehicle Speed: %s km/h", trip.getAvgSpeed()));
        holder.tvDistance.setText(String.format("Distance Travelled: %s km", df.format(trip.getDistance())));
        holder.tvDuration.setText(String.format("Time Duration: %s", trip.getDuration()));
        holder.tvTripCost.setText(String.format("Trip Cost: %s BGN", df.format(trip.getTripCost())));

        LocationComponentPlugin locationComponentPlugin = getLocationComponent(holder.mapView);
        final NavigationLocationProvider navigationLocationProvider = new NavigationLocationProvider();

        holder.mapView.getMapboxMap().loadStyleUri(Style.LIGHT, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                Point startPoint = Point.fromLngLat(trip.getStartLongitude(), trip.getStartLatitude());
                Point endPoint = Point.fromLngLat(trip.getEndLongitude(), trip.getEndLatitude());
                //Point startPoint = Point.fromLngLat(trip.getStartCoordinates().getLongitude(), trip.getStartCoordinates().getLatitude());
                //Point endPoint = Point.fromLngLat(trip.getEndCoordinates().getLongitude(), trip.getEndCoordinates().getLatitude());
                //Point endPoint = Point.fromLngLat(23.353902, 42.657138); //TU 2-ri blok
                //Point endPoint = Point.fromLngLat(23.37575068, 42.66727196); //TechPark
                holder.mapView.getMapboxMap().setCamera(new CameraOptions.Builder().center(getMidPoint(startPoint, endPoint)).zoom(10.0).build());
                locationComponentPlugin.setEnabled(true);
                locationComponentPlugin.setLocationProvider(navigationLocationProvider);

                AnnotationPlugin annotationPlugin = AnnotationPluginImplKt.getAnnotations(holder.mapView);
                PointAnnotationManager pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationPlugin, holder.mapView);

                Drawable drawableStart = ContextCompat.getDrawable(activity, R.drawable.icon_loc_start);
                Bitmap bitmapStart = ((BitmapDrawable) drawableStart).getBitmap();
                PointAnnotationOptions pointAnnotationOptionsStart = new PointAnnotationOptions().withTextAnchor(TextAnchor.CENTER).withIconImage(bitmapStart)
                        .withPoint(startPoint);
                pointAnnotationManager.create(pointAnnotationOptionsStart);

                Drawable drawableEnd = ContextCompat.getDrawable(activity, R.drawable.icon_loc_end);
                Bitmap bitmapEnd = ((BitmapDrawable) drawableEnd).getBitmap();
                PointAnnotationOptions pointAnnotationOptionsEnd = new PointAnnotationOptions().withTextAnchor(TextAnchor.CENTER).withIconImage(bitmapEnd)
                        .withPoint(endPoint);
                pointAnnotationManager.create(pointAnnotationOptionsEnd);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tripsList.size();
    }

    public void add(Trip trip) {
        if(!tripsList.contains(trip)){
            tripsList.add(trip);
        }
    }

    public void emptyList(){
        tripsList.clear();
    }

    private Point getMidPoint(Point start, Point end){
        //Cartesian coordinates for the two points
        double lat1 = start.latitude();
        double lng1 = start.longitude();
        double lat2 = end.latitude();
        double lng2 = end.longitude();

        double latMid;
        double lngMid;
        if(lat1>lat2){
            latMid = lat2+((lat1 - lat2)/2);
        }
        else{
            latMid = lat1+((lat2 - lat1)/2);
        }
        if(lng1>lng2){
            lngMid = lng2+((lng1 - lng2)/2);
        }
        else{
            lngMid = lng1+((lng2 - lng1)/2);
        }
        Log.d("POINT", String.format("%s, %s", latMid, lngMid));
        return Point.fromLngLat(lngMid, latMid);
    }
}
