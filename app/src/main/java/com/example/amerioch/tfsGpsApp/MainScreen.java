package com.example.amerioch.tfsGpsApp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by ramon on 26/07/15.
 */
public class MainScreen extends Activity{
    private TableLayout table;
    private Button addFriendsButton;
    private Button buttonLogOut;
    private GpsClass gps;

    TableLayout rl;
    //Executed when is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);
        gps = new GpsClass(getApplicationContext());

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DataBaseInteraction dBInteraction = new DataBaseInteraction(AccountData.URLDB, AccountData.PASS, AccountData.USERNAME);
                    dBInteraction.connectToDB();
                    rl = (TableLayout) findViewById(R.id.friendTable);
                    //modify this to read each friend and calculate GPS distance
                    ArrayList<String> friends = dBInteraction.getFriends(Connect.username);
                    dBInteraction.updatePosition(Connect.username,gps.getLatitude(),gps.getLongitude(),gps.getAltitude());
                    if(friends.size()>0) {
                        for (String friend : friends) {
                            Location friendLocation = new Location("");
                            //Get position first element latitude, second longitude, third altitude.
                            double[] position = dBInteraction.readPosition(friend);
                            friendLocation.setLatitude(position[0]);
                            friendLocation.setLongitude(position[1]);
                            Double distance = gps.calculateDistance(gps.getLocation(), friendLocation);
                            String distanceStr;
                            if (distance >= 1000.0) {
                                distance = distance / 1000.0;
                                distanceStr = distance.toString() + "km";
                            } else {
                                distanceStr = distance.toString() + "m";
                            }
                            writeRow(friend, distanceStr);
                        }
                    }else{
                        writeRow("No","Friends");
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
        });
        thread.start();

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
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    DataBaseInteraction dBInteraction = new DataBaseInteraction(AccountData.URLDB, AccountData.PASS, AccountData.USERNAME);
                                    dBInteraction.connectToDB();

                                    //Introduce in the friend list (prevent the other user)
                                    if(dBInteraction.userExists(addUsername.getText().toString())) {
                                        dBInteraction.addFriend(Connect.username, addUsername.getText().toString());
                                    }else{
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), "Error: Friend name doesn't exist", Toast.LENGTH_SHORT).show();
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
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            DataBaseInteraction dBInteraction = new DataBaseInteraction(AccountData.URLDB, AccountData.PASS, AccountData.USERNAME);
                            dBInteraction.connectToDB();

                            //Change DB isConnected
                            if(dBInteraction.offline(Connect.username)) {
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
                                }
                            });

                        }
                    }
                });
                thread.start();
            }
        });
    }

    private TableRow createRow(String firstCol, String secCol){
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
        return row;
    }

    private void fillFriendsTableContent(ArrayList<Person> people){
        for(Person p: people){
           createRow(p.name, p.distance);
        }
    }

    public void writeRow(final String firstCol,final String secCol) {
        runOnUiThread(new Runnable() {
            public void run() {
                createRow(firstCol,secCol);
            }
        });
    }

}
