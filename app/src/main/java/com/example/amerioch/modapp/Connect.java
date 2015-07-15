package com.example.amerioch.modapp;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Set;


public class Connect extends ActionBarActivity {

    private final static int REQUEST_ENABLE_BT = 1;
    private final static String TAG = "RAMON"; //TAG used to debug the program with Log.d()

    private Button buttonSendLogin;
    private TextView usernameTextView;
    private TextView passwordTextView;
    private Set<BluetoothDevice> devices;
    private final String  URL_REMOTE_DB = "jdbc:mysql://db4free.net:3306/tfsapp";
    private final String  PASS = "nonsesabe";
    private final String  DBNAME = "tfsapp";
    private final String  TABLENAME = "users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //We create the graphic interface as said in the project
        this.buttonSendLogin = (Button) findViewById(R.id.buttonAccept);
        this.buttonSendLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //We send the login values (username+pass)
                    sendLoginToDB();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    //we're the server
    private void sendLoginToDB() throws IOException {
        connectionDialog();
        DataBaseInteraction dB = new DataBaseInteraction(URL_REMOTE_DB,PASS,DBNAME);
        Log.d(TAG,"Siamo qui");
        dB.connectToDB();
        dB.insertRow(TABLENAME,"pip","ok",true,"192.168.2.1");
        dB.insertRow(TABLENAME,"giacomo","non",false,"193.65.42.1");

        //Unable the button serveur (avoid clicking twice or more)
        usernameTextView = (TextView) findViewById(R.id.username);
        passwordTextView = (TextView) findViewById(R.id.password);
        String username = usernameTextView.getText().toString();
        String password = passwordTextView.getText().toString();
        DataBaseInteraction dataDBremote 	= new DataBaseInteraction(URL_REMOTE_DB,PASS,DBNAME);
        TextView texte_attente = (TextView) findViewById(R.id.texte_attente);
        texte_attente.setText("* WAITING CLIENT TO CONNECT *");
        //Create the server thread passing the bluetooth device paired to us in order to create the socket
        //ServerThread serverConnexion = new ServerThread(bluetooth,devices.iterator().next().getName(),this);
        //Run the server thread
        //serverConnexion.start();
    }

    private void connectionDialog(){

        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);


            //For 3G check
            boolean connected3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
            //For WiFi Check
            boolean connectedWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();

        if (!connected3g && !connectedWifi){
            new AlertDialog.Builder(this)
                    .setTitle("Internet connection")
                    .setMessage("This application needs to connect to the internet, please select your preferable way:")
                    .setPositiveButton("WI-FI", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            activateWifi();
                        }
                    })
                    .setNegativeButton("3G/4G", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            activate3G(getApplicationContext(),true);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();        //Ask to switch on bluetooth if it's not already activated

        }

    }

    private void activateWifi(){
        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
    }

    public static String getipAddress() {
        try {
            for (Enumeration en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = (NetworkInterface) en.nextElement();
                for (Enumeration enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String ipaddress=inetAddress.getHostAddress().toString();
                        Log.e("ip address", "" + ipaddress);
                        return ipaddress;
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private void activate3G(Context context, boolean enabled){
        try {
            final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            final Class conmanClass = Class.forName(conman.getClass().getName());
            final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
            iConnectivityManagerField.setAccessible(true);
            final Object iConnectivityManager = iConnectivityManagerField.get(conman);
            final Class iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
            final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);
            setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //Launch the second activity "Measure"
    public void launchMeasures(){
        Intent measures = new Intent(this, Mesure.class);
        startActivity(measures);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
