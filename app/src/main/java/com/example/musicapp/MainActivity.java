package com.example.musicapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    TextView txtTitle;
    private BottomNavigationView navigationView;

    private Fragment discoverFragment, albumFragment, loveFragment, userFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);
        Mapping();
        navigationInit();
        String userUid = getIntent().getStringExtra("userID");
        Bundle bundle = new Bundle();
        bundle.putString("userID", userUid);
        userFragment.setArguments(bundle);
        loveFragment.setArguments(bundle);
        albumFragment.setArguments(bundle);
    }

    private void Mapping()
    {
        navigationView = findViewById(R.id.navigation);
        txtTitle = findViewById(R.id.txtTitle);
    }

    private void navigationInit()
    {
        //Khởi tạo các fragment
        discoverFragment = new DiscoverFragment();
        albumFragment = new AlbumFragment();
        loveFragment = new LoveFragment();
        userFragment = new UserFragment();
        //Click để chuyển các fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, discoverFragment)
                .commit();
        txtTitle.setText("Home");
        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    switchFragment(discoverFragment);
                    txtTitle.setText("Home");

                } else if (itemId == R.id.nav_album) {
                    switchFragment(albumFragment);
                    txtTitle.setText("Category");
                } else if (itemId == R.id.nav_love) {
                    switchFragment(loveFragment);
                    txtTitle.setText("Love");
                } else if (itemId == R.id.nav_user) {
                    switchFragment(userFragment);
                    txtTitle.setText("User");
                }
                return true;
            }
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
}


