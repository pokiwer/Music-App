package com.example.musicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {
    TextView txtAccept, txtEmailReg, txtPassReg,txtConfirm, txtLogin;
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
                String email = edtEmailReg.getText().toString();
                String pass = edtPassReg.getText().toString();
                String confirm = edtConfirm.getText().toString();
                if(!email.isEmpty()){
                    txtEmailReg.setTextColor(Color.parseColor("#000000"));
                    if(!pass.isEmpty())
                    {
                        txtPassReg.setTextColor(Color.parseColor("#000000"));
                        if (!confirm.isEmpty())
                        {
                            txtConfirm.setTextColor(Color.parseColor("#000000"));
                            if (pass.equals(confirm))
                            {
                                txtConfirm.setTextColor(Color.parseColor("#000000"));
                                if (isChecked)
                                {
                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                }
                                else {
                                    txtAccept.setTextColor(Color.parseColor("#ff0000"));
                                }
                            }
                            else
                            {
                                txtConfirm.setTextColor(Color.parseColor("#ff0000"));
                                txtConfirm.setText("Password incorrect");
                            }
                        }
                        else {
                            txtConfirm.setTextColor(Color.parseColor("#ff0000"));
                        }
                    }
                    else {
                        txtPassReg.setTextColor(Color.parseColor("#ff0000"));
                    }
                }
                else {
                    txtEmailReg.setTextColor(Color.parseColor("#ff0000"));
                }
            }
        });

    }
}