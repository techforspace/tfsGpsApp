package com.example.amerioch.tfsGpsApp;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;

import java.io.IOException;
import java.util.UUID;
import android.os.Handler;


public class ServerThread extends Thread {
    private final BluetoothServerSocket mmServerSocket;
    private final static String TAG = "RAMON";
    private Handler h;
    private Context context;

    //UUID the same for both devices
    final static UUID MY_UUID = UUID.fromString("a2adcbc0-0eb0-11e5-b939-0800200c9a66");

    //Initialization of values
    public ServerThread(BluetoothAdapter mBluetoothAdapter, String NAME, Context context) {
        // Use a temporary object that is later assigned to mmServerSocket,
        // because mmServerSocket is final
        BluetoothServerSocket tmp = null;
        this.context = context;
        this.h = h;
        try {
            // MY_UUID is the app's UUID string, also used by the client code
            tmp= mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
        } catch (IOException e) {}
        mmServerSocket = tmp;
    }

    public void run() {
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned
        while (true) {
            try {
                //Accept the socket launched from the client
                socket = mmServerSocket.accept();
                //Create the new Activity Measure
                Intent mesures = new Intent(this.context, Mesure.class);
                mesures.putExtra("key", 0); //Put your id to your next Intent (We're the server)
                //Start the activity
                this.context.startActivity(mesures);

            } catch (IOException e) {
                break;
            }
            // If a connection was accepted
            if (socket != null) {
                // Do work to manage the connection (in a separate thread)
                ConnectedThread managing = new ConnectedThread(socket);
                try {
                    mmServerSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    /** Will cancel the listening socket, and cause the thread to finish */
    public void cancel() {
        try {
            mmServerSocket.close();
        } catch (IOException e) { }
    }
}