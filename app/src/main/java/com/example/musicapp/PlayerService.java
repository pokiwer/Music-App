package com.example.musicapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;

public class PlayerService extends Service {
    public static final int ACTION_PREV = 1;
    public static final int ACTION_PAUSE = 2;
    public static final int ACTION_PLAY = 3;
    public static final int ACTION_NEXT = 4;
    public static final int ACTION_REWIND = 5;
    public static final int ACTION_CLEAR = 6;

    private int index, rewind, isRepeat = 0;
    private ArrayList<Song> songArrayList;
    private MediaPlayer mediaPlayer;
    private DataLoadListener dataLoadListener;
    private Song current;
    private String txtArtist;
    private Bitmap bitmap;
    private boolean isPlaying = true;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int action = intent.getIntExtra("action", 0);
        Song song = (Song) intent.getSerializableExtra("song");
        if (intent.hasExtra("songList")) {
            songArrayList = (ArrayList<Song>) intent.getSerializableExtra("songList");
        }
        index = findSongIndex(songArrayList, song);
        if (current != null && song.getId() != current.getId()) {
            current = songArrayList.get(index);
            mediaPlayer.release();
            mediaPlayer = null;
            showSong();
        } else {
            current = song;
            showSong();
        }
        if (intent.hasExtra("isOpen")) {
            Intent dialogIntent = new Intent(this, PlayerActivity.class);
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(dialogIntent);
        }
        if (intent.hasExtra("rewind")) {
            rewind = intent.getIntExtra("rewind", 0);
        }
        handleClick(action);
        return START_NOT_STICKY;
    }

    private void showSong() {
        getImageSong(current);
        getArtistName(current);
        dataLoadListener = new DataLoadListener() {
            @Override
            public void onDataLoaded(Bitmap loadedBitmap, String loadedArtist) {
                // Dữ liệu đã sẵn sàng, gọi sendNotification
                playSong(current);
                sendNotification(loadedBitmap, loadedArtist, current);
                handleRepeat();
            }
        };
    }


    private void playSong(Song current) {
        StorageReference audioUrl = FirebaseStorage.getInstance().getReference().child("song");
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            audioUrl.child(current.getName()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    try {
                        mediaPlayer.setDataSource(uri.toString());
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        isPlaying = true;
                        sendActionToActivity(ACTION_PLAY);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    }

    private void handleClick(int action) {
        switch (action) {
            case ACTION_PREV:
                if (isPlaying) {
                    mediaPlayer.pause();
                }
                mediaPlayer.release();
                mediaPlayer = null;
                index--;
                if (index < 0) index = songArrayList.size() - 1;
                current = songArrayList.get(index);
                showSong();
                sendActionToActivity(ACTION_PREV);
                break;
            case ACTION_PLAY:
                if (mediaPlayer != null && !isPlaying) {
                    mediaPlayer.start();
                    isPlaying = true;
                    sendNotification(bitmap, txtArtist, current);
                    sendActionToActivity(ACTION_PLAY);
                }
                break;
            case ACTION_PAUSE:
                if (mediaPlayer != null && isPlaying) {
                    mediaPlayer.pause();
                    isPlaying = false;
                    sendNotification(bitmap, txtArtist, current);
                    sendActionToActivity(ACTION_PAUSE);
                }
                break;
            case ACTION_NEXT:
                if (isPlaying) {
                    mediaPlayer.pause();
                }
                mediaPlayer.release();
                mediaPlayer = null;
                if (index == songArrayList.size() - 1) {
                    index = 0;
                } else index++;
                current = songArrayList.get(index);
                showSong();
                sendActionToActivity(ACTION_NEXT);
                break;
            case ACTION_REWIND:
                if (mediaPlayer != null)
                    mediaPlayer.seekTo(rewind);
                sendActionToActivity(ACTION_PLAY);
                break;
            case ACTION_CLEAR:
                stopSelf();
                break;
        }
    }

    private void handleRepeat() {
        if (mediaPlayer != null) {
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    // Xử lý theo trạng thái nút repeat
                    switch (isRepeat) {
                        case 0:
                            mediaPlayer.pause();
                            isPlaying = false;
                            getPendingIntent(getApplicationContext(), ACTION_NEXT, current);
                            break;
                        case 1:
                            isPlaying = false;
                            getPendingIntent(getApplicationContext(), ACTION_PLAY, current);
                            break;
                        case 2:
                            getPendingIntent(getApplicationContext(), ACTION_PAUSE, current);
                            if (index < songArrayList.size() - 1)
                                getPendingIntent(getApplicationContext(), ACTION_NEXT, current);
                            break;
                    }
                }
            });
        }
    }

    private void checkAndNotify() {
        // Kiểm tra xem cả hai dữ liệu đã sẵn sàng chưa.
        if (dataLoadListener != null && bitmap != null && txtArtist != null) {
            // Dữ liệu đã sẵn sàng, gọi onDataLoaded của interface.
            dataLoadListener.onDataLoaded(bitmap, txtArtist);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public interface DataLoadListener {
        void onDataLoaded(Bitmap bitmap, String artistName);
    }


    private void sendNotification(Bitmap bitmap, String artistName, Song current) {
        int desiredWidth = 64;
        int desiredHeight = 64;
        Bitmap largeIcon = Bitmap.createScaledBitmap(bitmap, desiredWidth, desiredHeight, false);
        Intent intent = new Intent(this, MainActivity.class);
        MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(this, "media_session");
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, MusicChanel.CHANNEL_ID)
                // Show controls on lock screen even when user hides sensitive content.
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.ic_music)
                .setLargeIcon(largeIcon)
                // Apply the media style template.
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2 /* #1: pause button */)
                        .setMediaSession(mediaSessionCompat.getSessionToken()))
                .setContentTitle(current.getTitle())
                .setContentText(artistName);
        if (isPlaying) {
            notificationBuilder.addAction(R.drawable.ic_play_prev, "Previous", getPendingIntent(this, ACTION_PREV, current)) // #0
                    .addAction(R.drawable.ic_pause, "Pause", getPendingIntent(this, ACTION_PAUSE, current))  // #1
                    .addAction(R.drawable.ic_play_next, "Next", getPendingIntent(this, ACTION_NEXT, current));  // #2
        } else {
            notificationBuilder.addAction(R.drawable.ic_play_prev, "Previous", getPendingIntent(this, ACTION_PREV, current)) // #0
                    .addAction(R.drawable.ic_play, "Pause", getPendingIntent(this, ACTION_PLAY, current))  // #1
                    .addAction(R.drawable.ic_play_next, "Next", getPendingIntent(this, ACTION_NEXT, current));   // #2
        }
        Notification notification = notificationBuilder.build();
        startForeground(1, notification);
    }

    private PendingIntent getPendingIntent(Context context, int action, Song current) {
        Intent intent = new Intent(this, MyReceiver.class);
        intent.putExtra("action", action);
        intent.putExtra("songList", songArrayList);
        intent.putExtra("song", current);
        return PendingIntent.getBroadcast(context.getApplicationContext(), action, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void getImageSong(Song current) {
        StorageReference audioUrl = FirebaseStorage.getInstance().getReference().child("song");
        audioUrl.child(current.getImage()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext())
                        .asBitmap()
                        .load(uri.toString())
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                bitmap = resource;
                                checkAndNotify();
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                // Xử lý khi tải ảnh bị xóa
                            }
                        });
            }
        });

    }

    private void getArtistName(Song current) {

        DatabaseReference artistDB = FirebaseDatabase.getInstance().getReference("artist");
        artistDB.child(String.valueOf(current.getArtist())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Artist artist = snapshot.getValue(Artist.class);
                txtArtist = artist.getName();
                checkAndNotify();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private int findSongIndex(ArrayList<Song> songArrayList, Song current) {
        for (int i = 0; i < songArrayList.size(); i++) {
            Song song = songArrayList.get(i);
            if (song.getId() == (current.getId())) {
                return i;
            }
        }
        return -1;
    }

    private void sendActionToActivity(int action) {
        Intent intent = new Intent("send_data");
        Bundle bundle = new Bundle();
        bundle.putParcelable("bitmap", bitmap);
        bundle.putInt("action", action);
        bundle.putString("artist", txtArtist);
        bundle.putSerializable("song", current);
        if (mediaPlayer != null) {
            bundle.putInt("duration", mediaPlayer.getDuration());
            bundle.putInt("position", mediaPlayer.getCurrentPosition());
        }
        bundle.putBoolean("isPlaying", isPlaying);
        intent.putExtras(bundle);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
