package com.example.musicapp;


import android.Manifest;
import android.app.Dialog;
import android.content.Intent;


import android.content.pm.PackageManager;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class UserFragment extends Fragment {

    private static final int PERMISSION_GALLERY = 1;
    private Fragment albumFragment, loveFragment;

    ImageButton btnCategory, btnProfile;
    LinearLayoutCompat category, profile;
    LinearLayout txtCategory, txtProfile;
    TextView txtUser, txtLogout, txtAlbum, txtArtist, txtInfor, txtChangePass;
    ImageView imgUser, imgCurrent;
    private ActivityResultLauncher<Intent> checkPermission;
    private User showUser;
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private final DatabaseReference userDB = FirebaseDatabase.getInstance().getReference("user/" + user.getUid());
    private final StorageReference storageRef = FirebaseStorage.getInstance().getReference("user");

    private Uri imageUri;

    public UserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user, container, false);
        //mapping id
        Mapping(rootView);

        //Event click
        eventClick();

        //show user information
        showUser();
        setCheckPermission();

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
            Intent intent = new Intent(getActivity(), LoginScreenActivity.class);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        txtAlbum.setOnClickListener(view -> {
            albumFragment = new AlbumFragment();
            changeFragment(albumFragment);
        });
        txtArtist.setOnClickListener(view -> {
            loveFragment = new LoveFragment();
            changeFragment(loveFragment);
        });

        txtInfor.setOnClickListener(txtInforView -> {
            Dialog customDialog = new Dialog(getContext());
            customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            customDialog.setContentView(R.layout.custom_edit_profile);
            imgCurrent = customDialog.findViewById(R.id.imgCurrent);
            EditText edtName, edtBirth, edtPhone;
            edtName = customDialog.findViewById(R.id.edtName);
            edtBirth = customDialog.findViewById(R.id.edtBirth);
            edtPhone = customDialog.findViewById(R.id.edtPhone);
            Button btnUpdate = customDialog.findViewById(R.id.btnUpdate);
            edtName.setText(showUser.getName());
            edtBirth.setText(showUser.getBirth());
            edtPhone.setText(showUser.getPhone());
            ImageButton btnChangeImage = customDialog.findViewById(R.id.btnChangeImage);
            imgCurrent.setImageDrawable(imgUser.getDrawable());
            customDialog.show();
            btnChangeImage.setOnClickListener(btnChangeImageView -> requestPermission());
            btnUpdate.setOnClickListener(btnUpdateView -> {
                if (imageUri != null) {
                    Date now = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", new Locale("vi", "VN"));
                    String fileName = sdf.format(now);
                    storageRef.child(fileName).putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                        Map<String, Object> updateData = new HashMap<>();
                        updateData.put("name", edtName.getText().toString());
                        updateData.put("image", fileName);
                        updateData.put("phone", edtPhone.getText().toString());
                        updateData.put("birth", edtBirth.getText().toString());
                        userDB.updateChildren(updateData, (error, ref) -> {
                            customDialog.dismiss();
                            Toast.makeText(getActivity(), "Update success", Toast.LENGTH_SHORT).show();
                        });
                    }).addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show());
                } else {
                    Map<String, Object> updateData = new HashMap<>();
                    updateData.put("name", edtName.getText().toString());
                    updateData.put("phone", edtPhone.getText().toString());
                    updateData.put("birth", edtBirth.getText().toString());
                    userDB.updateChildren(updateData, (error, ref) -> {
                        customDialog.dismiss();
                        Toast.makeText(getActivity(), "Update success", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });

        txtChangePass.setOnClickListener(txtChangePassView -> {
            Dialog customDialog = new Dialog(getContext());
            customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            customDialog.setContentView(R.layout.custom_change_pass);
            TextView txtOld, txtNew, txtConfirm, txtForgot;
            txtOld = customDialog.findViewById(R.id.txtOld);
            txtNew = customDialog.findViewById(R.id.txtNew);
            txtConfirm = customDialog.findViewById(R.id.txtConfirm);
            EditText edtOld, edtNew, edtConfirm;
            edtNew = customDialog.findViewById(R.id.edtNew);
            edtOld = customDialog.findViewById(R.id.edtOld);
            edtConfirm = customDialog.findViewById(R.id.edtConfirm);
            txtForgot = customDialog.findViewById(R.id.txtForgot);
            Button btnUpdate = customDialog.findViewById(R.id.btnUpdate);
            customDialog.show();
            edtNew.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    txtNew.setText("New password");
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String text = editable.toString();
                    if (text.length() < 6) {
                        txtNew.setText("Must be at least 6 characters");
                        txtNew.setTextColor(Color.parseColor("#FF0000"));
                    } else if(text.length() < 9) {
                        txtNew.setText("Nomal password");
                        txtNew.setTextColor(Color.parseColor("#FFA500"));
                    }
                    else {
                        txtNew.setText("Strong password");
                        txtNew.setTextColor(Color.parseColor("#008000"));
                    }

                }
            });
            edtConfirm.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    txtConfirm.setText("Confirm new password");
                    txtConfirm.setTextColor(Color.parseColor("#000000"));
                }
            });
            edtOld.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    txtOld.setText("Current password");
                    txtOld.setTextColor(Color.parseColor("#000000"));
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            txtForgot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(),ResetPassActivity.class);
                    startActivity(intent);
                }
            });
            btnUpdate.setOnClickListener(view -> {
                if (edtOld.getText().toString().equals("")){
                    txtOld.setText("Enter your current password");
                    txtOld.setTextColor(Color.parseColor("#FF0000"));
                }
                else{
                    if (edtNew.getText().toString().equals(""))
                    {
                        txtNew.setText("Enter your new password");
                        txtNew.setTextColor(Color.parseColor("#FF0000"));
                    }
                    else {
                        if (edtConfirm.getText().toString().equals("")){
                            txtConfirm.setText("Enter confirm new password");
                            txtConfirm.setTextColor(Color.parseColor("#FF0000"));
                        }
                        else {
                            if (!edtConfirm.getText().toString().equals(edtNew.getText().toString()))
                            {
                                txtConfirm.setText("Confirmation password does not match");
                                txtConfirm.setTextColor(Color.parseColor("#FF0000"));
                            }
                            else {
                                AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(),edtOld.getText().toString());
                                user.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            user.updatePassword(edtNew.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        Toast.makeText(getActivity(), "Update password successful", Toast.LENGTH_SHORT).show();
                                                        customDialog.dismiss();
                                                    }
                                                    else Toast.makeText(getActivity(), "Update failed", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }else {
                                            txtOld.setText("Current password does not match");
                                            txtOld.setTextColor(Color.parseColor("#FF0000"));
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            });

        });
    }

    private void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .setReorderingAllowed(true)
                .commit();
    }

    private void showUser() {
        //Show thông tin người dùng
        if (user == null) return;
        String email = user.getEmail();
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showUser = dataSnapshot.getValue(User.class);
                txtUser.setText(showUser.getName());
                StorageReference pathReference = storageRef.child(showUser.getImage());
                pathReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    Glide.with(getActivity())
                            .load(imageUrl)
                            .error(R.drawable.ic_user)
                            .into(imgUser);
                }).addOnFailureListener(e -> Log.d("TAG", "Failed "));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                txtUser.setText(email);
            }
        };
        userDB.addValueEventListener(userListener);
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
        txtAlbum = rootView.findViewById(R.id.txtAlbum);
        txtArtist = rootView.findViewById(R.id.txtArtist);
        txtInfor = rootView.findViewById(R.id.txtInfor);
        txtChangePass = rootView.findViewById(R.id.txtChangePass);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        checkPermission.launch(intent);
    }

    private void setCheckPermission() {
        checkPermission = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), o -> {
            try {
                imageUri = o.getData().getData();
                imgCurrent.setImageURI(imageUri);
            } catch (Exception e) {
                e.getStackTrace();
                Toast.makeText(getActivity(), "No image chosen", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void requestPermission() {
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_GALLERY);
        } else openGallery();
    }

}