package com.example.amerioch.tfsGpsApp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.text.InputType;
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
import java.util.HashMap;


/**
 * Created by ramon on 26/07/15.
 */
public class MainScreen extends Activity implements View.OnClickListener {
    private TableLayout table;
    private Button addFriendsButton;
    private Button reload;
    private Button buttonLogOut;
    private GpsClass gps;
    Double distance;
    String distanceStr;

    TableLayout tableOnline;
    TableLayout tableOffline;

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

                builder.setMessage("Add / Remove Friend");

                final EditText addRemoveUsername = new EditText(MainScreen.this);
                addRemoveUsername.setInputType(InputType.TYPE_CLASS_TEXT);

                builder.setView(addRemoveUsername);
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
                                    if (dBInteraction.userExists(addRemoveUsername.getText().toString())) {
                                        dBInteraction.addFriend(Connect.username, addRemoveUsername.getText().toString());
                                        runOnUiThread(new Runnable() {
                                            public void run() {

                                                updateView();
                                                Toast.makeText(getApplicationContext(), addRemoveUsername.getText().toString()+" inserted correctly", Toast.LENGTH_SHORT).show();

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

                builder.setNeutralButton("Remove", new DialogInterface.OnClickListener() {

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
                                    if (dBInteraction.isFriend(Connect.username, addRemoveUsername.getText().toString())){
                                        dBInteraction.removeFriend(Connect.username, addRemoveUsername.getText().toString());
                                        runOnUiThread(new Runnable() {
                                            public void run() {

                                                updateView();
                                                Toast.makeText(getApplicationContext(), addRemoveUsername.getText().toString()+" removed", Toast.LENGTH_SHORT).show();
                                                pd.dismiss();
                                            }
                                        });
                                    }else{
                                        runOnUiThread(new Runnable() {
                                            public void run() {

                                                updateView();
                                                Toast.makeText(getApplicationContext(), "Error: " +addRemoveUsername.getText().toString()+ " is not a friend", Toast.LENGTH_SHORT).show();
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
                                //dBInteraction.updatePosition(Connect.username, 0.00, 0.00, 0.00);
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

        this.reload = (Button) findViewById(R.id.reload);
        this.reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateView();
            }
        });

    }

    private void deleteRows(final TableLayout table) {
        runOnUiThread(new Runnable() {
            public void run() {
                if (table != null) {
                    int count = table.getChildCount();
                    for (int i = 1; i < count; i++) {
                        View child = table.getChildAt(i);
                        if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
                    }
                }
            }
        });

    }


    public void updateView() {
        deleteRows(tableOffline);
        deleteRows(tableOnline);
        addFriends();
    }

    public void addFriends() {

        Thread thread = new Thread() {
           @Override
           public void run() {
                try {
                    DataBaseInteraction dBInteraction = new DataBaseInteraction(AccountData.URLDB, AccountData.PASS, AccountData.USERNAME);
                    dBInteraction.connectToDB();
                    tableOnline = (TableLayout) findViewById(R.id.friendTableOnline);
                    tableOffline = (TableLayout) findViewById(R.id.friendTableOffline);
                    //modify this to read each friend and calculate GPS distance
                    ArrayList<String> friendsOnline = dBInteraction.getFriendsOnline(Connect.username);
                    ArrayList<String> friendsOffline = dBInteraction.getFriendsOffline(Connect.username);
                    //Stay inside the loop
                    dBInteraction.updatePosition(Connect.username, gps.getLatitude(), gps.getLongitude(), gps.getAltitude());
                    if(friendsOffline!=null && friendsOnline!=null) {
                        showFriendsOnline(true, friendsOnline, dBInteraction);
                        showFriendsOnline(false, friendsOffline, dBInteraction);
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
    private void showFriendsOnline(final boolean bool, final ArrayList<String> friends, final DataBaseInteraction dBInteraction){
        Thread thread = new Thread() {
            @Override
            public void run() {
                if (bool) {
                    //online friends
                    if (friends.size() > 0) {
                        for (String friend : friends) {
                            Location friendLocation = new Location("");
                            //Get position first element latitude, second longitude, third altitude.
                            double[] position = dBInteraction.readPosition(friend);
                            if(position!=null) {
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
                                writeRow(tableOnline, friend, distanceStr);
                                gps.update = true;
                            }else{
                                writeRow(tableOnline, friend, "ERROR");
                            }
                        }

                    } else {
                        writeRow(tableOnline, "No Friends", "Connected");
                    }
                } else {
                    //Offline friends
                    if (friends.size() > 0) {
                        for (String friend : friends) {
                            Location friendLocation = new Location("");
                            //Get position first element latitude, second longitude, third altitude.
                            double[] position = dBInteraction.readPosition(friend);
                            if(position!=null) {
                                friendLocation.setLatitude(position[0]);
                                friendLocation.setLongitude(position[1]);
                                distance = gps.calculateDistance(gps.getLocation(), friendLocation);
                                if (distance >= 1000.0) {
                                    distance = distance / 1000.0;
                                    distance = round(distance, 2);
                                    distanceStr = distance.toString() + "km";
                                } else {
                                    distance = round(distance, 2);
                                    distanceStr = distance.toString() + "m";
                                }
                                writeRow(tableOffline, friend, distanceStr);
                            }else{
                                writeRow(tableOffline, friend, "ERROR");
                            }
                        }
                    } else {
                        writeRow(tableOffline, "No Friends", "Offline");
                    }
                }
            }
        };

        thread.start();

    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private TableRow createRow(String firstCol, String secCol) {
        TextView friendText = new TextView(this);
        friendText.setText(firstCol);
        TextView distanceText = new TextView(this);
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

    public void writeRow(final TableLayout table,final String firstCol,final String secCol) {
        runOnUiThread(new Runnable() {
            public void run() {
                table.addView(createRow(firstCol, secCol));
            }
        });
    }

    @Override
    public void onClick(View v) {

        TableRow t = (TableRow) v;
        TextView friendText = (TextView) t.getChildAt(0);
        TextView distanceText = (TextView) t.getChildAt(1);
        String distNum = distanceText.getText().toString();
        String friendName = friendText.getText().toString();
        Location friendLocation = (Location) locationFriends.get(friendName);
        if(friendLocation!=null) {
            friendText.setTextColor(Color.BLACK);
            gps.stopUsingGPS();
            Intent intent = new Intent(MainScreen.this, MapsActivity.class);
            intent.putExtra("latitude", String.valueOf(friendLocation.getLatitude()));
            intent.putExtra("longitude", String.valueOf(friendLocation.getLongitude()));
            intent.putExtra("friendname", friendName);
            intent.putExtra("distance", distNum);
            intent.putExtra("currentLat", String.valueOf(gps.getLatitude()));
            intent.putExtra("currentLong", String.valueOf(gps.getLongitude()));

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

    }
}
