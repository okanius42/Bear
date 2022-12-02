package com.okanius.bear;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.okanius.bear.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String email;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        email = binding.emailL.getText().toString();
        password = binding.passwordL.getText().toString();

        if (firebaseUser != null) {
            if (firebaseUser.isEmailVerified()) {
                startActivity(new Intent(LoginActivity.this, FeedActivity.class));
            } else {
                Intent intent = getIntent();
                Boolean ver = intent.getBooleanExtra("ver",false);
                if(ver){
                }
                else{
                    startActivity(new Intent(LoginActivity.this, VerificationActivity.class));

                }
            }
        }

        binding.registerL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        binding.forgotL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });

    }
    public void loginL(View view){
        email = binding.emailL.getText().toString();
        password = binding.passwordL.getText().toString();
        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                firebaseUser=firebaseAuth.getCurrentUser();
                if(firebaseUser.isEmailVerified()){
                    startActivity(new Intent(LoginActivity.this,FeedActivity.class));
                }
                else{
                startActivity(new Intent(LoginActivity.this,VerificationActivity.class));
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }
}