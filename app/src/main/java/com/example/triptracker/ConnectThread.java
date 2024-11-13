package com.example.triptracker;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;


import java.io.IOException;
import java.util.UUID;

class ConnectThread extends Thread {
    private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    MainActivity activity;
    UpdateUIThread UIThread;

    public ConnectThread(BluetoothDevice device, MainActivity activity) {
        // Use a temporary object that is later assigned to mmSocket
        // because mmSocket is final.
        BluetoothSocket tmp = null;
        mmDevice = device;
        this.activity = activity;

        UIThread = new UpdateUIThread(activity);

        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.

            //tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            UIThread.showToast("Connection failed");
            LogWriter.writeError("[Connection_Error] ", "Connection failed!");
        }
        mmSocket = tmp;
    }

    public void run() {
        // Cancel discovery because it otherwise slows down the connection.
        //bluetoothAdapter.cancelDiscovery();

        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            mmSocket.connect();

        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            try {
                UIThread.showToast("Unable to connect");
                LogWriter.writeError("[Connection_Error] ", connectException.toString());
                mmSocket.close();
            } catch (IOException closeException) {
                LogWriter.writeError("[IOException_C-Thread] ", closeException.toString());
                UIThread.showToast("Could not close the client socket");
            }
            return;
        }

        // The connection attempt succeeded. Perform work associated with
        // the connection in a separate thread.
        //manageMyConnectedSocket(mmSocket);
        LogWriter.write("[Connection] ", "Connection successful.");
        UIThread.deviceListUpdate(activity.BTAdapterConnected, mmDevice);
        TransmitDataThread tdThread = new TransmitDataThread(mmSocket, activity, mmDevice);
        tdThread.start();
    }

    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            LogWriter.writeError("[IOException_C-Thread] ", e.toString());
            UIThread.showToast("Error closing the connection.");
        }
    }



}