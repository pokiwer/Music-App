package com.example.musicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
    private final FirebaseAuth user = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);
        edtEmailReset = findViewById(R.id.edtEmailReset);
        btnReset = findViewById(R.id.btnReset);
        txtEmailReset = findViewById(R.id.txtEmailReset);
        FirebaseUser current = user.getCurrentUser();

        if (current != null) edtEmailReset.setText(current.getEmail());
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

        btnReset.setOnClickListener(view -> {
            if (edtEmailReset.getText().toString().trim().isEmpty())
            {
                txtEmailReset.setText("Enter your email");
                txtEmailReset.setTextColor(Color.parseColor("#FF0000"));
            }
            else {
                user.sendPasswordResetEmail(edtEmailReset.getText().toString().trim())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                Intent intent = new Intent(ResetPassActivity.this,LoginActivity.class);
                                intent.putExtra("reset",0);
                                startActivity(intent);
                                finishAffinity();
                            }
                            else
                                Toast.makeText(ResetPassActivity.this, "An error occurred, please try again later", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }
}