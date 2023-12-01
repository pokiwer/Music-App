package com.example.musicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.hardware.camera2.params.ColorSpaceTransform;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ResetPassActivity extends AppCompatActivity {
    private EditText edtEmailReset;
    private TextView txtEmailReset;
    private Button btnReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);
        edtEmailReset = findViewById(R.id.edtEmailReset);
        btnReset = findViewById(R.id.btnReset);
        txtEmailReset = findViewById(R.id.txtEmailReset);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) edtEmailReset.setText(user.getEmail());
        edtEmailReset.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                txtEmailReset.setText("Email reset password");
                txtEmailReset.setTextColor(Color.parseColor("#000000"));
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtEmailReset.getText().toString().trim().isEmpty())
                {
                    txtEmailReset.setText("Enter your email");
                    txtEmailReset.setTextColor(Color.parseColor("#FF0000"));
                }
                else {
                    Toast.makeText(ResetPassActivity.this, "Success", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}