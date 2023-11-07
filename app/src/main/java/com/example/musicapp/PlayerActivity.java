package com.example.musicapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PlayerActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    ImageView imgSong, btnExit;
    TextView txtSong, txtArtist;
    ImageButton btnPrev, btnPlay, btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        Intent intent = getIntent();
        int songID = intent.getIntExtra("songID", 0);
        Mapping();
        showInfor(songID);
    }
    private void showInfor(int songID) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference songDB = database.getReference("song/" + songID);
        DatabaseReference artistDB = database.getReference("artist/");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        songDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Song song = snapshot.getValue(Song.class);
                txtSong.setText(song.getTitle());
                StorageReference pathReference = storageRef.child("song/" + song.getImage());
                pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(PlayerActivity.this).load(uri.toString()).into(imgSong);
                    }
                });
                artistDB.child(String.valueOf(song.getArtist())).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String artistName = snapshot.child("name").getValue(String.class);
                        txtArtist.setText(artistName);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void Mapping() {
        btnExit = findViewById(R.id.btnExit);
        imgSong = findViewById(R.id.imgSong);
        txtSong = findViewById(R.id.txtSong);
        txtArtist = findViewById(R.id.txtArtist);
        btnPrev = findViewById(R.id.btnPrev);
        btnPlay = findViewById(R.id.btnPlay);
        btnNext = findViewById(R.id.btnNext);
    }
}