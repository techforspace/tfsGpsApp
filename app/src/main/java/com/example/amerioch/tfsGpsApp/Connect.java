package com.example.amerioch.tfsGpsApp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Set;



public class Connect extends ActionBarActivity {

    private final static int REQUEST_ENABLE_BT = 1;
    private final static String TAG = "RAMON"; //TAG used to debug the program with Log.d()

    private Button buttonSendLogin;
    private Button buttonRegister;
    private TextView usernameTextView;
    private TextView passwordTextView;
    private Set<BluetoothDevice> devices;
    private GpsClass gps;
    private final String  URL_REMOTE_DB = AccountData.URLDB;
    public static DataBaseInteraction dB;

    private final String  PASS = AccountData.PASS;
    private final String  USERNAME = AccountData.USERNAME;
    private final String  USERSTABLENAME = AccountData.USERSTABLENAME;
    private final String  FRIENDSTABLENAME = AccountData.FRIENDSTABLENAME;
    double latitude, longitude, altitude;
    private String password;
    public static String username;

    //Progress Dialog
    ContextThemeWrapper ctw;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Loading bar style
        ctw = new ContextThemeWrapper(this, R.style.Theme_AppCompat_Light_Dialog);

        //We create the graphic interface as said in the project
        this.buttonSendLogin = (Button) findViewById(R.id.buttonAccept);
        this.buttonSendLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Loading bar
                pd = new ProgressDialog(ctw);
                pd.setMessage("Loading");
                pd.show();
                pd.setCanceledOnTouchOutside(false);

                //Load the Password and Username fields from the GUI
                EditText pass = (EditText) findViewById(R.id.password);

                //Enable connections (GPS and Internet)
                connectionInternetGPSDialog();

                    /*Create new DB connexion from the user + pass, if the user is not register we won't be able to pass
                    *from this activity to the next one
                    */
                Thread thread = new Thread(new Runnable(){
                    @Override
                    public void run() {
                        try {
                            dB = new DataBaseInteraction(AccountData.URLDB, AccountData.PASS, AccountData.USERNAME);

                            EditText user = (EditText) findViewById(R.id.username);
                            username = user.getText().toString();
                            EditText pass = (EditText) findViewById(R.id.password);
                            password = pass.getText().toString();
                            dB.connectToDB();
                            if(verifUser(username,password) && !username.equals("") && !password.equals("")) {
                                dB.updatePosition(user.getText().toString(), latitude, longitude, latitude);
                                dB.online(user.getText().toString());
                                Intent mainScreen = new Intent(Connect.this, MainScreen.class);
                                pd.dismiss();
                                startActivity(mainScreen);
                            }else{
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Username or Password not correct", Toast.LENGTH_SHORT).show();
                                        pd.dismiss();
                                    }
                                });
                            }
                        } catch (SQLException sql) {
                            System.out.println("SQLException: " + sql.getMessage() + sql.getCause());
                            System.out.println("SQLState: " + sql.getSQLState());
                            System.out.println("Error: " + sql.getErrorCode());
                            System.out.println("StackTrace: " + sql.getStackTrace());
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "ERROR while connecting, try again", Toast.LENGTH_SHORT).show();
                                    pd.dismiss();
                                }
                            });
                        }
                    }
                });
                thread.start();

            }
        });

        this.buttonRegister = (Button) findViewById(R.id.buttonRegister);
        this.buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerScreen = new Intent(Connect.this, Register.class);
                startActivity(registerScreen);
            }
        });


    }

    private boolean verifUser(String username, String password){

        if(dB.verifyPassword(password, username).equals(username)){
            return true;
        }else{
            return false;
        }
    }

    private void connectionInternetGPSDialog(){

        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        //For GPS Check
        this.gps = new GpsClass(null, Connect.this);

        // Check if GPS enabled
        if(gps.canGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            latitude = gps.getLatitude();

        } else {gps.showSettingsAlert();}

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

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en =
                 NetworkInterface.getNetworkInterfaces(); en
                         .hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr =
                     intf.getInetAddresses(); enumIpAddr
                             .hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement
                            ();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e(TAG, ex.toString());
        }
        return null;
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

    /*@Override
    public void onDestroy() {
        Log.d("onDestroy", "begin");

                                super.onDestroy();
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {

                    dB = new DataBaseInteraction(AccountData.URLDB, AccountData.PASS, AccountData.USERNAME);
                    dB.offline(AccountData.USERSTABLENAME, username);
                Log.d("onDestroy", "thread");

            }
        });
        thread.start();
        Log.d("onDestroy", "end");

    }*/
}