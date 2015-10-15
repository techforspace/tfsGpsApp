package com.example.amerioch.tfsGpsApp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.maps.*;


/**
 * Created by ramon on 26/07/15.
 */
public class MainScreen extends Activity implements View.OnClickListener {
    private TableLayout table;
    private Button addFriendsButton;
    private Button buttonLogOut;
    private GpsClass gps;
    private boolean firstTime = true;
    Double distance;
    String distanceStr;

    TableLayout rl;

    //Progress Dialog
    ContextThemeWrapper ctw;
    ProgressDialog pd;

    //Array for store the location's friends
    HashMap locationFriends;

    //Executed when is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);
        gps = new GpsClass(this, this);
        //Loading bar style
        ctw = new ContextThemeWrapper(this, R.style.Theme_AppCompat_Light_Dialog);

        locationFriends = new HashMap<String, Location>();


        addFriends();

        this.addFriendsButton = (Button) findViewById(R.id.addFriends);
        this.addFriendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainScreen.this);

                builder.setMessage("Add Friend");

                final EditText addUsername = new EditText(MainScreen.this);
                addUsername.setInputType(InputType.TYPE_CLASS_TEXT);

                builder.setView(addUsername);
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {

                        //Loading bar
                        pd = new ProgressDialog(ctw);
                        pd.setMessage("Loading");
                        pd.show();
                        pd.setCanceledOnTouchOutside(false);

                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {


                                try {
                                    DataBaseInteraction dBInteraction = new DataBaseInteraction(AccountData.URLDB, AccountData.PASS, AccountData.USERNAME);
                                    dBInteraction.connectToDB();

                                    //Introduce in the friend list (prevent the other user)
                                    if (dBInteraction.userExists(addUsername.getText().toString())) {
                                        dBInteraction.addFriend(Connect.username, addUsername.getText().toString());
                                        runOnUiThread(new Runnable() {
                                            public void run() {

                                                updateView();
                                                firstTime = true;
                                                Toast.makeText(getApplicationContext(), "Friend inserted correctly", Toast.LENGTH_SHORT).show();

                                                pd.dismiss();
                                            }
                                        });

                                    } else {
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), "Error: Friend name doesn't exist", Toast.LENGTH_SHORT).show();
                                                pd.dismiss();
                                            }
                                        });
                                    }
                                } catch (SQLException sql) {
                                    System.out.println("SQLException: " + sql.getMessage());
                                    System.out.println("SQLState: " + sql.getSQLState());
                                    System.out.println("Error: " + sql.getErrorCode());
                                    System.out.println("StackTrace: " + sql.getStackTrace());
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "We're sorry we detected an ERROR while connecting", Toast.LENGTH_LONG).show();
                                            pd.dismiss();
                                        }
                                    });

                                }
                            }
                        });
                        thread.start();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //do nothing
                    }
                });
                builder.show();
            }
        });

        this.buttonLogOut = (Button) findViewById(R.id.buttonLogOut);
        this.buttonLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Loading bar
                pd = new ProgressDialog(ctw);
                pd.setMessage("Loading");
                pd.show();
                pd.setCanceledOnTouchOutside(false);

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            DataBaseInteraction dBInteraction = new DataBaseInteraction(AccountData.URLDB, AccountData.PASS, AccountData.USERNAME);
                            dBInteraction.connectToDB();

                            //Change DB isConnected
                            if (dBInteraction.offline(Connect.username)) {
                                pd.dismiss();
                                finish();
                            }
                        } catch (SQLException sql) {
                            System.out.println("SQLException: " + sql.getMessage());
                            System.out.println("SQLState: " + sql.getSQLState());
                            System.out.println("Error: " + sql.getErrorCode());
                            System.out.println("StackTrace: " + sql.getStackTrace());
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "We're sorry we detected an ERROR while connecting", Toast.LENGTH_LONG).show();
                                    pd.dismiss();
                                }
                            });

                        }
                    }
                });
                thread.start();
            }
        });
    }

    private void deleteRows() {
        runOnUiThread(new Runnable() {
            public void run() {
                int count = rl.getChildCount();
                for (int i = 1; i < count; i++) {
                    View child = rl.getChildAt(i);
                    if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
                }
            }
        });

    }


    public void updateView() {
        deleteRows();
        addFriends();
    }

    public void addFriends() {

     /*   Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {*/

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Log.d("GPS thread", "enter");
                    DataBaseInteraction dBInteraction = new DataBaseInteraction(AccountData.URLDB, AccountData.PASS, AccountData.USERNAME);
                    dBInteraction.connectToDB();
                    rl = (TableLayout) findViewById(R.id.friendTable);
                    //modify this to read each friend and calculate GPS distance
                    ArrayList<String> friends = dBInteraction.getFriends(Connect.username);
                    //Stay inside the loop
                    dBInteraction.updatePosition(Connect.username, gps.getLatitude(), gps.getLongitude(), gps.getAltitude());
                    if (friends.size() > 0) {
                        for (String friend : friends) {
                            Location friendLocation = new Location("");
                            //Get position first element latitude, second longitude, third altitude.
                            double[] position = dBInteraction.readPosition(friend);
                            friendLocation.setLatitude(position[0]);
                            friendLocation.setLongitude(position[1]);

                            //save latitude and longitude for the visualization of friends on the map
                            locationFriends.put(friend, friendLocation);

                            distance = gps.calculateDistance(gps.getLocation(), friendLocation);
                            if (distance >= 1000.0) {
                                distance = distance / 1000.0;
                                distance = round(distance, 2);
                                distanceStr = distance.toString() + "km";
                            } else {
                                distance = round(distance, 2);
                                distanceStr = distance.toString() + "m";
                            }
                            if (firstTime) {
                                writeRow(friend, distanceStr);
                                gps.update = true;
                            } else {
                                modifyRowDistance(friend, distanceStr);
                            }
                        }
                        firstTime = false;
                    } else {
                        writeRow("No", "Friends");

                    }
                } catch (SQLException sql) {
                    System.out.println("SQLException: " + sql.getMessage());
                    System.out.println("SQLState: " + sql.getSQLState());
                    System.out.println("Error: " + sql.getErrorCode());
                    System.out.println("StackTrace: " + sql.getStackTrace());
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "We're sorry we detected an ERROR while connecting", Toast.LENGTH_LONG).show();
                        }
                    });

                }


            }
        };

        thread.start();
    }
    // }
       /* }, 0, 10000);
    }*/

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private TableRow createRow(String firstCol, String secCol){
        Log.d("GIACOMO", "PIPPO");
        TextView friendText = new TextView (this);
        friendText.setText(firstCol);
        TextView distanceText = new TextView (this);
        distanceText.setText(secCol);
        TableRow row = new TableRow(this);
        TableRow.LayoutParams tlparamsFriend = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                Gravity.RIGHT
        );
        TableRow.LayoutParams tlparamsDistance = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                Gravity.LEFT
        );
        friendText.setLayoutParams(tlparamsFriend);
        friendText.setTextSize(20);
        distanceText.setTextSize(20);
        distanceText.setLayoutParams(tlparamsDistance);
        row.addView(friendText);
        row.addView(distanceText);
        row.setOnClickListener(this);
        return row;
    }

    private void modifyRowDistance(final String friend, final String newDistance){
        runOnUiThread(new Runnable() {
            public void run() {
                int i=0;
                while(i < rl.getChildCount()) {
                    TableRow v = (TableRow) rl.getChildAt(i);
                    if(friend.equals(v.getChildAt(0).toString())){
                        rl.removeView(v);
                        TableRow newRow = createRow(friend, newDistance);
                        rl.addView(newRow);
                        i = rl.getChildCount();
                    }
                }
            }
        });
    }

    public void writeRow(final String firstCol,final String secCol) {
        runOnUiThread(new Runnable() {
            public void run() {
                rl.addView(

                        createRow(firstCol, secCol));
            }
        });
    }

    @Override
    public void onClick(View v) {

        TableRow t = (TableRow) v;
        TextView friendText = (TextView) t.getChildAt(0);
        friendText.setTextColor(Color.BLACK);


        String friendName = friendText.getText().toString();
        Location friendLocation = (Location) locationFriends.get(friendName);

        gps.stopUsingGPS();
        Intent intent = new Intent(MainScreen.this, MapsActivity.class);
        intent.putExtra("latitude", String.valueOf(friendLocation.getLatitude()));
        intent.putExtra("longitude", String.valueOf(friendLocation.getLongitude()));
        intent.putExtra("friendname", friendName);
        intent.putExtra("distance", distanceStr);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);


    }
}
