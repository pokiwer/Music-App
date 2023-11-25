package com.example.musicapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int action = intent.getIntExtra("action",0);
        ArrayList<Song> songArrayList = (ArrayList<Song>) intent.getSerializableExtra("songList");
        Song song = (Song) intent.getSerializableExtra("song");
        Intent returnIntent = new Intent(context,PlayerService.class);
        returnIntent.putExtra("action",action);
        returnIntent.putExtra("songList",songArrayList);
        returnIntent.putExtra("song",song);
        context.startService(returnIntent);
    }
}
