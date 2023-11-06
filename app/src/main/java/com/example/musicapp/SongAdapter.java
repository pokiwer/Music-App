package com.example.musicapp;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.MyViewHolder> {
    Context context;
    ArrayList<Song> songArrayList;
    int type;
    private SongAdapter.OnUserClickListener clickListener;
    public SongAdapter(Context context, ArrayList<Song> songArrayList, int type) {
        this.context = context;
        this.songArrayList = songArrayList;
        this.type = type;
    }

    @NonNull
    @Override
    public SongAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.song, parent, false);
        return new SongAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongAdapter.MyViewHolder holder, int position) {
        Song song = songArrayList.get(position);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference artistDB = database.getReference("artist/" + song.getArtist());
        if (song == null){
            return;
        }
        //type = 1 => list tổng hợp tất cả bài hát
        if (type == 1){
            artistDB.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists())
                    {
                        String artistName = snapshot.child("name").getValue(String.class);
                        holder.txtArtist.setText(artistName);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            holder.txtTitle.setText(song.getTitle());
            loadImage(song,holder);
        }
        //type = 2 => list các bài hát được theo dõi
        else if (type == 2) {

                artistDB.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = snapshot.child("name").getValue(String.class);
                        holder.txtArtist.setText(name);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                holder.txtTitle.setText(song.getTitle());
                loadImage(song,holder);
            }

    }

    private void loadImage(Song song, MyViewHolder holder) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference pathReference = storageRef.child("song/" + song.getImage());
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String imageUrl = uri.toString();
                Glide.with(holder.itemView.getContext())
                        .load(imageUrl)
                        .error(R.drawable.ic_user)
                        .into(holder.imgSong);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "Failed ");
            }
        });
    }

    @Override
    public int getItemCount() {
        return songArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgSong;
        TextView txtTitle, txtArtist;
        ImageButton btnSong;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSong = itemView.findViewById(R.id.imgSong);
            txtArtist = itemView.findViewById(R.id.txtArtist);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            btnSong = itemView.findViewById(R.id.btnSong);
        }
    }
    public interface OnUserClickListener {
        void onUserClick(Song song);
    }
    public void setOnUserClickListener(SongAdapter.OnUserClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
