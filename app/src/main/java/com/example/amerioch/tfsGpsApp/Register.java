package com.example.amerioch.tfsGpsApp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by ramon on 5/08/15.
 */
public class Register extends Activity{
    private Button buttonSubmit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        this.buttonSubmit = (Button) findViewById(R.id.buttonSubmit);
        this.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    //Add this username and password to the DB
                EditText pass = (EditText) findViewById(R.id.password);
                EditText passConfirmation = (EditText) findViewById(R.id.confirmPassword);
                EditText user = (EditText) findViewById(R.id.username);

                if ((pass.getText().toString().equals(passConfirmation.getText().toString()))) {
                    try {
                        DataBaseInteraction dBInteraction = new DataBaseInteraction(AccountData.URLDB, AccountData.PASS, AccountData.USERNAME);
                        dBInteraction.connectToDB();
                        try{
                            dBInteraction.insertNewUser(AccountData.USERSTABLENAME, user.getText().toString(), pass.getText().toString(), false, 0.0, 0.0);
                            Toast.makeText(getApplicationContext(), "Congratulations you have been correctly registered!",
                                    Toast.LENGTH_LONG).show();
                        }catch(SQLException sql){
                            Toast.makeText(getApplicationContext(), "ERROR: Username already exists",
                                    Toast.LENGTH_LONG).show();
                        }
                        finish();
                    } catch (SQLException sql){
                        pass.setText("");
                        passConfirmation.setText("");
                        user.setText("");
                        System.out.println("SQLException: " + sql.getMessage());
                        System.out.println("SQLState: " + sql.getSQLState());
                        System.out.println("Error: " + sql.getErrorCode());
                        System.out.println("StackTrace: " + sql.getStackTrace());
                        Toast.makeText(getApplicationContext(), "We're sorry we detected an ERROR while connecting",
                                Toast.LENGTH_LONG).show();
                    }
                }else{
                    pass.setText("");
                    passConfirmation.setText("");
                    user.setText("");
                    Toast.makeText(getApplicationContext(), "ERROR: Passwords don't match",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
