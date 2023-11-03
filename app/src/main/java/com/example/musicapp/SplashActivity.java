package com.example.musicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    Intent intent = new Intent(SplashActivity.this, LoginSrceenActivity.class);
                    startActivity(intent);
                } else {
                    SharedPreferences sharedPreferences = getSharedPreferences("account", Context.MODE_PRIVATE);
                    String uid = sharedPreferences.getString("uid", null);
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    intent.putExtra("userID",uid);
                    startActivity(intent);
                }
                finish();
            }
        }, 2000);

    }
}