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
    private ResultSet res;
    private String pass;
    private String dBname;

    public DataBaseInteraction(String url, String pass, String dBname){
        this.url 		= url;
        this.conn 		= null;
        this.cmd 		= null;
        this.res 		= null;
        this.pass       = pass;
        this.dBname     = dBname;

        //Registrazione del Driver JDBC
        try{Class.forName ("com.mysql.jdbc.Driver").newInstance();}
        catch(Exception e){}
    }

    public void connectToDB() {


        try {
            Log.d(Connect.TAG,"Hola ");
            conn =  DriverManager.getConnection(url, pass, dBname);
            //Creiamo un oggetto Statement per poter interrogare il db
            Log.d(Connect.TAG,"Adios");
            cmd = conn.createStatement();
        } catch (Exception e) {
            Log.i("DB connection rejected", "" + e.getMessage() + " " + e.getCause());
        }
    }

    public boolean insertRow(String table, String username, String password, Boolean connected, String ip) {

            String insert = "INSERT INTO " + table +"(`username`,";
            insert += "`password`, `ip`, `connected`) VALUES";
            insert += "('" + username + "'," + password + "," + ip + ","+connected+"')";


            try{cmd.executeUpdate(insert);}
            catch(Exception e){return false;}
            return true;
    }

    public boolean connectionOK(){
            if(conn == null)
                return false;
            else
                return true;

    }
}
