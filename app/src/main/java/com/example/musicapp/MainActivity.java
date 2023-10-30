package com.example.musicapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView txtTitle;
    ImageButton btnDiscover, btnAlbum, btnLove, btnUser;
    private Fragment discoverFragment, albumFragment, loveFragment, userFragment;
    private ImageButton selectedButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);
        Mapping();
        BtnControl();
        updateButtonState(btnDiscover,"Discover");
        String userUid = getIntent().getStringExtra("userID");
        Bundle bundle = new Bundle();
        bundle.putString("userID", userUid);
        userFragment.setArguments(bundle);
    }

    private void Mapping()
    {
        btnDiscover = findViewById(R.id.btnDiscover);
        btnAlbum = findViewById(R.id.btnAlbum);
        btnLove = findViewById(R.id.btnLove);
        btnUser = findViewById(R.id.btnUser);
        txtTitle = findViewById(R.id.txtTitle);
    }

    private void BtnControl()
    {
        //Khởi tạo các fragment
        discoverFragment = new DiscoverFragment();
        albumFragment = new AlbumFragment();
        loveFragment = new LoveFragment();
        userFragment = new UserFragment();
        //Click để chuyển các fragment
        btnDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchFragment(discoverFragment, "discover_fragment");
                updateButtonState(btnDiscover, "Discover");
            }
        });

        btnAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchFragment(albumFragment,"album_fragment");
                updateButtonState(btnAlbum,"Album");
            }
        });

        btnLove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchFragment(loveFragment,"love_fragment");
                updateButtonState(btnLove,"Love");
            }
        });

        btnUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchFragment(userFragment,"user_fragment");
                updateButtonState(btnUser,"User");
            }
        });
    }
    //Hàm chuyển đổi các fragment
    private void switchFragment(Fragment fragment, String backStackName) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .setReorderingAllowed(true)
                .addToBackStack(backStackName)
                .commit();
    }

    //Hàm cập nhật trạng thái của các nút điều hướng
    private void updateButtonState(ImageButton button, String title) {
        // Đặt độ trong suốt của nút được chọn thành 1 (đậm lên)
        if (selectedButton != null) {
            selectedButton.setAlpha(0.5f); // Đặt độ trong suốt của nút cũ
        }
        button.setAlpha(1.0f); // Đặt độ trong suốt của nút mới được chọn
        selectedButton = button;
        txtTitle.setText(title);
    }
}


