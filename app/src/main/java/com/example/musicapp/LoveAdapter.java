package com.example.musicapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LoveAdapter extends RecyclerView.Adapter<LoveAdapter.MyViewHolder> {
    Context context;
    ArrayList<Love> loveArrayList;

    private OnUserClickListener clickListener;
    public LoveAdapter(Context context, ArrayList<Love> loveArrayList) {
        this.context = context;
        this.loveArrayList = loveArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.love, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Love love = loveArrayList.get(position);
        holder.imgArtist.setImageResource(love.getResourceID());
        holder.txtArtist.setText(love.getName());
        holder.txtNumsong.setText(love.getNumSong());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(clickListener != null) clickListener.onUserClick(love);
            }
        });
    }

    @Override
    public int getItemCount() {
        return loveArrayList.size();
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
        void onUserClick(Love love);
    }
    public void setOnUserClickListener(OnUserClickListener clickListener) {
        this.clickListener = clickListener;
    }

}
