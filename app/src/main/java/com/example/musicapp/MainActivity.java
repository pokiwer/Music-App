package com.example.musicapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {
    TextView txtTitle;
    private BottomNavigationView navigationView;

    private Fragment discoverFragment, albumFragment, loveFragment, userFragment;
    private boolean doubleBackToExitPressedOnce = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);
        Mapping();
        //Khởi tạo các fragment
        discoverFragment = new DiscoverFragment();
        albumFragment = new AlbumFragment();
        loveFragment = new LoveFragment();
        userFragment = new UserFragment();
        navigationInit();
        String userUid = getIntent().getStringExtra("userID");
        Bundle bundle = new Bundle();
        bundle.putString("userID", userUid);
        loveFragment.setArguments(bundle);
        albumFragment.setArguments(bundle);
    }

    private void Mapping() {
        navigationView = findViewById(R.id.navigation);
        txtTitle = findViewById(R.id.txtTitle);
    }

    private void navigationInit() {

        //Click để chuyển các fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, discoverFragment)
                .commit();
        txtTitle.setText("Home");
        navigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                switchFragment(discoverFragment);
                txtTitle.setText("Home");

            } else if (itemId == R.id.nav_album) {
                switchFragment(albumFragment);
                txtTitle.setText("Album");
            } else if (itemId == R.id.nav_love) {
                switchFragment(loveFragment);
                txtTitle.setText("Favourite");
            } else if (itemId == R.id.nav_user) {
                switchFragment(userFragment);
                txtTitle.setText("User");
            }
            return true;
        });
    }

    //Hàm chuyển đổi các fragment
    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .setReorderingAllowed(true)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press Back again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000); // Thời gian để ngăn double click
    }


}


