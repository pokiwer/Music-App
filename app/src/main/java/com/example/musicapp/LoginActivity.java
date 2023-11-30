package com.example.musicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;



import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    TextView txtRemember, txtEmail, txtPassword, txtRegister, txtForgot;
    Button btnLogin;
    EditText edtEmail, edtPassword;
    private boolean isChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Mapping();
        Controller();
    }

    private void Mapping() {
        txtRemember = findViewById(R.id.txtRemember);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        txtRegister = findViewById(R.id.txtRegister);
        txtForgot = findViewById(R.id.txtForgot);
        btnLogin = findViewById(R.id.btnLogin);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
    }

    private void Controller() {
        txtRemember.setOnClickListener(view -> {
            isChecked = !isChecked;
            if (isChecked) {
                txtRemember.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_checked, 0, 0, 0);
            } else {
                txtRemember.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_unchecked, 0, 0, 0);
            }
        });
        txtForgot.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, ResetPassActivity.class);
            startActivity(intent);
        });
        btnLogin.setOnClickListener(view -> {
            String email = edtEmail.getText().toString().trim();
            String pass = edtPassword.getText().toString().trim();
            if (!email.isEmpty()) {
                txtEmail.setTextColor(Color.parseColor("#000000"));
                if (!pass.isEmpty()) {
                    txtPassword.setTextColor(Color.parseColor("#000000"));
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    auth.signInWithEmailAndPassword(email, pass)
                            .addOnCompleteListener(LoginActivity.this, task -> {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = auth.getCurrentUser();
                                    String uid = user.getUid();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.putExtra("userID", uid);
                                    startActivity(intent);
                                    finishAffinity();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Dialog customDialog = new Dialog(LoginActivity.this);
                                    customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    customDialog.setContentView(R.layout.custom_dialog);
                                    customDialog.show();
                                }
                            });
                } else {
                    txtPassword.setTextColor(Color.parseColor("#ff0000"));
                }
            } else {
                txtEmail.setTextColor(Color.parseColor("#ff0000"));
            }
        });

        txtRegister.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}
