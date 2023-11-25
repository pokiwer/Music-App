package com.example.musicapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

import androidx.core.app.NotificationManagerCompat;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DiscoverFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DiscoverFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<Artist> artistArrayList;
    private ArrayList<Song> famousArrayList, newsArrayList, songArrayList;
    private RecyclerView famous, news, popular, song;

    public DiscoverFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DiscoverFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DiscoverFragment newInstance(String param1, String param2) {
        DiscoverFragment fragment = new DiscoverFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_discover, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Thêm list các bài hát phổ biến
        famousArrayList = new ArrayList<>();
        FamousAdapter famousAdapter = new FamousAdapter(getContext(), famousArrayList);
        dataInitFamous(famousAdapter, view);
        //Thêm list các bài hát mới
        newsArrayList = new ArrayList<>();
        NewsAdapter newsAdapter = new NewsAdapter(getContext(), newsArrayList);
        dataInitNews(newsAdapter, view);
        //Thêm list  các nghệ sĩ
        artistArrayList = new ArrayList<>();
        PopularAdapter popularAdapter = new PopularAdapter(getContext(), artistArrayList);
        dataInitPopular(popularAdapter, view);
        //Thêm list các bài hát
        songArrayList = new ArrayList<>();
        SongAdapter songAdapter = new SongAdapter(getContext(), songArrayList, 1);
        dataInitSong(songAdapter, view);
    }

    private void dataInitSong(SongAdapter songAdapter, View view) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference songDB = database.getReference("song");
        songDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Song song = snapshot.getValue(Song.class);
                if (song != null) {
                    songArrayList.add(0, song);
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
        song = view.findViewById(R.id.rcvSong);
        song.setLayoutManager(new LinearLayoutManager(getContext()));
        song.setAdapter(songAdapter);
        song.setHasFixedSize(true);
        songAdapter.setOnUserClickListener(new SongAdapter.OnUserClickListener() {
            @Override
            public void onUserClick(Song song) {
                Intent intent = new Intent(getActivity(), PlayerService.class);
                intent.putExtra("song", song);
                intent.putExtra("songList", songArrayList);
                intent.putExtra("isOpen",true);
                getActivity().startService(intent);
            }
        });
    }

    private void dataInitNews(NewsAdapter newsAdapter, View view) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference songDB = database.getReference("song");
        Query query = songDB.orderByChild("id");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Song news = snapshot.getValue(Song.class);
                if (news != null) {
                    newsArrayList.add(0, news);
                    newsAdapter.notifyDataSetChanged();
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
        news = view.findViewById(R.id.rcvNew);
        news.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        news.setAdapter(newsAdapter);
        news.setHasFixedSize(true);
        newsAdapter.setOnUserClickListener(new NewsAdapter.OnUserClickListener() {
            @Override
            public void onUserClick(Song song) {
                Intent intent = new Intent(getActivity(), PlayerService.class);
                intent.putExtra("song", song);
                intent.putExtra("songList", newsArrayList);
                intent.putExtra("isOpen",true);
                getActivity().startService(intent);
            }
        });
    }

    private void dataInitFamous(FamousAdapter famousAdapter, View view) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference songDB = database.getReference("song");
        Query query = songDB.orderByChild("numListen");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Song famous = snapshot.getValue(Song.class);
                if (famous != null) {
                    famousArrayList.add(0, famous);
                    famousAdapter.notifyDataSetChanged();
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
        famous = view.findViewById(R.id.rcvFamous);
        famous.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        famous.setHasFixedSize(true);
        famous.setAdapter(famousAdapter);
        famous.setHasFixedSize(true);
        famousAdapter.setOnUserClickListener(new FamousAdapter.OnUserClickListener() {
            @Override
            public void onUserClick(Song song) {
                Intent intent = new Intent(getActivity(), PlayerService.class);
                intent.putExtra("song", song);
                intent.putExtra("songList", famousArrayList);
                intent.putExtra("isOpen",true);
                getActivity().startService(intent);
            }
        });
    }

    private void dataInitPopular(PopularAdapter popularAdapter, View view) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference artistDB = database.getReference("artist");
        Query query = artistDB.orderByChild("numFollow");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Artist artist = snapshot.getValue(Artist.class);
                if (artist != null) {
                    artistArrayList.add(0, artist);
                    popularAdapter.setOnUserClickListener(new PopularAdapter.OnUserClickListener() {
                        @Override
                        public void onUserClick(Artist artist) {
                            Intent intent = new Intent(getActivity(), DetailActivity.class);
                            intent.putExtra("artistID", artist.getId());
                            startActivity(intent);
                        }
                    });
                    popularAdapter.notifyDataSetChanged();
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
        popular = view.findViewById(R.id.rcvPopular);
        popular.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        popular.setAdapter(popularAdapter);
        popular.setHasFixedSize(true);
    }
}