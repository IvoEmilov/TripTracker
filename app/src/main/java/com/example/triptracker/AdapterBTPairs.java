package com.example.triptracker;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterBTPairs extends RecyclerView.Adapter<AdapterBTPairs.ViewHolder> implements ItemTouchHelperAdapter {
    private List<BluetoothDevice> BTPairs;
    private Context context;
    private ItemTouchHelper itemTouchHelper;
    private int layout_resource;
    private MainActivity activity;


    @Override
    public void onItemMove(int fromPosition, int toPosition) {

    }

    @Override
    public void onItemSwiped(int position) {

    }

    public void setItemTouchHelper(ItemTouchHelper itemTouchHelper) {
        this.itemTouchHelper = itemTouchHelper;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener, GestureDetector.OnGestureListener {
        private TextView tvDeviceName, tvDeviceMAC;
        private CardView cardViewBTPairs;
        private GestureDetector gestureDetector;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gestureDetector = new GestureDetector(itemView.getContext(), this);
            cardViewBTPairs = itemView.findViewById(R.id.cardViewBTPairs);
            tvDeviceName = itemView.findViewById(R.id.tvDeviceName);
            tvDeviceMAC = itemView.findViewById(R.id.tvDeviceMAC);
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

    public AdapterBTPairs(Context context, List<BluetoothDevice> BTPairs, int layout_resource, MainActivity activity) {
        this.context = context;
        this.BTPairs = BTPairs;
        this.layout_resource = layout_resource;
        this.activity = activity;
    }

    @NonNull
    @Override
    public AdapterBTPairs.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout_resource, parent, false);
        AdapterBTPairs.ViewHolder viewHolder = new AdapterBTPairs.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterBTPairs.ViewHolder holder, int position) {
        BluetoothDevice pair = BTPairs.get(position);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        holder.tvDeviceName.setText(pair.getName());
        holder.tvDeviceMAC.setText(pair.getAddress());

        holder.cardViewBTPairs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ConnectThread connect = new ConnectThread(pair, activity);
                connect.start();
            }
        });

        }

    @Override
    public int getItemCount() {
        return BTPairs.size();
    }

    public void add(BluetoothDevice device) {
        if(device.getName() != null && !BTPairs.contains(device)){
            BTPairs.add(device);
        }
    }

    public void emptyList(){
        BTPairs.clear();
    }
}
