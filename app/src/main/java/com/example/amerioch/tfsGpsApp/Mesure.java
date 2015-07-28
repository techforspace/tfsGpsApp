package com.example.amerioch.tfsGpsApp;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.nio.ByteBuffer;
import java.text.DecimalFormat;


public class Mesure extends Activity {
    private int client; //If client 0 server if 1 client
    private Button button;
    private final static String TAG = "RAMON"; //TAG used to debug with Log.d()
    private ConnectedThread thread; //Thread used to manage the connection (write, read,...)
    private Handler mHandler;

    //Executed when is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);
        //The handler reads the message received
        this.mHandler = new Handler(){
            public void handleMessage(Message msg){
                byte[] buffer = (byte[]) msg.obj;
                //Convert the bytes received into the distance (double)
                double distance = ByteBuffer.wrap(buffer).getDouble();
                //Print the distance in the screen
                TextView textDistance = (TextView) findViewById(R.id.distance);
                //Visualize only 2 decimals of the distance
                DecimalFormat f = new DecimalFormat();
                f.setMaximumFractionDigits(2);
                textDistance.setText("Distance: " + f.format(distance));
            }
        };
        this.client = getIntent().getIntExtra("key",2); //Know if we're server or client
        this.button= (Button) findViewById(R.id.logOut);
        this.thread = ConnectedThread.getthread(); //Get the connexion thread
        this.thread.sethandler(this.mHandler); // set handler
        this.thread.setClient(this.client); //set client
        this.thread.setcontext(this); //set context

        //If we're client, server or we have a problem (default)
        switch(this.client){
            case 0: //serveur
                this.button.setEnabled(false);
                this.button.setText("Waiting Measures");
                //Launch the server
                launchServer();
                break;

            case 1: //client

                this.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    //Launch the client when we push the button
                    public void onClick(View v) {
                        launchClient();
                    }
                });
                break;

            //Houston: We have a problem
            default:
                Log.d(TAG, "ERROR bad argument returned ");
                break;

        }

    }

    //Launch the client and the server (actually,both do the same but we preferred to call
    //them different to be clearer)
    private void launchServer() {thread.start();}
    private void launchClient() {
        thread.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mesure, menu);
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
