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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class FamousAdapter extends RecyclerView.Adapter<FamousAdapter.MyViewHolder> {
    Context context;
    ArrayList<Song> famousArrayList;

    private FamousAdapter.OnUserClickListener clickListener;

    public FamousAdapter(Context context, ArrayList<Song> famousArrayList) {
        this.context = context;
        this.famousArrayList = famousArrayList;
    }

    @NonNull
    @Override
    public FamousAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.famous, parent, false);
        return new FamousAdapter.MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull FamousAdapter.MyViewHolder holder, int position) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference artistdb = database.getReference("artist");
        Song song = famousArrayList.get(position);
        if (song == null) {
            return;
        }
        int artistID = song.getArtist();
        if (artistID != 0) {
            artistdb.child(String.valueOf(artistID)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String artistName = snapshot.child("name").getValue(String.class);
                        holder.txtArtist.setText(artistName);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            holder.txtArtist.setText("Unknow artist");
        }
        holder.txtTitle.setText(song.getTitle());
        loadImage(song, holder);
        holder.itemView.setOnClickListener(view -> {
            if (clickListener != null) clickListener.onUserClick(song);
        });
    }

    private void loadImage(Song song, MyViewHolder holder) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference pathReference = storageRef.child("song/" + song.getImage());
        pathReference.getDownloadUrl().addOnSuccessListener(uri -> {
            String imageUrl = uri.toString();
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .error(R.drawable.ic_user)
                    .into(holder.imgSong);
        }).addOnFailureListener(e -> Log.d("TAG", "Failed "));
    }

    @Override
    public int getItemCount() {
        int size = 10;
        return Math.min(famousArrayList.size(),size);
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

    public void setOnUserClickListener(FamousAdapter.OnUserClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
