package com.example.musicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.google.android.material.snackbar.Snackbar;

public class LoginActivity extends AppCompatActivity {
    TextView txtRemember, txtEmail, txtPassword, txtRegister;
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

    private void Mapping()
    {
        txtRemember = findViewById(R.id.txtRemember);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        txtRegister = findViewById(R.id.txtRegister);
        btnLogin = findViewById(R.id.btnLogin);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
    }

    private void Controller()
    {
        txtRemember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isChecked = !isChecked;

                // Tùy theo trạng thái, đặt biểu tượng
                if (isChecked) {
                    txtRemember.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_checked, 0, 0, 0);
                } else {
                    txtRemember.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_unchecked, 0, 0, 0);
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtEmail.getText().toString();
                String pass = edtPassword.getText().toString();
                if(!email.isEmpty())
                {
                    txtEmail.setTextColor(Color.parseColor("#000000"));
                    if(!pass.isEmpty()){
                        txtPassword.setTextColor(Color.parseColor("#000000"));
                        if(email.equals("admin") && pass.equals("123")) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                        else {
                            Dialog customDialog = new Dialog(LoginActivity.this);
                            customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            customDialog.setContentView(R.layout.custom_dialog);
                            customDialog.show();
                        }
                    }
                    else {
                        txtPassword.setTextColor(Color.parseColor("#ff0000"));
                    }
                }
                else {
                    txtEmail.setTextColor(Color.parseColor("#ff0000"));
                }
            }
        });

        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}
