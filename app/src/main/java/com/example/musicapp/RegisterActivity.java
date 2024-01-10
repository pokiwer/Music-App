package com.example.musicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    TextView txtAccept, txtEmailReg, txtPassReg, txtConfirm, txtLogin;
    EditText edtEmailReg, edtPassReg, edtConfirm;
    Button btnRegister;
    private boolean isChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Mapping();
        Controller();
    }

    private void Mapping() {
        txtEmailReg = findViewById(R.id.txtEmailReg);
        txtPassReg = findViewById(R.id.txtPassReg);
        txtAccept = findViewById(R.id.txtAccept);
        txtLogin = findViewById(R.id.txtLogin);
        txtConfirm = findViewById(R.id.txtConfirm);
        edtEmailReg = findViewById(R.id.edtEmailReg);
        edtPassReg = findViewById(R.id.edtPassReg);
        edtConfirm = findViewById(R.id.edtConfirm);
        btnRegister = findViewById(R.id.btnRegister);
    }

    private void Controller() {
        txtAccept.setOnClickListener(view -> {
            isChecked = !isChecked;
            if (isChecked) {
                txtAccept.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_checked, 0, 0, 0);
            } else {
                txtAccept.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_unchecked, 0, 0, 0);
            }
        });

        txtLogin.setOnClickListener(view -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        btnRegister.setOnClickListener(view -> {
            String email = edtEmailReg.getText().toString().trim();
            String pass = edtPassReg.getText().toString().trim();
            String confirm = edtConfirm.getText().toString().trim();
            if (!email.isEmpty()) {
                txtEmailReg.setTextColor(Color.parseColor("#000000"));
                if (!pass.isEmpty()) {
                    txtPassReg.setTextColor(Color.parseColor("#000000"));
                    if (!confirm.isEmpty()) {
                        txtConfirm.setTextColor(Color.parseColor("#000000"));
                        if (pass.equals(confirm)) {
                            txtConfirm.setTextColor(Color.parseColor("#000000"));
                            if (isChecked) {
                                txtAccept.setTextColor(Color.parseColor("#000000"));
                                FirebaseAuth auth = FirebaseAuth.getInstance();
                                auth.createUserWithEmailAndPassword(email, pass)
                                        .addOnCompleteListener(RegisterActivity.this, task -> {
                                            if (task.isSuccessful()) {
                                                DatabaseReference userDB = FirebaseDatabase.getInstance().getReference("user");
                                                FirebaseUser user = task.getResult().getUser();
                                                String uid = user.getUid();
                                                User addUser = new User(email,"","","");
                                                userDB.child(uid).setValue(addUser, (error, ref) -> {
                                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                                    intent.putExtra("userID",uid);
                                                    startActivity(intent);
                                                    finishAffinity();
                                                });

                                            } else {
                                                // If sign in fails, display a message to the user.
                                                Toast.makeText(RegisterActivity.this, "Register failed.",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                txtAccept.setTextColor(Color.parseColor("#ff0000"));
                            }
                        } else {
                            txtConfirm.setTextColor(Color.parseColor("#ff0000"));
                            txtConfirm.setText("Password incorrect");
                        }
                    } else {
                        txtConfirm.setTextColor(Color.parseColor("#ff0000"));
                    }
                } else {
                    txtPassReg.setTextColor(Color.parseColor("#ff0000"));
                }
            } else {
                txtEmailReg.setTextColor(Color.parseColor("#ff0000"));
            }
        });

    }
}