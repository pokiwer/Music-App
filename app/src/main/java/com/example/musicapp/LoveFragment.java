package com.example.musicapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
 * Use the {@link LoveFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoveFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ArrayList<Artist> artistArrayList, recommend;
    private RecyclerView recyclerview;
    private ImageView imgArtist;
    private TextView txtArtist, txtNumSong;
    private Button btnAddFollow;
    private String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference artistDB = database.getReference("artist");
    DatabaseReference followDB = database.getReference("follow/" + userUid + "/artist");

    public LoveFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoveFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoveFragment newInstance(String param1, String param2) {
        LoveFragment fragment = new LoveFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_love, container, false);
        recommend = new ArrayList<>();
        Mapping(rootView);
        eventClick();
        return rootView;
    }

    private void eventClick() {
        followDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String keyString = childSnapshot.getKey();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void Mapping(View rootView) {
        imgArtist = rootView.findViewById(R.id.imgArtist);
        txtArtist = rootView.findViewById(R.id.txtArtist);
        txtNumSong = rootView.findViewById(R.id.txtNumSong);
        btnAddFollow = rootView.findViewById(R.id.btnAddFollow);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        artistArrayList = new ArrayList<>();
        LoveAdapter loveAdapter = new LoveAdapter(getContext(), artistArrayList, userUid);
        dataInit(loveAdapter, userUid);
        recyclerview = view.findViewById(R.id.rcvLove);
        recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerview.setHasFixedSize(true);
        recyclerview.setAdapter(loveAdapter);

        loveAdapter.setOnUserClickListener(artist -> {
            Intent intent = new Intent(getActivity(), DetailActivity.class);
            intent.putExtra("artistID", artist.getId());
            startActivity(intent);
        });
    }

    private void dataInit(LoveAdapter loveAdapter, String userUid) {
        artistDB.orderByChild("numFollow").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Artist artist = snapshot.getValue(Artist.class);
                if (artist != null) {
                    followDB.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild(String.valueOf(artist.getId()))) {
                                artistArrayList.add(0,artist);
                                loveAdapter.notifyDataSetChanged();
                            }
                            else {
                                recommend.add(artist);
                                handleClick();
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

    private void handleClick() {
        if (recommend.size() > 0) {
            Artist artist = recommend.get(0);
            Log.d("TAG", "handleClick: " + artist.getName());
            txtArtist.setText(artist.getName());
            txtNumSong.setText(artist.getNumSong() + " song - " + artist.getNumFollow() + " follower");
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference pathReference = storageRef.child("artist/" + artist.getImage());
            pathReference.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();
                Glide.with(getContext())
                        .load(imageUrl)
                        .into(imgArtist);
            });
            imgArtist.setOnClickListener(view -> {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("artistID", artist.getId());
                startActivity(intent);
            });
            btnAddFollow.setOnClickListener(view -> followDB.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild(String.valueOf(artist.getId()))){
                        followDB.child(String.valueOf(artist.getId())).removeValue((error, ref) -> btnAddFollow.setText("ADD TO FAVOURITE"));
                    }
                    else {
                        Map<String, Object> dataMap = new HashMap<>();
                        dataMap.put(String.valueOf(artist.getId()), true);
                        followDB.updateChildren(dataMap, (error, ref) -> btnAddFollow.setText("ADDED"));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            }));
        }
    }
}