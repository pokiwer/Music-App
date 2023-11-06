package com.example.musicapp;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder> {
    Context context;
    ArrayList<Song> newArrayList;

    public NewsAdapter(Context context, ArrayList<Song> newArrayList) {
        this.context = context;
        this.newArrayList = newArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.news, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference artistDB = database.getReference("artist");
        Song song = newArrayList.get(position);
        if (song == null){
            return;
        }
        int artistID = song.getArtist();
        if (artistID != 0)
        {
            artistDB.child(String.valueOf(artistID)).addListenerForSingleValueEvent(new ValueEventListener() {
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
        }
        else {
            holder.txtArtist.setText("Unknow artist");
        }
        holder.txtTitle.setText(song.getTitle());
        loadImage(song,holder);
    }

    private void loadImage(Song news, MyViewHolder holder) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference pathReference = storageRef.child("song/" + news.getImage());
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
        return newArrayList.size();
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
}
