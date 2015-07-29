package com.example.amerioch.tfsGpsApp;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by ramon on 8/07/15.
 */
public class DataBaseInteraction {

    private String 		url;
    private Connection conn;
    private Statement cmd;
    private String pass;
    private String username;
    private  ResultSet res;

    public DataBaseInteraction(String url, String pass, String username){
        this.url 		= url;
        this.conn 		= null;
        this.cmd 		= null;
        this.pass       = pass;
        this.username   = username;

        //Registrazione del Driver JDBC
        try{Class.forName ("com.mysql.jdbc.Driver").newInstance();}
        catch(Exception e){Log.d("non carica", "jdbc");}
    }


    public void connectToDB() {
        try {
            conn = DriverManager.getConnection(url, username, pass);
            //Creiamo un oggetto Statement per poter interrogare il db
            cmd = conn.createStatement();
        } catch (Exception e) {
            Log.i("DB connection rejected", "" + e.getMessage() + " " + e.getCause());
        }
        if(cmd!=null)
            Log.d("comando command","not null");
        else
            Log.d("comando command","null");

    }

    public boolean insertRow(String table, String username, String password, boolean connected, String ip) {

        String insert = "INSERT INTO " + table +"(`username`,";
        insert += "`password`, `ip`, `status`) VALUES";
        insert += "('" + username + "','" + password + "','" + ip + "'," + connected + ")";


        try{cmd.executeUpdate(insert);}
        catch(Exception e){Log.d("non scrive nada", e.getMessage() + e.getCause() + insert);return false;}
        return true;
    }

    public boolean readData (String friend){

        String insert = "SELECT password FROM users WHERE `username`='" + friend + "'";
        String output="No funciona";
        try{
            res = cmd.executeQuery(insert);
            if(res.next())
                output = res.getString("password");
                Log.d("Ramon", output);
        }
        catch(Exception e){
            return false;
        }
        return true;
    }

    public boolean connectionOK(){
        if(conn == null)
            return false;
        else
            return true;

    }
}