package com.example.amerioch.tfsGpsApp;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class ConnectedThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream; //Data reception
    private final OutputStream mmOutStream; //Send data
    private final static String TAG = "RAMON"; //TAG used to debug the program with Log.d()
    private int client; //0 if server, 1 if client
    private Context ctxt; //Context of Mesure

    private static final int MESSAGE_READ = 2; //value choose randomly
    private static ConnectedThread connect_thread;
    private Handler mhandler;

    public static ConnectedThread getthread(){
        return connect_thread;
    }

    public void setcontext(Context ctxt){
        this.ctxt = ctxt;
    }
    public static void setthread(ConnectedThread connct_thread){
        connect_thread = connct_thread;
    }

    public void sethandler(Handler h){
        this.mhandler = h;
    }

    //Initialize class values of the thread
    public ConnectedThread(BluetoothSocket socket) {
        setthread(this);
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void setClient(int client){
        this.client=client;
    }

    //Execution of the thread
    public void run() {
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes_recv; // bytes returned from read()
        byte[] send= {0,1,2,3};

        if(client==0){
            //Server
            while (true) {
                try {
                    // Read from the InputStream
                    bytes_recv = mmInStream.read(buffer);
                    //Send the ACK message to the client
                    write(send);
                    //Pass the message received to the handler in order to read it
                    this.mhandler.obtainMessage(MESSAGE_READ, bytes_recv, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        if(client==1){
            //Client
            byte[] byt_em; //buffer emission
            while (true) {
                try {
                    ScanResult ap=null;
                    //Acces to wifi service
                    WifiManager wifi = (WifiManager) this.ctxt.getSystemService(this.ctxt.WIFI_SERVICE);
                    wifi.startScan();
                    //Scan wifi Points
                    List<ScanResult> wifiPoints = wifi.getScanResults();
                    //We consider we're rely to only one wifi device, so the first one will be the one
                    //we are looking for the SSID paired with us
                    for (ScanResult r : wifiPoints) {
                        if(r.SSID.equals("GT-I9300")){
                            ap = r;
                        }
                    }
                    //ScanResult ap = wifiPoints.iterator().next();
                    //Calculation of the distance from Wi-Fi signal
                    double exp = (27.55 - (20 * Math.log10(ap.frequency)) + Math.abs(ap.level)) / 20.0;
                    double distance = Math.pow(10.0, exp);
                    //convert double format to Byte[] in order to send it to the server
                    byt_em=doubleToByte(distance);
                    //Send the distance to the server
                    this.write(byt_em);
                    //read the ACK received by the server
                    bytes_recv = this.mmInStream.read(buffer);
                    //Pass the message sent to the handler in order to read it
                    this.mhandler.obtainMessage(MESSAGE_READ, bytes_recv, -1, byt_em).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(100); //Wait 0.1s to restart the run()
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    //Returns a Byte[] from a Double
    public byte[] doubleToByte(double d){
        byte[] output = new byte[8];
        long lng = Double.doubleToLongBits(d);
        for(int i = 0; i < 8; i++) output[i] = (byte)((lng >> ((7 - i) * 8)) & 0xff);
        return output;
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) { }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}