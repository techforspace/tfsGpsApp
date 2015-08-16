package com.example.amerioch.tfsGpsApp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by ramon on 26/07/15.
 */
public class MainScreen extends Activity{
    private TableLayout table;
    private Button addFriendsButton;
    private Button buttonLogOut;
    //Executed when is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);
        TableLayout rl = (TableLayout) findViewById(R.id.friendTable);
        //modify this to read each friend and calculate GPS distance
        /*
        **
         */
        rl.addView(createRow("Giacomo", "5km"));
        rl.addView(createRow("Pablo", "100m"));
        rl.addView(createRow("Ramon", "2km"));
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
                        //Introduce in the friend list (prevent the other user)
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
                //Change DB isConnected
                finish();
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

}
