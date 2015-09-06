package com.example.amerioch.tfsGpsApp;

import android.util.Log;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by plnaspa on 8/07/15.
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


    public void connectToDB() throws SQLException{
        conn = DriverManager.getConnection(url, username, pass);
        Log.d("RAMON",conn+" ");
        if(conn!=null) {
            //Creation of a Statement object to be able to connect to the DB
            cmd = conn.createStatement();
        }
    }

    public boolean removeFriend(String table, String username, String friend) {

        String insert = "DELETE FROM " + table +" WHERE ";
        insert += "`username`='" + username +"' AND `friend`='" + friend + "'";

        try{cmd.executeUpdate(insert);}
        catch(Exception e){Log.d("non scrive nada", e.getMessage() + e.getCause() + insert);return false;}
        return true;
    }

    public ArrayList<String> getFriends(String table, String username){
        ArrayList<String> friends = null;
        //It should return the friends username
        String insert = "SELECT friend FROM "+table+ " WHERE `username`='" + username + "'";
        try{
            res = cmd.executeQuery(insert);
            friends = new ArrayList<String>();
            while(res.next()) {
                friends.add(res.getString("friend"));
            }
            Log.d("Ramon", "Friends: " + friends);
        }
        catch(SQLException sql){
            Log.d("Ramon", "SQL error getting friends");
        }

        return friends;
    }

    public String getPassword(String table, String username){
        String password = null;

        String query = "SELECT `password` FROM " + AccountData.USERSTABLENAME + " WHERE `username`='" + username + "'";
        query = "SELECT `password` FROM Users WHERE `username`='a'";

        try{
            res = cmd.executeQuery(query);
            password = res.getString("password");

        }
        catch(Exception e){Log.d("non scrive nada", e.getMessage() + e.getCause() + query);}
        return password;
    }

    public boolean removeUser(String table, String username, String friend) {

        String insert = "DELETE FROM " + table +" WHERE ";
        insert += "`username`='" + username +"'";

        try{cmd.executeUpdate(insert);}
        catch(Exception e){Log.d("non scrive nada", e.getMessage() + e.getCause() + insert);return false;}
        return true;
    }

    public boolean addFriend(String table, String username, String friend) {

        String insert = "INSERT INTO " + table +"(`username`,";
        insert += "`friend`) VALUES";
        insert += "('" + username + "','" + friend + "')";

        try{cmd.executeUpdate(insert);}
        catch(Exception e){Log.d("non scrive nada", e.getMessage() + e.getCause() + insert);return false;}
        return true;
    }

    public boolean insertNewUser(String table, String username, String password, boolean connected, double lat, double lon, double altitude) throws SQLException{

        String insert = "INSERT INTO " + table +"(`username`,";
        insert += "`password`, `status`, `lat`, `lon`, `altitude`) VALUES";
        insert += "('" + username + "','" + password + "','" + connected +  "'," + lat + "," + lon + "," + altitude +")";


        try{cmd.executeUpdate(insert);}
        catch(Exception e){Log.d("non scrive nada", e.getMessage() + e.getCause() + insert);return false;}
        return true;
    }

    public boolean updatePosition(String table, String username, double lat, double lon, double altitude) {

        String insert = "UPDATE " + table +" SET ";
        insert += "`lat`=" + lat + ", `lon`=" + lon + ", `altitude`=" + altitude;
        insert += "WHERE `username` = '" + username +"'";

        try{cmd.executeUpdate(insert);}
        catch(Exception e){Log.d("non scrive nada", e.getMessage() + e.getCause() + insert);return false;}
        return true;
    }

    public boolean online(String table, String username) {

        String insert = "UPDATE " + table +" SET ";
        insert += "`status`= 'online'";
        insert += "WHERE `username` = '" + username +"'";

        try{cmd.executeUpdate(insert);}
        catch(Exception e){Log.d("non scrive nada", e.getMessage() + e.getCause() + insert);return false;}
        return true;
    }

    public boolean offline(String table, String username) {

        String insert = "UPDATE " + table +" SET ";
        insert += "`status`= 'offline'";
        insert += "WHERE `username` = '" + username +"'";

        try{cmd.executeUpdate(insert);}
        catch(Exception e){Log.d("non scrive nada", e.getMessage() + e.getCause() + insert);return false;}
        return true;
    }

    public double[] readPosition(String friend){

        String insert = "SELECT lat, lon, latitude FROM users WHERE `username`='" + friend + "'";
        String output="No funciona";
        double [] out = null;
        try{
            res = cmd.executeQuery(insert);
            out = new double[3];
            out[0] = res.getDouble("lat");
            out[1] = res.getDouble("lon");
            out[2] = res.getDouble("altitude");
            Log.d("Ramon", "Position: " + out[0] + " AND " + out[1]);
            
            
        }
        catch(Exception e){
            return out;
        }
        return out;
    }

    public boolean connectionOK(){
        if(conn == null)
            return false;
        else
            return true;

    }
}