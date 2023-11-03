package com.example.musicapp;

import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private boolean isTxtCategoryVisible = false;
    private boolean isTxtProfileVisible = false;
    ImageButton btnCategory, btnProfile;
    LinearLayoutCompat category, profile;
    LinearLayout txtCategory, txtProfile;
    TextView txtUser, txtLogout;
    ImageView imgUser;

    public UserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_user, container, false);
        //mapping id
        Mapping(rootView);
        //List options
        View.OnClickListener categoryClickListener = view -> {
            if (isTxtCategoryVisible) {
                txtCategory.setVisibility(View.GONE);
                isTxtCategoryVisible = false;
            } else {
                txtCategory.setVisibility(View.VISIBLE);
                isTxtCategoryVisible = true;
            }
        };
        View.OnClickListener profileClickListener = view -> {
            if (isTxtProfileVisible) {
                txtProfile.setVisibility(View.GONE);
                isTxtProfileVisible = false;
            } else {
                txtProfile.setVisibility(View.VISIBLE);
                isTxtProfileVisible = true;
            }
        };
        //Event click
        eventClick();

        //show user information
        showUser();

        return rootView;
    }

    private void toggleVisibility(LinearLayout linearLayout) {
        if (linearLayout.getVisibility() == View.VISIBLE) {
            linearLayout.setVisibility(View.GONE);
        } else {
            linearLayout.setVisibility(View.VISIBLE);
        }
    }

    private void eventClick() {

        View.OnClickListener categoryClickListener = view -> toggleVisibility(txtCategory);
        View.OnClickListener profileClickListener = view -> toggleVisibility(txtProfile);
        btnCategory.setOnClickListener(categoryClickListener);
        category.setOnClickListener(categoryClickListener);
        btnProfile.setOnClickListener(profileClickListener);
        profile.setOnClickListener(profileClickListener);

        txtLogout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), LoginSrceenActivity.class);
            startActivity(intent);
            getActivity().finish();
        });
    }

    private void showUser() {
        //Show thông tin người dùng
        String userUid = getArguments().getString("userID");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;
        String email = user.getEmail();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String path = "user/" + userUid;
        DatabaseReference myRef = database.getReference(path);
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.child("name").getValue(String.class);
                String image = dataSnapshot.child("image").getValue(String.class);
                txtUser.setText(value);
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                StorageReference pathReference = storageRef.child("user/" + image);
                pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUrl = uri.toString();
                        Glide.with(getActivity())
                                .load(imageUrl)
                                .error(R.drawable.ic_user)
                                .into(imgUser);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", "Failed ");
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                txtUser.setText(email);
            }
        };
        myRef.addValueEventListener(userListener);


    }

    private void Mapping(View rootView) {
        category = rootView.findViewById(R.id.category);
        txtCategory = rootView.findViewById(R.id.txtCategory);
        btnCategory = rootView.findViewById(R.id.btnCategory);
        profile = rootView.findViewById(R.id.profile);
        txtProfile = rootView.findViewById(R.id.txtProfile);
        btnProfile = rootView.findViewById(R.id.btnProfile);
        txtUser = rootView.findViewById(R.id.txtUser);
        imgUser = rootView.findViewById(R.id.imgUser);
        txtLogout = rootView.findViewById(R.id.txtLogout);
    }

}