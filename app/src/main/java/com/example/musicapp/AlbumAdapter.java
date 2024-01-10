package com.example.musicapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyViewHolder> {
    Context context;
    String userId;
    ArrayList<Song> songArrayList;
    private OnUserClickListener clickListener;

    public AlbumAdapter(Context context, String userId, ArrayList<Song> songArrayList) {
        this.context = context;
        this.userId = userId;
        this.songArrayList = songArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.album, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference artistDB = database.getReference("artist");
        Song album = songArrayList.get(position);
        if (album == null) return;
        artistDB.child(String.valueOf(album.getArtist())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String artistName = snapshot.child("name").getValue(String.class);
                holder.txtArtist.setText(String.valueOf(artistName));
                loadImage(album, holder);
                holder.txtTitle.setText(album.getTitle());
                holder.itemView.setOnClickListener(view -> {
                    if (clickListener != null) clickListener.onUserClick(album);
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadImage(Song album, MyViewHolder holder) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference pathReference = storageRef.child("song/" + album.getImage());
        pathReference.getDownloadUrl().addOnSuccessListener(uri -> {
            String imageUrl = uri.toString();
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .into(holder.imgSong);
        });
    }

    @Override
    public int getItemCount() {
        return songArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgSong;
        TextView txtArtist, txtTitle;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSong = itemView.findViewById(R.id.imgSong);
            txtArtist = itemView.findViewById(R.id.txtArtist);
            txtTitle = itemView.findViewById(R.id.txtTitle);
        }
    }

    public interface OnUserClickListener {
        void onUserClick(Song song);
    }

    public void setOnUserClickListener(AlbumAdapter.OnUserClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
