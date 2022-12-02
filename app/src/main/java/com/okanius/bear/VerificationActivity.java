package com.okanius.bear;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.okanius.bear.databinding.ActivityVerificationBinding;

public class VerificationActivity extends AppCompatActivity {
    private ActivityVerificationBinding binding;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityVerificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    }
    public void verificationV(View view){
        if(firebaseUser.isEmailVerified()){
            startActivity(new Intent(VerificationActivity.this,FeedActivity.class));
        }
        else{
        firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(VerificationActivity.this, "Please Make Your Account Verified then Login", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(VerificationActivity.this,LoginActivity.class);
                    intent.putExtra("ver",true);
                    startActivity(intent);
                }
            }
        });
        }
    }

}