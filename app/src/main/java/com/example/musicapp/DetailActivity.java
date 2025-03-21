package com.example.musicapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
public class DetailActivity extends AppCompatActivity {

    private ArrayList<Song> songArrayList;
    private RecyclerView recyclerview;
    private boolean isFollow;
    private int numFollow;
    ImageView btnReturn, btnFollow, imgArtist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userUid = user.getUid();
        Intent intent = getIntent();
        int artistID = intent.getIntExtra("artistID", 0);
        songArrayList = new ArrayList<>();
        SongAdapter songAdapter = new SongAdapter(this, songArrayList, 2);
        dataInit(songAdapter, artistID);
        Mapping();
        showInfor(artistID, userUid);
    }

    private void Mapping() {
        btnReturn = findViewById(R.id.btnReturn);
        btnFollow = findViewById(R.id.btnFollow);
        imgArtist = findViewById(R.id.imgArtist);
    }

    private void showInfor(int artistID, String userUid) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference artistDB = database.getReference("artist/" + artistID);
        DatabaseReference followDB = database.getReference("follow/" + userUid + "/artist");
        followDB.child(String.valueOf(artistID)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    btnFollow.setAlpha(1f);
                    btnFollow.setColorFilter(Color.parseColor("#ff0000"), PorterDuff.Mode.SRC_ATOP);
                    isFollow = true;
                } else {
                    btnFollow.setAlpha(0.5f);
                    btnFollow.setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_ATOP);
                    isFollow = false;
                }
                handleClick(isFollow, followDB, artistID, artistDB);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        artistDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String imageName = snapshot.child("image").getValue(String.class);
                StorageReference pathReference = storageRef.child("artist/" + imageName);
                pathReference.getDownloadUrl().addOnSuccessListener(uri -> Glide.with(DetailActivity.this).load(uri.toString()).into(imgArtist));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void handleClick(boolean isFollow, DatabaseReference followDB, int artistID, DatabaseReference artistDB) {
        btnFollow.setOnClickListener(view -> {
            if (isFollow) {
                followDB.child(String.valueOf(artistID)).removeValue((error, ref) -> Toast.makeText(DetailActivity.this, "Delete from favourite", Toast.LENGTH_SHORT).show());
                artistDB.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        numFollow = snapshot.child("numFollow").getValue(Integer.class);
                        artistDB.child("numFollow").setValue(numFollow - 1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            } else {
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put(String.valueOf(artistID),true);
                followDB.updateChildren(dataMap, (error, ref) -> Toast.makeText(DetailActivity.this, "Added to favourite", Toast.LENGTH_SHORT).show());
                artistDB.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        numFollow = snapshot.child("numFollow").getValue(Integer.class);
                        artistDB.child("numFollow").setValue(numFollow + 1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        btnReturn.setOnClickListener(view -> onBackPressed());
    }

    private void dataInit(SongAdapter songAdapter, int artistID) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference songDB = database.getReference("song");
        songDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Song song = snapshot.getValue(Song.class);
                if (song != null && song.getArtist() == artistID) {
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
        songAdapter.setOnUserClickListener(song -> {
            Intent intent = new Intent(DetailActivity.this, PlayerService.class);
            intent.putExtra("song", song);
            intent.putExtra("songList", songArrayList);
            intent.putExtra("isOpen",true);
            startService(intent);
        });
    }
}