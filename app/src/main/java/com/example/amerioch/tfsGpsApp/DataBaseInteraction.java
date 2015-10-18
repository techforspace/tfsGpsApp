package com.example.amerioch.tfsGpsApp;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.security.MessageDigest;

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

    public boolean removeFriend(String username, String friend) {

        String insert = "DELETE FROM " +  AccountData.FRIENDSTABLENAME + " WHERE ";
        insert += "`username`='" + username +"' AND `friend`='" + friend + "'";

        try{cmd.executeUpdate(insert);}
        catch(Exception e){Log.d("non scrive nada", e.getMessage() + e.getCause() + insert);return false;}
        return true;
    }

    public ArrayList<String> getFriends(String username){
        ArrayList<String> friends = null;
        //It should return the friends username
        String insert = "SELECT friend FROM "+AccountData.FRIENDSTABLENAME+ " WHERE `username`='" + username + "'";
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

    public ArrayList<String> getFriendsOnline(String username){
        ArrayList<String> friendsOnline = null;
        //It should return the friends username
        String insert = "SELECT friend FROM "+AccountData.FRIENDSTABLENAME+ " INNER JOIN Users ON Friends.friend = Users.username WHERE Friends.username = '"+username+"' and `status`='online'";
        try{
            res = cmd.executeQuery(insert);
            friendsOnline = new ArrayList<String>();
            while(res.next()) {
                friendsOnline.add(res.getString("friend"));
            }
            Log.d("Ramon", "Friends: " + friendsOnline);
        }
        catch(SQLException sql){
            Log.d("Ramon", "SQL error getting friends");
        }

        return friendsOnline;
    }

    public ArrayList<String> getFriendsOffline(String username){
        ArrayList<String> friendsOffline = null;
        //It should return the friends username
        String insert = "SELECT friend FROM "+AccountData.FRIENDSTABLENAME+ " INNER JOIN Users ON Friends.friend = Users.username WHERE Friends.username = '"+username+"' and `status`='offline'";
        try{
            res = cmd.executeQuery(insert);
            friendsOffline = new ArrayList<String>();
            while(res.next()) {
                friendsOffline.add(res.getString("friend"));
            }
            Log.d("Ramon", "Friends: " + friendsOffline);
        }
        catch(SQLException sql){
            Log.d("Ramon", "SQL error getting friends");
        }

        return friendsOffline;
    }

    public String verifyPassword(String password, String username){

        String pass = null;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes("UTF-8"));

            byte bytes[] = md.digest();
            pass = new String(bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String query = "SELECT `username` FROM " + AccountData.USERSTABLENAME + " WHERE `password`='" + pass + "' and `username`='" + username + "'";
        try{
            res = cmd.executeQuery(query);
            res.next();
            password = res.getString("username");

        }catch(Exception e){
            Log.d("non scrive nada", e.getMessage() + e.getCause() + query);
        }
        return password;
    }

    public boolean removeUser(String username) {

        String insert = "DELETE FROM " + AccountData.FRIENDSTABLENAME +" WHERE ";
        insert += "`username`='" + username +"' or `friend` ='"+ username +"'";

        try{cmd.executeUpdate(insert);}
        catch(Exception e){Log.d("non scrive nada", e.getMessage() + e.getCause() + insert);return false;}

        insert = "DELETE FROM " + AccountData.USERSTABLENAME +" WHERE ";
        insert += "`username`='" + username +"'";

        try{cmd.executeUpdate(insert);}
        catch(Exception e){Log.d("non scrive nada", e.getMessage() + e.getCause() + insert);return false;}
        return true;
    }

    public boolean addFriend(String username, String friend) {

        String insert = "INSERT INTO " + AccountData.FRIENDSTABLENAME +"(`username`,";
        insert += "`friend`) VALUES";
        insert += "('" + username + "','" + friend + "')";

        try{cmd.executeUpdate(insert);}
        catch(Exception e){Log.d("non scrive nada", e.getMessage() + e.getCause() + insert);return false;}
        return true;
    }

    public boolean insertNewUser(String username, String password, double lat, double lon, double altitude) throws SQLException{

        String insert = null;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes("UTF-8"));

            byte bytes[] = md.digest();
            String pass = new String(bytes);

            insert = "INSERT INTO " + AccountData.USERSTABLENAME +"(`username`,";
            insert += "`password`, `status`, `lat`, `lon`, `altitude`) VALUES";
            insert += "('" + username + "','" + pass + "','Never Connected'," + lat + "," + lon + "," + altitude +")";
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        try{cmd.executeUpdate(insert);}
        catch(Exception e){Log.d("non scrive nada", e.getMessage() + e.getCause() + insert);return false;}
        return true;
    }

    public boolean updatePosition(String username, double lat, double lon, double altitude) {

        String insert = "UPDATE " + AccountData.USERSTABLENAME +" SET ";
        insert += "`lat`=" + lat + ", `lon`=" + lon + ", `altitude`=" + altitude;
        insert += "WHERE `username` = '" + username +"'";

        try{cmd.executeUpdate(insert);}
        catch(Exception e){Log.d("non scrive nada", e.getMessage() + e.getCause() + insert);return false;}
        return true;
    }

    public boolean online(String username) {

        String insert = "UPDATE " + AccountData.USERSTABLENAME +" SET ";
        insert += "`status`= 'online'";
        insert += "WHERE `username` = '" + username +"'";

        try{cmd.executeUpdate(insert);}
        catch(Exception e){Log.d("non scrive nada", e.getMessage() + e.getCause() + insert);return false;}
        return true;
    }

    public boolean offline(String username) {

        String insert = "UPDATE " + AccountData.USERSTABLENAME +" SET ";
        insert += "`status`= 'offline'";
        insert += "WHERE `username` = '" + username +"'";

        try{cmd.executeUpdate(insert);}
        catch(Exception e){Log.d("non scrive nada", e.getMessage() + e.getCause() + insert);return false;}
        return true;
    }

    public double[] readPosition(String friend){

        String insert = "SELECT lat, lon, altitude FROM " + AccountData.USERSTABLENAME + " WHERE `username`='" + friend + "'";
        String output="No funciona";
        double [] out = null;
        try{
                res = cmd.executeQuery(insert);
            res.next();
            out = new double[3];
            out[0] = res.getDouble("lat");
            out[1] = res.getDouble("lon");
            out[2] = res.getDouble("altitude");
            Log.d("Ramon", "Position: " + out[0] + " AND " + out[1]);
            
            
        }
        catch(Exception e){
            Log.d("Error", e.getMessage());
            return out;
        }
        return out;
    }

    public boolean userExists(String user){

        String insert = "SELECT username FROM " + AccountData.USERSTABLENAME + " WHERE username='" + user + "'";
        try{
            res = cmd.executeQuery(insert);
            res.next();
            String userDB = res.getString("username");
            if (user.equals(userDB)){
                return true;
            }
            else{
                return false;
            }
        }
        catch(Exception e){
            Log.d("Error", e.getMessage());
            return false;
        }
    }

    public boolean isFriend(String username, String friend){
        ArrayList<String> friends =getFriends(username);
        for(String f: friends){
            if(f.equals(friend)){
             return true;
            }
        }
        return false;
    }

    public boolean connectionOK(){
        if(conn == null)
            return false;
        else
            return true;

    }
}