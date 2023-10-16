package com.example.musicapp;

import android.os.Bundle;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

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
    ImageButton btnCategory, btnProfile;
    LinearLayoutCompat category, profile;
    LinearLayout txtCategory, txtProfile;

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
        category = rootView.findViewById(R.id.category);
        txtCategory = rootView.findViewById(R.id.txtCategory);
        btnCategory = rootView.findViewById(R.id.btnCategory);
        profile = rootView.findViewById(R.id.profile);
        txtProfile = rootView.findViewById(R.id.txtProfile);
        btnProfile = rootView.findViewById(R.id.btnProfile);
        View.OnClickListener categoryClickListener = view -> {
            if (isTxtCategoryVisible) {
                txtCategory.setVisibility(View.GONE);
                isTxtCategoryVisible = false;
            } else {
                txtCategory.setVisibility(View.VISIBLE);
                isTxtCategoryVisible = true;
            }
        };

        btnCategory.setOnClickListener(categoryClickListener);
        category.setOnClickListener(categoryClickListener);
        return rootView;
    }
}