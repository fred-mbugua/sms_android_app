package com.payprint.pos;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.payprint.pos.utilities.InternetConnection;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;

import com.payprint.pos.R;

public class Login extends AppCompatActivity{

    //        //where Fred's code starts
    private TextInputLayout loginEmail;
    private TextInputLayout loginPassword;
    public static CheckBox remememberMe;
    private MaterialButton loginbutton;
    public static View parentLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        parentLayout = findViewById(android.R.id.content);

        loginEmail = (TextInputLayout) findViewById(R.id.usernamelogin);
        loginPassword = (TextInputLayout) findViewById(R.id.passwordlogin);
        remememberMe = (CheckBox) findViewById(R.id.loginCheckbox);

        loginbutton = (MaterialButton) findViewById(R.id.loginbtn);

        loginEmail.getEditText().setText(MainActivity.LICENCE_CUSTOMER_EMAIL_ADDRESS);
        loginPassword.getEditText().setText(MainActivity.LICENCE_CUSTOMER_USER_PASSWORD);

        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            String email = loginEmail.getEditText().getText().toString();
            String password = loginPassword.getEditText().getText().toString();
//            String macAddress = "";

            String urlPage= "accounts/remote_device_login.jsp?action=login";

            // checking if the entered text is empty or not.
            if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
//                Toast.makeText(Login.this, "Email and Password must be entered", Toast.LENGTH_SHORT).show();
                Snackbar.make(parentLayout, "Email and Password must be entered", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            } else if (TextUtils.isEmpty(email)){

//                Toast.makeText(Login.this, "Email must be entered", Toast.LENGTH_SHORT).show();
                Snackbar.make(parentLayout, "Email must be entered", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();


            } else if (TextUtils.isEmpty(password)){

//                Toast.makeText(Login.this, "Password must be entered", Toast.LENGTH_SHORT).show();
                Snackbar.make(parentLayout, "Password must be entered", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }  else {


                try{
                    if(InternetConnection.checkConnection(Login.this)) {

                        HashMap<String, String> hashmap = new HashMap<String, String>();
                        hashmap.put("UserEmail", email);
                        hashmap.put("UserPassword", password);
                        // ServerCenterRequestsTool post = new ServerCenterRequestsTool();
                        // System.out.println("Calling login " + Login.this);
                        // post.onAttach(Login.this);
                        // post.loginAttempt(urlPage, hashmap);

                    }else{

                        if(!MainActivity.LICENCE_CUSTOMER_EMAIL_ADDRESS.isEmpty() && !MainActivity.LICENCE_CUSTOMER_USER_PASSWORD.isEmpty()) {
                            new AlertDialog.Builder(Login.this)
                                    .setMessage("You dont have access to internet. Want to proceed offline?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //go to first page of the app after successful login
                                            Intent homepage = new Intent(Login.this, Appfunctionality.class);
                                            startActivity(homepage);
                                            finish();
                                        }
                                    })
                                    .setNegativeButton("No", null)
                                    .show();
                        }else{
                            new AlertDialog.Builder(Login.this)
                                    .setMessage("Please connect to internet and try to login again to proceed")
                                    .setPositiveButton("Ok", null)
                                    .show();
                        }
                    }

                }catch(Exception ioe){
                    ioe.printStackTrace();
                }
            }
        }


        });

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN );

    }

}