package com.example.project_album;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class LoginActivity extends AppCompatActivity {
    private final String keyUsername = "username";
    private final String keyPassword = "password";
    private final String keyEmail = "email";
    private final String keyPhone = "phone";
    private final String keyUser = "userkey";
    public String username = "";
    public String password = "";
    private String phone;
    private String email;
    private String userkey;
    public int userID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        MainActivity.dataResource = new DataResource(this);
        MainActivity.dataResource.open();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.login_container, new LoginFragment())
                .commit();

    }

}