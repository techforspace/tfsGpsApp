
package com.example.amerioch.tfsGpsApp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by ramon on 5/08/15.
 */
public class Register extends Activity{
    private Button buttonSubmit;
    private EditText pass;
    private EditText passConfirmation;
    private EditText user;

    //Progress Dialog
    ContextThemeWrapper ctw;
    ProgressDialog pd;

    //Creedential errors
    ArrayList<Boolean> errors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        //Loading bar style
        ctw = new ContextThemeWrapper(this, R.style.Theme_AppCompat_Light_Dialog);

        errors = new ArrayList<Boolean>();
        errors.add(false);
        errors.add(false);
        errors.add(false);

        this.buttonSubmit = (Button) findViewById(R.id.buttonSubmit);
        this.buttonSubmit.setOnClickListener(new View.OnClickListener() {
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
                            //Add this username and password to the DB
                            pass = (EditText) findViewById(R.id.password);
                            passConfirmation = (EditText) findViewById(R.id.confirmPassword);
                            user = (EditText) findViewById(R.id.username);

                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Log.d("ARRIVA", "Arriva");
                                    user.setBackgroundColor(Color.WHITE);
                                    pass.setBackgroundColor(Color.WHITE);
                                    passConfirmation.setBackgroundColor(Color.WHITE);

                                }
                            });


                            boolean err = controlCredentials(pass.getText().toString(), passConfirmation.getText().toString(), user.getText().toString());

                                if (err) {
                                    DataBaseInteraction dBInteraction = new DataBaseInteraction(AccountData.URLDB, AccountData.PASS, AccountData.USERNAME);
                                    dBInteraction.connectToDB();
                                    try {
                                        if (dBInteraction.insertNewUser(user.getText().toString(), pass.getText().toString(), 0.0, 0.0, 0.0)) {
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    Toast.makeText(getApplicationContext(), "Congratulations you have been correctly registered!", Toast.LENGTH_SHORT).show();
                                                    pd.dismiss();
                                                }
                                            });
                                            finish();
                                        } else {
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    Toast.makeText(getApplicationContext(), "ERROR: Username already exists, try another", Toast.LENGTH_SHORT).show();
                                                    pd.dismiss();
                                                }
                                            });
                                        }
                                    } catch (SQLException sql) {
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), "ERROR: Cannot connect!", Toast.LENGTH_SHORT).show();
                                                pd.dismiss();
                                            }
                                        });
                                    }

                                } else {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            if(errors.get(0)){
                                                Toast.makeText(getApplicationContext(), "ERROR: Passwords don't match", Toast.LENGTH_SHORT).show();
                                                pass.setBackgroundColor(Color.RED);
                                                passConfirmation.setBackgroundColor(Color.RED);
                                            }

                                            if(errors.get(1)){
                                                Toast.makeText(getApplicationContext(), "ERROR: Password less than 9 character", Toast.LENGTH_SHORT).show();
                                                pass.setBackgroundColor(Color.RED);
                                            }

                                            if(errors.get(2)){
                                                Toast.makeText(getApplicationContext(), "ERROR: Username less than 7 character", Toast.LENGTH_SHORT).show();
                                                user.setBackgroundColor(Color.RED);
                                            }

                                            pass.setText("");
                                            passConfirmation.setText("");
                                            user.setText("");
                                            pd.dismiss();
                                        }
                                    });

                                }
                            }catch(SQLException sql){
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

    private Boolean controlCredentials(String password, String confirmPassword, String user) {
        boolean err = true;

        errors.set(0, false);
        errors.set(1, false);
        errors.set(2, false);

        if(!password.equals(confirmPassword)){
            errors.set(0, true);
            err = false;
        }


        if(password.length() < 9){
            errors.set(1, true);
            err = false;
        }

        if(user.length() < 7){
            Log.d("USER", ""+ user.length());
            errors.set(2, true);
            err = false;
        }

        return err;
    }
}
