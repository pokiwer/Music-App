package com.example.musicapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
        txtAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isChecked = !isChecked;
                if (isChecked) {
                    txtAccept.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_checked, 0, 0, 0);
                } else {
                    txtAccept.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_unchecked, 0, 0, 0);
                }
            }
        });

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (task.isSuccessful()) {
                                                        FirebaseUser user = task.getResult().getUser();
                                                        String uid = user.getUid();
                                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                                        intent.putExtra("userID",uid);
                                                        startActivity(intent);
                                                        finishAffinity();
                                                    } else {
                                                        // If sign in fails, display a message to the user.
                                                        Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                                                Toast.LENGTH_SHORT).show();
                                                    }
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
            }
        });

    }
}