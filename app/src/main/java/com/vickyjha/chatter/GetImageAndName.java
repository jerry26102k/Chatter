package com.vickyjha.chatter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import  java.io.Serializable;



import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class GetImageAndName extends AppCompatActivity {

    private CircleImageView profileImage;
    private EditText name;
    private Button submitBtn;
    private String phoneNumber;


    Uri mFilePathImg;
    Bitmap mBitmapImg;
    DatabaseReference reference;
    StorageReference storeItemImageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_image_and_name);
        profileImage = findViewById(R.id.set_profile_image);
        name = findViewById(R.id.setName);
        submitBtn = findViewById(R.id.submitButton);
        submitBtn.setBackgroundResource(R.drawable.submit_btn_design);

        phoneNumber = getIntent().getStringExtra("phone");


        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dexter.withActivity(GetImageAndName.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                startActivityForResult(Intent.createChooser(intent, "please choose image to save"), 1);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                            }
                        }).check();

            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFilePathImg != null && !name.getText().equals(null)) {
                    storeDataToFireBase();
                } else {
                    Toast.makeText(GetImageAndName.this, "Please select Image and enter name properly", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && data != null && resultCode == RESULT_OK) {
            mFilePathImg = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(mFilePathImg);
                mBitmapImg = BitmapFactory.decodeStream(inputStream);
                profileImage.setImageBitmap(mBitmapImg);


            } catch (Exception e) {
                Toast.makeText(this, "There was a problem uploading your image", Toast.LENGTH_SHORT).show();

            }
        } else {
            Toast.makeText(this, "Can't find the image ", Toast.LENGTH_SHORT).show();
        }

    }

    private void storeDataToFireBase() {
        ProgressDialog dialog = new ProgressDialog(GetImageAndName.this);
        dialog.setTitle("Uploading your data, Please wait");
        dialog.show();
        reference = FirebaseDatabase.getInstance().getReference("Store Item");

        storeItemImageRef = FirebaseStorage.getInstance().getReference().child("storeItemImages");
        storeItemImageRef.child(String.valueOf(System.currentTimeMillis()) + "." + getImageExtension()).putFile(mFilePathImg)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        dialog.dismiss();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                dialog.dismiss();
                Toast.makeText(GetImageAndName.this, "Upload failed!! Please try again.", Toast.LENGTH_SHORT).show();
            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                task.getResult().getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {


                        String user = FirebaseAuth.getInstance().getCurrentUser().getUid();


                        final DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("user").child(user);
                        mUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.exists()) {
                                    Map<String, Object> userMap = new HashMap<>();
                                    userMap.put("phone", phoneNumber);
                                    userMap.put("name", name.getText().toString().trim());
                                    userMap.put("image", uri.toString().trim());
                                    mUserDB.updateChildren(userMap);
                                }
                                userIsLoggedIn();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        Toast.makeText(GetImageAndName.this, "data uploaded successfully", Toast.LENGTH_SHORT).show();


                        finish();

                    }
                });

            }
        });

    }

    private String getImageExtension() {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mFilePathImg));
    }

    private void userIsLoggedIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (!user.equals(null)) {
            startActivity(new Intent(getApplicationContext(), HomepageActivity.class));
            finish();
            return;
        }
    }
}