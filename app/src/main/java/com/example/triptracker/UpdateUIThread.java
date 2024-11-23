package com.example.triptracker;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.pires.obd.commands.control.DtcNumberCommand;
import com.github.pires.obd.commands.control.TroubleCodesCommand;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class UpdateUIThread extends Thread {

    private MainActivity activity;
    private ProfileActivity profileActivity;
    private BluetoothAdapter btAdapter;
    private static Context applicationContext;

    public static void setApplicationContext(Context applicationContext) {
        UpdateUIThread.applicationContext = applicationContext;
    }

    public static Context getApplicationContext() {
        return applicationContext;
    }



    private boolean LocationFlag = Boolean.FALSE;
    private static boolean initMapFlag = Boolean.FALSE;
    private boolean previewModeFlag = Boolean.FALSE;

    private Location startLocation;
    private Location destLocation;


    public UpdateUIThread(MainActivity activity) {
        this.activity = activity;
    }
    public UpdateUIThread(ProfileActivity activity) {
        this.profileActivity = activity;
    }

    public UpdateUIThread(MainActivity activity, BluetoothAdapter btAdapter) {
        this.activity = activity;
        this.btAdapter = btAdapter;
    }



    public void run() {
        while (true) {

            Log.d("UI_Thread", "Thread is still running...");
            if (btAdapter.isEnabled()) {

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.BtGradient.setBackgroundResource(R.drawable.gradient_minty);
                        MainActivity.tvBtToggle.setText("Bluetooth is ON");

                    }
                });
            }
            if (LocationFlag) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.LocGradient.setBackgroundResource(R.drawable.gradient_minty);
                        MainActivity.tvLocToggle.setText("Location is ON");

                    }
                });

            }
            if (!btAdapter.isEnabled() || !LocationFlag) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            if (btAdapter.isEnabled() && LocationFlag) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.loBtLocSettings.setVisibility(View.GONE);
                        MainActivity.loRootView.setVisibility(View.VISIBLE);
                        ArrayList<BluetoothDevice> connectedDevices = activity.getConnectedDevices();
                        ArrayList<BluetoothDevice> pairedDevices = activity.getPairedDevices();
                        activity.loadRecyclerView(pairedDevices, connectedDevices);
                    }
                });
                break;
            }

        }
    }

    void dataUpdate(int id, String result) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (id == MainActivity.data1Spinner.getId()) {
                    MainActivity.tvData1.setText(result);
                } else {
                    MainActivity.tvData2.setText(result);
                }

            }
        });
    }

    void calculationsUpdate(String avgSpeed, String currConsumption, String avgConsumption, String distance) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MainActivity.tvAvgSpeed.setText(String.format("Average Speed\n %s km/h", avgSpeed));//     "Average Speed: " + avgSpeed.concat("km/h"));
                MainActivity.tvInstantConsumption.setText(String.format("Current Fuel Consumption\n %s ", currConsumption));
                MainActivity.tvAvgConsumption.setText(String.format("Avg. Fuel Consumption\n %s", avgConsumption));
                MainActivity.tvDistance.setText(String.format("Distance Travelled\n %s km", distance));
            }
        });
    }

    void locUiUpdate() {
        LocationFlag = Boolean.TRUE;
    }

    void deviceListUpdate(AdapterBTPairs adapter, BluetoothDevice device) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.add(device);
                adapter.notifyDataSetChanged();
            }
        });
    }

    void establishedConnectionUI(String deviceName) {
        startLocation = getCurrentLocation();
        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                MainActivity.tvHeader.setText(deviceName);
                MainActivity.tvHeader.setEnabled(Boolean.TRUE);
                MainActivity.devicesSV.setVisibility(View.GONE);
                MainActivity.loMap.setVisibility(View.VISIBLE);
                MainActivity.loCalculations.setVisibility(View.VISIBLE);
                MainActivity.loData.setVisibility(View.VISIBLE);
                MainActivity.loFooter.setVisibility(View.VISIBLE);
                MainActivity.data1Spinner.setSelection(0);
                MainActivity.data1Spinner.setSelection(1);
                MainActivity.btnNavigation.setBackgroundResource(R.drawable.footer_layout_border_leftbtn_filled);
                MainActivity.btnTrips.setBackgroundResource(R.drawable.footer_layout_border_rightbtn);
                if(!initMapFlag){
                    activity.initMap();
                    initMapFlag = Boolean.TRUE;
                }
            }
        });
    }

    void closeConnection(BluetoothSocket socket, TransmitDataThread thread, boolean ignitionOffFlag){
        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if(ignitionOffFlag){
                    try {
                        destLocation = getCurrentLocation();
                        socket.close();
                        thread.interrupt();

                        MainActivity.devicesSV.setVisibility(View.VISIBLE);
                        MainActivity.tripsSV.setVisibility(View.GONE);
                        MainActivity.loMap.setVisibility(View.GONE);
                        MainActivity.loCalculations.setVisibility(View.GONE);
                        MainActivity.loData.setVisibility(View.GONE);
                        MainActivity.loFooter.setVisibility(View.GONE);
                        MainActivity.tvHeader.setText("Connect to a device");
                        MainActivity.tvHeader.setEnabled(Boolean.FALSE);
                    } catch (IOException e) {
                        LogWriter.writeError("[Socket_UI-Thread]",e.toString());
                    }
                }
                else{
                    MainActivity.tvHeader.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Dialog dialog = new Dialog(activity);
                            dialog.setContentView(R.layout.dialog_disconnect);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                            Button btnDisconnect = dialog.findViewById(R.id.btnDisconnect);
                            Button btnCancel = dialog.findViewById(R.id.btnCancel);

                            btnCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                }
                            });

                            btnDisconnect.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    try {
                                        destLocation = getCurrentLocation();
                                        socket.close();
                                        thread.interrupt();

                                        MainActivity.devicesSV.setVisibility(View.VISIBLE);
                                        MainActivity.tripsSV.setVisibility(View.GONE);
                                        MainActivity.loMap.setVisibility(View.GONE);
                                        MainActivity.loCalculations.setVisibility(View.GONE);
                                        MainActivity.loData.setVisibility(View.GONE);
                                        MainActivity.loFooter.setVisibility(View.GONE);
                                        MainActivity.tvHeader.setText("Connect to a device");
                                        MainActivity.tvHeader.setEnabled(Boolean.FALSE);
                                        dialog.dismiss();

                                    } catch (IOException e) {
                                        LogWriter.writeError("[Socket_UI-Thread]",e.toString());
                                    }
                                }
                            });

                            dialog.show();
                        }
                    });
                }
            }
        });
    }

    void showDTCReport(DtcNumberCommand DtcNumber, TroubleCodesCommand TroubleCodes){
        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Dialog dialog = new Dialog(activity);
                dialog.setContentView(R.layout.dialog_dtc);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                Button buttonOk = dialog.findViewById(R.id.btnOk);
                TextView tvCheckEngine = dialog.findViewById(R.id.tvCheckEngine);
                TextView tvDtcCnt = dialog.findViewById(R.id.tvDtcCnt);
                if(DtcNumber.getMilOn()){
                    tvCheckEngine.setText("Check engine light is ON.");
                }
                int cntDtc = DtcNumber.getTotalAvailableCodes();
                String numberDTCs;
                if(cntDtc == 1){
                    numberDTCs = "There is " + String.valueOf(cntDtc) + " DTC found.";
                }
                else{
                    numberDTCs = "There are " + String.valueOf(cntDtc) + " DTCs found.";
                }
                tvDtcCnt.setText(numberDTCs);

                String[] codes = TroubleCodes.getFormattedResult().split("/")[0].split("\n");

                RecyclerView recyclerView = dialog.findViewById(R.id.recyclerView);
                List<String> items = Arrays.asList(codes);

                TextAdapter adapter = new TextAdapter(items);
                View.OnClickListener onClickListener = v -> {
                    int position = ((RecyclerView.LayoutParams) v.getLayoutParams()).getViewAdapterPosition();
                    String item = items.get(position);
                    Log.d("[Item]", item);
                    Intent intent = new Intent(activity, WebviewActivity.class);
                    intent.putExtra("dtc", item);
                    activity.startActivity(intent);
                };
                adapter.setOnClickListener(onClickListener);

                recyclerView.setLayoutManager(new LinearLayoutManager(activity));
                recyclerView.setAdapter(adapter);

                buttonOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
    }

    void startPreviewMode(){
        previewModeFlag = Boolean.TRUE;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                MainActivity.imgAppLogo.setEnabled(Boolean.TRUE);
                MainActivity.devicesSV.setVisibility(View.GONE);
                MainActivity.tvHeader.setEnabled(Boolean.TRUE);
                MainActivity.loMap.setVisibility(View.VISIBLE);
                MainActivity.loCalculations.setVisibility(View.GONE);
                MainActivity.loData.setVisibility(View.GONE);
                MainActivity.loFooter.setVisibility(View.VISIBLE);
                MainActivity.data1Spinner.setSelection(0);
                MainActivity.data1Spinner.setSelection(1);
                if(!initMapFlag){
                    activity.initMap();
                    initMapFlag = Boolean.TRUE;
                }
            }
        });
    }

    void stopPreviewMode(){
        previewModeFlag = Boolean.FALSE;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MainActivity.devicesSV.setVisibility(View.VISIBLE);
                MainActivity.tvHeader.setEnabled(Boolean.FALSE);
                MainActivity.loMap.setVisibility(View.GONE);
                MainActivity.loCalculations.setVisibility(View.GONE);
                MainActivity.loData.setVisibility(View.GONE);
                MainActivity.loFooter.setVisibility(View.GONE);
                MainActivity.imgAppLogo.setEnabled(Boolean.FALSE);
            }
        });
    }

    void TripsView(){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MainActivity.loMap.setVisibility(View.GONE);
                MainActivity.loCalculations.setVisibility(View.GONE);
                MainActivity.loData.setVisibility(View.GONE);

                MainActivity.tripsSV.setVisibility(View.VISIBLE);

                MainActivity.btnNavigation.setBackgroundResource(R.drawable.footer_layout_border_leftbtn);
                MainActivity.btnTrips.setBackgroundResource(R.drawable.footer_layout_border_rightbtn_filled);
            }
        });
    }

    void NavigationView(){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MainActivity.tripsSV.setVisibility(View.GONE);

                MainActivity.loMap.setVisibility(View.VISIBLE);
                if(!previewModeFlag){
                    MainActivity.loCalculations.setVisibility(View.VISIBLE);
                    MainActivity.loData.setVisibility(View.VISIBLE);
                }

                MainActivity.btnNavigation.setBackgroundResource(R.drawable.footer_layout_border_leftbtn_filled);
                MainActivity.btnTrips.setBackgroundResource(R.drawable.footer_layout_border_rightbtn);
            }
        });
    }

    void showTripSummary(Trip trip){
        Database db = new Database();

        trip.setStartLongitude(startLocation.getLongitude());
        trip.setStartLatitude(startLocation.getLatitude());
        trip.setEndLongitude(destLocation.getLongitude());
        trip.setEndLatitude(destLocation.getLatitude());

        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Dialog dialog = new Dialog(activity);
                dialog.setContentView(R.layout.dialog_trip);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                DecimalFormat df = new DecimalFormat("#0.00");

                TextView tvAvgFuel = dialog.findViewById(R.id.tvAvgFuel);
                TextView tvAvgSpeed = dialog.findViewById(R.id.tvAvgSpeed);
                TextView tvDistance = dialog.findViewById(R.id.tvDistance);
                TextView tvDuration = dialog.findViewById(R.id.tvDuration);
                TextView tvTripCost = dialog.findViewById(R.id.tvTripCost);

                Button btnSave = dialog.findViewById(R.id.btnSave);
                Button btnDismiss = dialog.findViewById(R.id.btnDismiss);

                tvAvgFuel.setText(String.format("Average Fuel Consumption: %s l/100km", df.format(trip.getAvgFuel())));
                tvAvgSpeed.setText(String.format("Average Vehicle Speed: %s km/h", trip.getAvgSpeed()));
                tvDistance.setText(String.format("Distance Travelled: %s km", df.format(trip.getDistance())));
                tvDuration.setText(String.format("Time Duration: %s", trip.getDuration()));
                tvTripCost.setText(String.format("Trip Cost: %s BGN", df.format(trip.getTripCost())));



                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainActivity.adapterTrips.add(trip);
                        db.addTrip(trip);
                        MainActivity.sortTrips();
                        MainActivity.adapterTrips.notifyDataSetChanged();
                        Log.d("Position", "Trips after insertion of new one " + String.valueOf(MainActivity.adapterTrips.getItemCount()));

                        dialog.dismiss();
                    }
                });

                btnDismiss.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialog.dismiss();
                    }
                });


                dialog.show();
            }
        });
    }

    private Location getCurrentLocation(){
        LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        return lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }


    void showToast(String message){
        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
