package com.okanius.bear;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.okanius.bear.databinding.ActivityUploadBinding;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;

public class UploadActivity extends AppCompatActivity {
    private ActivityUploadBinding binding;
    FirebaseUser firebaseUser;
    FirebaseStorage firebaseStorage;
    FirebaseFirestore firebaseFirestore;
    DocumentReference documentReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        String email = firebaseUser.getEmail();
        documentReference = firebaseFirestore.collection("users").document(email);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        binding.usernameU.setText((String) "nick : " + documentSnapshot.getData().get("username"));
                        Picasso.get().load((String) documentSnapshot.getData().get("profilePhotoUri")).into(binding.bearU);
                    }
                }
            }
        });
    }

    public class arrays {

    }


    Calendar calendar;
    int day,month,year,hour,minute,second;

    public void share(View view){
        String email = firebaseUser.getEmail();
        HashMap<String,Object> post = new HashMap<>();

        calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH); post.put("day",day);
        month = calendar.get(Calendar.MONTH); post.put("month",month);
        year = calendar.get(Calendar.YEAR); post.put("year",year);
        hour = calendar.get(Calendar.HOUR_OF_DAY); post.put("hour",hour);
        minute = calendar.get(Calendar.MINUTE); post.put("minute",minute);
        second = calendar.get(Calendar.SECOND);post.put("second",second);

        firebaseFirestore
                .collection("posts")
                .document(email)
                .set(post)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                }
                else{

                }
            }
        });

    }
}