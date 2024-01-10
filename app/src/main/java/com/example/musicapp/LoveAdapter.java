package com.example.musicapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class LoveAdapter extends RecyclerView.Adapter<LoveAdapter.MyViewHolder> {
    Context context;
    String userId;
    ArrayList<Artist> artistArrayList;

    private OnUserClickListener clickListener;

    public LoveAdapter(Context context, ArrayList<Artist> artistArrayList, String userId) {
        this.context = context;
        this.userId = userId;
        this.artistArrayList = artistArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.love, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Artist artist = artistArrayList.get(position);
        if (artist == null) {
            return;
        }
        holder.txtArtist.setText(artist.getName());
        holder.txtNumsong.setText(String.valueOf(artist.getNumSong()) + " songs");
        loadImage(artist, holder);
        holder.itemView.setOnClickListener(view -> {
            if (clickListener != null) clickListener.onUserClick(artist);
        });
    }

    private void loadImage(Artist artist, MyViewHolder holder) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference pathReference = storageRef.child("artist/" + artist.getImage());
        pathReference.getDownloadUrl().addOnSuccessListener(uri -> {
            String imageUrl = uri.toString();
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .error(R.drawable.ic_user)
                    .into(holder.imgArtist);
        }).addOnFailureListener(e -> Log.d("TAG", "Failed "));
    }

    @Override
    public int getItemCount() {
        return artistArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgArtist;
        TextView txtArtist, txtNumsong;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgArtist = itemView.findViewById(R.id.imgArtist);
            txtArtist = itemView.findViewById(R.id.txtArtist);
            txtNumsong = itemView.findViewById(R.id.txtNumsong);
        }
    }

    public interface OnUserClickListener {
        void onUserClick(Artist artist);
    }

    public void setOnUserClickListener(OnUserClickListener clickListener) {
        this.clickListener = clickListener;
    }

}
