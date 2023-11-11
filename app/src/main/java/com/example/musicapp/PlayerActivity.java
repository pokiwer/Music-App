package com.example.musicapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    ImageView imgSong, btnExit;
    TextView txtSong, txtArtist, txtTime, txtDuration;
    SeekBar seekbar;
    ImageButton btnPrev, btnPlay, btnNext, btnRepeat, btnAddMusic;
    private File fileLocal;
    private int index, buttonState = 0;
    private Song song;
    private ArrayList<Song> songArrayList;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        Intent intent = getIntent();
        song = (Song) intent.getSerializableExtra("song");
        songArrayList = (ArrayList<Song>) intent.getSerializableExtra("songList");
        index = findSongIndex(songArrayList, song);
        Mapping();
        showInfor();
        eventClick();
    }

    //Xử lí lặp bài hát
    private void handleRepeat() {
        if (mediaPlayer != null) {
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    // Xử lý theo trạng thái nút repeat
                    switch (buttonState) {
                        case 0:
                            btnNext.performClick();
                            break;
                        case 1:
                            mediaPlayer.start();
                            break;
                        case 2:
                            mediaPlayer.stop();
                            break;
                    }
                }
            });
        }
    }


    //Tìm index của bài hát
    private int findSongIndex(ArrayList<Song> songArrayList, Song current) {
        for (int i = 0; i < songArrayList.size(); i++) {
            Song song = songArrayList.get(i);
            if (song.getId() == (current.getId())) {
                return i;
            }
        }
        return -1;
    }

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
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                String currentTime = duration2String(currentPosition);
                // Cập nhật giao diện người dùng trên UI Thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtTime.setText(currentTime);
                        seekbar.setProgress(currentPosition);
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

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void eventClick() {
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    btnPlay.setImageResource(R.drawable.ic_play);
                } else if (mediaPlayer != null) {
                    mediaPlayer.start();
                    btnPlay.setImageResource(R.drawable.ic_pause);
                }
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                    }
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                if (index == songArrayList.size() - 1) {
                    index = 0;
                } else index++;
                showInfor();
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                    }
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                if (index == 0) {
                    index = songArrayList.size() - 1;
                } else index--;
                showInfor();
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
                handleRepeat();
            }
        });
    }

    //Hiển thị thông tin bài hát
    private void showInfor() {
        Song current = songArrayList.get(index);
        database = FirebaseDatabase.getInstance();
        DatabaseReference songDB = database.getReference("song/" + current.getId());
        DatabaseReference artistDB = database.getReference("artist");
        storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference audioUrl = storageRef.child("song");
        txtSong.setText(current.getTitle());
        songDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int artistID = snapshot.child("artist").getValue(Integer.class);
                artistDB.child(String.valueOf(artistID)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Artist artist = snapshot.getValue(Artist.class);
                        txtArtist.setText(artist.getName());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                //Load ảnh
                audioUrl.child(current.getImage()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(PlayerActivity.this).load(uri.toString()).into(imgSong);
                    }
                });
                //Load mp3
                loadAudio(current.getName(), audioUrl);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadAudio(String name, StorageReference audioUrl) {
        mediaPlayer = new MediaPlayer();
        audioUrl.child(name).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try {
                    mediaPlayer.setDataSource(uri.toString());
                    mediaPlayer.prepare();
                    mediaPlayer.seekTo(0);
                    seekbar.setProgress(0);
                    txtTime.setText("0:00");
                    mediaPlayer.start();
                    btnPlay.setImageResource(R.drawable.ic_pause);
                    String duration = duration2String(mediaPlayer.getDuration());
                    txtDuration.setText(duration);
                    handleRepeat();
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            btnPlay.setImageResource(R.drawable.ic_play);
                        }
                    });
                    //Xử lí seekbar
                    seekbar.setMax(mediaPlayer.getDuration());
                    // Bắt đầu cập nhật seekbar và thời gian
                    startUpdatingSeekBarAndTime();
                    seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int process, boolean isFromUser) {
                            if (isFromUser) {
                                mediaPlayer.seekTo(process);
                                seekbar.setProgress(process);
                            }
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }
                    });

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
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
}