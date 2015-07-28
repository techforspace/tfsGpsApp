package com.example.amerioch.tfsGpsApp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextSwitcher;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ramon on 26/07/15.
 */
public class MainScreen extends Activity{
    private TableLayout table;
    //Executed when is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);
        TableLayout rl = (TableLayout) findViewById(R.id.friendTable);
        //this.table=new TableLayout (this);
        rl.addView(createRow("Giacomo", "5km"));
        rl.addView(createRow("Pablo", "100m"));
        rl.addView(createRow("Ramon", "2km"));
        /*TableLayout.LayoutParams tparams = new TableLayout.LayoutParams(
                TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER_VERTICAL);*/
                //USE WHEN DATA RECEIVED: fillFriendsTableContent();
        /*createRow("Giacomo", "5km");
        createRow("Pablo", "100m");
        createRow("Ramon", "2km");*/
       // table.setLayoutParams(tparams);
        //rl.addView(this.table);
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
