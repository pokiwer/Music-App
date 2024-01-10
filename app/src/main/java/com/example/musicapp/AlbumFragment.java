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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AlbumFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlbumFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ImageView imgSong;
    private TextView txtArtist, txtTitle;
    private Button btnAddAlbum;
    private ArrayList<Song> songArrayList, recommend;
    private RecyclerView recyclerview;
    String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference songDB = database.getReference("song");
    DatabaseReference albumDB = database.getReference("album/" + userUid + "/song");
    DatabaseReference artistDB = database.getReference("artist");

    public AlbumFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AlbumFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AlbumFragment newInstance(String param1, String param2) {
        AlbumFragment fragment = new AlbumFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_album, container, false);
        Mapping(rootView);
        recommend = new ArrayList<>();
        return rootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        songArrayList = new ArrayList<>();
        AlbumAdapter albumAdapter = new AlbumAdapter(getContext(), userUid, songArrayList);
        dataInit(albumAdapter, userUid);
        recyclerview = view.findViewById(R.id.rcvAlbum);
        recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerview.setHasFixedSize(true);
        recyclerview.setAdapter(albumAdapter);
        albumAdapter.setOnUserClickListener(song -> {
            Intent intent = new Intent(getActivity(), PlayerService.class);
            intent.putExtra("song", song);
            intent.putExtra("songList", songArrayList);
            intent.putExtra("isOpen",true);
            getActivity().startService(intent);
        });
    }

    private void dataInit(AlbumAdapter albumAdapter, String userUid) {
        songDB.orderByChild("numSong").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Song song = snapshot.getValue(Song.class);
                if (song != null) {
                    albumDB.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild(String.valueOf(song.getId()))) {
                                songArrayList.add(0,song);
                                albumAdapter.notifyDataSetChanged();
                            }
                            else {
                                recommend.add(song);
                                eventClick();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

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
    }

    private void Mapping(View rootView) {
        imgSong = rootView.findViewById(R.id.imgSong);
        txtArtist = rootView.findViewById(R.id.txtArtist);
        txtTitle = rootView.findViewById(R.id.txtTitle);
        btnAddAlbum = rootView.findViewById(R.id.btnAddAlbum);
    }
    private void eventClick() {
        if (recommend.size() > 0) {
            Song song = recommend.get(0);
            txtTitle.setText(song.getTitle());
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference pathReference = storageRef.child("song/" + song.getImage());
            pathReference.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();
                Glide.with(getContext())
                        .load(imageUrl)
                        .into(imgSong);
            });
            artistDB.child(String.valueOf(song.getArtist())).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String name = snapshot.child("name").getValue(String.class);
                    txtArtist.setText(name);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            btnAddAlbum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    albumDB.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild(String.valueOf(song.getId()))){
                                albumDB.child(String.valueOf(song.getId())).removeValue((error, ref) -> btnAddAlbum.setText("ADD TO ALBUM"));
                            }
                            else {
                                Map<String, Object> dataMap = new HashMap<>();
                                dataMap.put(String.valueOf(song.getId()), true);
                                albumDB.updateChildren(dataMap, (error, ref) -> btnAddAlbum.setText("ADDED"));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });

            imgSong.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), PlayerService.class);
                    intent.putExtra("song", song);
                    intent.putExtra("songList", songArrayList);
                    intent.putExtra("isOpen",true);
                    getActivity().startService(intent);
                }
            });
        }

    }

}