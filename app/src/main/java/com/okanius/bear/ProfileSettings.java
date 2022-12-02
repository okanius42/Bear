package com.okanius.bear;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.okanius.bear.databinding.ActivityProfileSettingsBinding;
import com.squareup.picasso.Picasso;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickClick;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileSettings extends AppCompatActivity implements IPickResult{
    private ActivityProfileSettingsBinding binding;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    DocumentReference documentReference;
    DatePickerDialog datePickerDialog;
    int dayToday, monthToday, yearToday, day, month, year;

    Uri profilePhotoData;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    private final int PICK_IMAGE_CAMERA = 1,PICK_IMAGE_GALLERY = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        getDataClass();

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        setRequestLauncher();
        setAddPhotoLauncher();

        binding.birthdayPS.setInputType(InputType.TYPE_NULL);
        binding.birthdayPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                dayToday = calendar.get(Calendar.DAY_OF_MONTH);
                monthToday = calendar.get(Calendar.MONTH);
                yearToday = calendar.get(Calendar.YEAR);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                month = calendar.get(Calendar.MONTH);
                year = calendar.get(Calendar.YEAR);
                datePickerDialog = new DatePickerDialog(ProfileSettings.this, R.style.DatePickerDialogStyle, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int yearDatePicker, int monthDatePicker, int dayDatePicker) {
                        binding.birthdayPS.setText(dayDatePicker + "." + (monthDatePicker + 1) + "." + yearDatePicker);
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            LocalDate birthDate = LocalDate.of(yearDatePicker, monthDatePicker, dayDatePicker);
                            LocalDate now = LocalDate.of(yearToday, monthToday, dayToday);
                        }
                        year = yearDatePicker;
                        month = monthDatePicker + 1;
                        day = dayDatePicker;
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });
    }

    public void setPS(View view) {
        String email = firebaseUser.getEmail();

        Map<String, Object> userInfo = new HashMap<>();
        String name = binding.namePS.getText().toString();
        String surname = binding.surnamePS.getText().toString();
        String username = binding.usernamePS.getText().toString();
        String birthday = day + "." + month + "." + year;

        documentReference = firebaseFirestore.collection("users").document(email);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        if(!(name.isEmpty())){
                            firebaseFirestore.collection("users")
                                    .document(email)
                                    .update("name",name)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });
                        }
                        if(!(surname.isEmpty())){
                            firebaseFirestore.collection("users")
                                    .document(email)
                                    .update("surname",surname)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });
                        }
                        if(!(username.isEmpty())){
                            firebaseFirestore.collection("users")
                                    .document(email)
                                    .update("username",username)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });
                        }
                        if(day != 0 && month!=0 && year != 0){
                            firebaseFirestore.collection("users")
                                    .document(email)
                                    .update("birthday", birthday, "day", day, "month", month, "year", year)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                        }
                                    });

                        }
                        if(profilePhotoData != null){
                            String usernameDS = (String) documentSnapshot.getData().get("username");
                            String profilePhotoChild = "images/" + email + "/" + "profilePhoto.jpg";
                            storageReference
                                    .child(profilePhotoChild)
                                    .putFile(profilePhotoData)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            StorageReference referenceProfilePhoto = firebaseStorage.getReference(profilePhotoChild);
                                            referenceProfilePhoto
                                                    .getDownloadUrl()
                                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    firebaseFirestore
                                                            .collection("users")
                                                            .document(email)
                                                            .update("profilePhotoUri",uri.toString())
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                }
                                                            });
                                                }
                                            });

                                        }
                                    });

                        }
                    }
                    else{
                        if(firebaseUser.isEmailVerified()) {

                            if (profilePhotoData != null) {
                                String profilePhotoChild = "images/" + email + "/" + "profilePhoto.jpg";
                                storageReference
                                        .child(profilePhotoChild)
                                        .putFile(profilePhotoData)
                                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                StorageReference referenceProfilePhoto = firebaseStorage.getReference(profilePhotoChild);
                                                referenceProfilePhoto
                                                        .getDownloadUrl()
                                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                            @Override
                                                            public void onSuccess(Uri uri) {
                                                                userInfo.put("profilePhotoUri", uri.toString());
                                                                userInfo.put("name", name);
                                                                userInfo.put("surname", surname);
                                                                userInfo.put("username", username);
                                                                userInfo.put("day", day);
                                                                userInfo.put("month", month);
                                                                userInfo.put("year", year);
                                                                userInfo.put("birthday", birthday);
                                                                firebaseFirestore.collection("users")
                                                                        .document(email)
                                                                        .set(userInfo)
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                            }
                                                                        });
                                                            }
                                                        });
                                            }
                                        });
                            } else {

                            userInfo.put("name", name);
                            userInfo.put("surname", surname);
                            userInfo.put("username", username);
                            userInfo.put("day", day);
                            userInfo.put("month", month);
                            userInfo.put("year", year);
                            userInfo.put("birthday", birthday);
                            firebaseFirestore.collection("users")
                                    .document(email)
                                    .set(userInfo)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                        }
                                    });
                        }
                        }
                        else{
                            AlertDialog.Builder verification = new AlertDialog.Builder(ProfileSettings.this);
                            verification
                                    .setTitle("Verification")
                                    .setMessage("Verification needed!")
                                    .setPositiveButton("Okey", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(ProfileSettings.this,VerificationActivity.class));
                                }
                            });
                            verification.show();
                        }
                    }
                }
            }
        });
    }
    public void getDataClass() {
        String email = firebaseUser.getEmail();
        documentReference = firebaseFirestore.collection("users").document(email);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()) {
                        binding.namePS.setText((String) documentSnapshot.getData().get("name"));
                        binding.surnamePS.setText((String) documentSnapshot.getData().get("surname"));
                        binding.usernamePS.setText((String) documentSnapshot.getData().get("username"));
                        binding.birthdayPS.setText((String) (
                                        documentSnapshot.getData().get("day") + "." +
                                        documentSnapshot.getData().get("month") + "." +
                                        documentSnapshot.getData().get("year")));
                        Picasso.get().load((String) documentSnapshot.getData().get("profilePhotoUri")).into(binding.profilePhotoPS);
                    }
                }
                else{

                }

            }
        });

    }


    public void profilePhotoPS(View view){
        PickImageDialog dialog = PickImageDialog.build(new PickSetup()).setOnClick(new IPickClick() {
            @Override
            public void onGalleryClick() {
                if(ContextCompat.checkSelfPermission(ProfileSettings.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    if(ActivityCompat.shouldShowRequestPermissionRationale(ProfileSettings.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                        Snackbar.make(view,"Permission needed for gallery",Snackbar.LENGTH_INDEFINITE)
                                .setAction("Give Permission", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                                    }
                                }).show();
                    }
                    else{
                        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }
                else{
                    Intent goToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    addPhotoLauncher.launch(goToGallery);
                }
            }
            @Override
            public void onCameraClick() {

                if(ContextCompat.checkSelfPermission(ProfileSettings.this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                    if(ActivityCompat.shouldShowRequestPermissionRationale(ProfileSettings.this,Manifest.permission.CAMERA)){
                        Snackbar.make(view,"Permission needed for camera!",Snackbar.LENGTH_INDEFINITE)
                                .setAction("Give permission", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        requestPermissionLauncher.launch(Manifest.permission.CAMERA);
                                    }
                                });
                    }
                    else{
                        requestPermissionLauncher.launch(Manifest.permission.CAMERA);

                    }

                }
                else{
                    String fileName = "profile photo";
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE,fileName);
                    values.put(MediaStore.Images.Media.DESCRIPTION,"Image capture by camera with Bear app");
                    profilePhotoData = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
                    Intent gotocamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    gotocamera.putExtra(MediaStore.EXTRA_OUTPUT,profilePhotoData);
                    startActivityForResult(gotocamera,0);
                }

            }
        }).show(getSupportFragmentManager());


    }
    ActivityResultLauncher<String> requestPermissionLauncher;
    public void setRequestLauncher(){
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission()
                ,new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                    Intent goToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    addPhotoLauncher.launch(goToGallery);
                }
                else{
                    Toast.makeText(ProfileSettings.this,"Permission needed!",Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    ActivityResultLauncher<Intent> addPhotoLauncher;
    public void setAddPhotoLauncher(){
        addPhotoLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult()
                , new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == RESULT_OK){
                            Intent fromResult = result.getData();
                            if(fromResult != null){
                                profilePhotoData = fromResult.getData();
                                binding.profilePhotoPS.setImageURI(profilePhotoData);
                            }
                        }
                    }
                });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && resultCode == RESULT_OK){
            ContentResolver cr = getContentResolver();
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(cr,profilePhotoData);
                binding.profilePhotoPS.setImageBitmap(bitmap);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }

    }


    @Override
    public void onPickResult(PickResult r) {

    }
}