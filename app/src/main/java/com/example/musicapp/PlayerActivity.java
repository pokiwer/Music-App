package com.example.musicapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Map;


public class PlayerActivity extends AppCompatActivity {

    ImageView imgSong, btnExit;
    TextView txtSong, txtArtist, txtTime, txtDuration;
    SeekBar seekbar;
    ImageButton btnPrev, btnPlay, btnNext, btnRepeat, btnAddMusic;
    private int buttonState = 0, mediaDuration, position, rewind;
    private Song song;
    private Bitmap bitmap;
    private String artistName;
    private boolean isAdded, isPlaying;
    private FirebaseDatabase database;
    private Handler handler = new Handler();
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle == null) return;
            song = (Song) bundle.get("song");
            int action = bundle.getInt("action");
            artistName = bundle.getString("artist");
            bitmap = bundle.getParcelable("bitmap");
            mediaDuration = bundle.getInt("duration", 0);
            position = bundle.getInt("position", 0);
            isPlaying = bundle.getBoolean("isPlaying");
            handleAction(action);
            showInfor();
        }
    };

    private void handleAction(int action) {
        switch (action) {
            case PlayerService.ACTION_PLAY:
                btnPlay.setImageResource(R.drawable.ic_pause);
                break;
            case PlayerService.ACTION_PAUSE:
                btnPlay.setImageResource(R.drawable.ic_play);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("send_data"));
        Mapping();
    }

    //Xử lí lặp bài hát
//    private void handleRepeat() {
//        if (mediaPlayer != null) {
//            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mp) {
//                    // Xử lý theo trạng thái nút repeat
//                    switch (buttonState) {
//                        case 0:
//                            btnNext.performClick();
//                            break;
//                        case 1:
//                            mediaPlayer.start();
//                            break;
//                        case 2:
//                            btnPlay.setImageResource(R.drawable.ic_play);
//                            mediaPlayer.pause();
//                            break;
//                    }
//                }
//            });
//        }
//    }


    //Xử lí hiển thị khung thời gian
    private String duration2String(int duration) {
        String ellapsedTime = "";
        int minutes = duration / 1000 / 60;
        int seconds = duration / 1000 % 60;
        ellapsedTime = minutes + ":";
        if (seconds < 10) {
            ellapsedTime += "0";
        }
        ellapsedTime += seconds;
        return ellapsedTime;
    }

    private Runnable updateSeekBarAndTime = new Runnable() {
        @Override
        public void run() {
            if (isPlaying) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Cập nhật SeekBar và TextView ứng với giá trị mới của position
                        seekbar.setProgress(position);
                        txtTime.setText(duration2String(position));
                    }
                });
            }
            // Lập lịch chạy lại Runnable sau 1000ms (1 giây)
            handler.postDelayed(this, 1000);
        }
    };

    private void startUpdatingSeekBarAndTime() {
        handler.postDelayed(updateSeekBarAndTime, 1000);
    }

    // Bổ sung phương thức này để dừng việc cập nhật seekbar và thời gian
    private void stopUpdatingSeekBarAndTime() {
        handler.removeCallbacks(updateSeekBarAndTime);
    }

    protected void onDestroy() {
        super.onDestroy();
        stopUpdatingSeekBarAndTime();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    private void eventClick(DatabaseReference albumDB, boolean isAdded, int songID) {
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPlaying) {
                    sendActionToService(PlayerService.ACTION_PAUSE);
                } else {
                    sendActionToService(PlayerService.ACTION_PLAY);
                }
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendActionToService(PlayerService.ACTION_NEXT);
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendActionToService(PlayerService.ACTION_PREV);
            }
        });
        btnRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (buttonState) {
                    case 0:
                        buttonState = 1;
                        btnRepeat.setImageResource(R.drawable.ic_repeat_one);
                        btnRepeat.setAlpha(1f);
                        break;
                    case 1:
                        buttonState = 2;
                        btnRepeat.setAlpha(0.5f);
                        btnRepeat.setImageResource(R.drawable.ic_repeat);
                        break;
                    case 2:
                        buttonState = 0;
                        btnRepeat.setImageResource(R.drawable.ic_repeat);
                        btnRepeat.setAlpha(1f);
                        break;
                }
//                handleRepeat();
            }
        });
        btnAddMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAdded) {
                    albumDB.child(String.valueOf(songID)).removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            Toast.makeText(PlayerActivity.this, "Removed from album", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Map<String, Object> dataMap = new HashMap<>();
                    dataMap.put(String.valueOf(songID), true);
                    albumDB.updateChildren(dataMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            Toast.makeText(PlayerActivity.this, "Added to album", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayerActivity.this.finish();
            }
        });
    }

    //Hiển thị thông tin bài hát
    private void showInfor() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userUID = user.getUid();
        database = FirebaseDatabase.getInstance();
        DatabaseReference albumDB = database.getReference("album/" + userUID + "/song");
        txtSong.setText(song.getTitle());
        txtArtist.setText(artistName);
        imgSong.setImageBitmap(bitmap);
        albumDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(String.valueOf(song.getId()))) {
                    isAdded = true;
                    btnAddMusic.setImageResource(R.drawable.ic_added_music);
                } else {
                    isAdded = false;
                    btnAddMusic.setImageResource(R.drawable.ic_add_music);
                }
                eventClick(albumDB, isAdded, song.getId());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        loadAudio();

    }

    private void loadAudio() {
        seekbar.setProgress(position);
        txtTime.setText(duration2String(position));
        String duration = duration2String(mediaDuration);
        txtDuration.setText(duration);
        seekbar.setMax(mediaDuration);
        // Bắt đầu cập nhật seekbar và thời gian
        startUpdatingSeekBarAndTime();
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int process, boolean isFromUser) {
                if (isFromUser) {
                    rewind = process;
                    sendActionToService(PlayerService.ACTION_REWIND);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    //Ánh xạ id
    private void Mapping() {
        btnExit = findViewById(R.id.btnExit);
        imgSong = findViewById(R.id.imgSong);
        txtSong = findViewById(R.id.txtSong);
        txtTime = findViewById(R.id.txtTime);
        txtDuration = findViewById(R.id.txtDuration);
        seekbar = findViewById(R.id.seekbar);
        txtArtist = findViewById(R.id.txtArtist);
        btnPrev = findViewById(R.id.btnPrev);
        btnPlay = findViewById(R.id.btnPlay);
        btnNext = findViewById(R.id.btnNext);
        btnRepeat = findViewById(R.id.btnRepeat);
        btnAddMusic = findViewById(R.id.btnAddMusic);
    }

    private void sendActionToService(int action) {
        Intent intent = new Intent(this, PlayerService.class);
        intent.putExtra("action", action);
        intent.putExtra("song", song);
        intent.putExtra("rewind", rewind);
        startService(intent);
    }
}