package com.example.musicapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    private ArrayList<Song> songArrayList;
    private RecyclerView recyclerview;
    ImageView btnReturn, btnFollow, imgArtist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        int artistID = intent.getIntExtra("artistID",0);
        songArrayList = new ArrayList<>();
        SongAdapter songAdapter = new SongAdapter(this,songArrayList,2);
        dataInit(songAdapter, artistID);
        Mapping();
        evenClick(artistID);
    }

    private void Mapping() {
        btnReturn = findViewById(R.id.btnReturn);
        btnFollow = findViewById(R.id.btnFollow);
        imgArtist = findViewById(R.id.imgArtist);
    }

    private void evenClick(int artistID) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference artistDB = database.getReference("artist/"+artistID);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        artistDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String imageName = snapshot.child("image").getValue(String.class);
                StorageReference pathReference = storageRef.child("artist/" + imageName);
                pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(DetailActivity.this).load(uri.toString()).into(imgArtist);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void dataInit(SongAdapter songAdapter, int artistID) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference songDB = database.getReference("song");
        songDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Song song = snapshot.getValue(Song.class);
                if (song != null && song.getArtist() == artistID)
                {
                    songArrayList.add(song);
                    songAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        recyclerview = findViewById(R.id.rcvSong);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.setAdapter(songAdapter);
        recyclerview.setHasFixedSize(true);
        songAdapter.setOnUserClickListener(new SongAdapter.OnUserClickListener() {
            @Override
            public void onUserClick(Song song) {
                Intent intent = new Intent(DetailActivity.this, PlayerActivity.class);
                intent.putExtra("song", song);
                intent.putExtra("songList",songArrayList);
                startActivity(intent);
            }
        });

    }

}