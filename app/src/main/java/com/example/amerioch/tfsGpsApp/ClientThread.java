package com.example.amerioch.tfsGpsApp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;

import java.io.IOException;
import java.util.UUID;

public class ClientThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private final BluetoothAdapter mBluetoothAdapter;
    private final static String TAG = "RAMON";
    private Context contxt;
    //UUID common to both devices
    final static UUID MY_UUID = UUID.fromString("a2adcbc0-0eb0-11e5-b939-0800200c9a66");

    public ClientThread(BluetoothDevice device, BluetoothAdapter bluetoothAdapter, Context contx) {
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        this.contxt = contx;
        this.mBluetoothAdapter = bluetoothAdapter;
        BluetoothSocket tmp = null;
        mmDevice = device;

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
        }
        mmSocket = tmp;

    }

    public void run() {
        // Cancel discovery because it will slow down the connection
        this.mBluetoothAdapter.cancelDiscovery();

        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception

            //Socket trying to connect
            mmSocket.connect();

            //Execute the second Intent once the socket has been correctly established
            Intent mesures= new Intent(this.contxt,Mesure.class);
            mesures.putExtra("key", 1); //Put your id to your next Intent (We're the client)
            this.contxt.startActivity(mesures);

        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                mmSocket.close();
            } catch (IOException closeException) { }
            return;
        }

        // Do work to manage the connection (in a separate thread)
        ConnectedThread manageConnexion = new ConnectedThread(mmSocket);
    }

    /** Will cancel the listening socket, and cause the thread to finish */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }

}