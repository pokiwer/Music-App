package com.example.musicapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    private ArrayList<Song> songArrayList;
    private RecyclerView recyclerview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        int artistID = intent.getIntExtra("artistID",0);
        songArrayList = new ArrayList<>();
        SongAdapter songAdapter = new SongAdapter(this,songArrayList,2);
        dataInit(songAdapter, artistID);
        recyclerview = findViewById(R.id.rcvSong);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.setAdapter(songAdapter);
        recyclerview.setHasFixedSize(true);
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

    }

}