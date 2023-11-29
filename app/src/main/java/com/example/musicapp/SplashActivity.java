package com.example.musicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
                    Intent intent = new Intent(SplashActivity.this, LoginScreenActivity.class);
                    startActivity(intent);
                } else {
                    String uid = user.getUid();
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    intent.putExtra("userID",uid);
                    startActivity(intent);
                }
                finish();
            }
        }, 2000);

    }
}