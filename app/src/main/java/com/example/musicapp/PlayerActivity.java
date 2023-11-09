package com.example.musicapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import java.util.ArrayList;
import java.util.List;

public class PlayerActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    ImageView imgSong, btnExit;
    TextView txtSong, txtArtist, txtTime, txtDuration;
    SeekBar seekbar;
    ImageButton btnPrev, btnPlay, btnNext;
    private File fileLocal;
    private int index;
    private Song song;
    private ArrayList<Song> songArrayList;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private StorageTask downloadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        Intent intent = getIntent();
        song = (Song) intent.getSerializableExtra("song");
        songArrayList = (ArrayList<Song>) intent.getSerializableExtra("songList");
        index = findSongIndex(songArrayList, song);
        for (int i = 0; i < songArrayList.size(); i++) {
            Song song = songArrayList.get(i);
        }
        Mapping();
        showInfor();
        eventClick();
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

    private void eventClick() {
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    btnPlay.setImageResource(R.drawable.ic_play);
                } else {
                    mediaPlayer.start();
                    btnPlay.setImageResource(R.drawable.ic_pause);
                }
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (index == songArrayList.size() - 1) {
                    index = 0;
                } else index++;
                showInfor();
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (index == 0) {
                    index = songArrayList.size() - 1;
                } else index--;
                showInfor();
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
        
        mediaPlayer = MediaPlayer.create(PlayerActivity.this, R.raw.dragonvslatch);
        mediaPlayer.seekTo(0);
        String duration = duration2String(mediaPlayer.getDuration());
        txtDuration.setText(duration);
        //Xử lí seekbar
        seekbar.setMax(mediaPlayer.getDuration());
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
        //Xử lí cập nhật seekbar theo thời gian
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        try {
                            final double current = mediaPlayer.getCurrentPosition();
                            final String currentTime = duration2String((int) current);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    txtTime.setText(currentTime);
                                    seekbar.setProgress((int) current);
                                }
                            });
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {

                        }
                    }
                }
            }
        }).start();
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
    }
}