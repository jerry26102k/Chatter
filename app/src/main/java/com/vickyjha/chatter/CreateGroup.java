package com.vickyjha.chatter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import  java.io.Serializable;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
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
import com.vickyjha.chatter.Adapter.ContactListAdapter;
import com.vickyjha.chatter.Data.ItemModelData;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateGroup extends AppCompatActivity {


    Uri mFilePathImg;
    Bitmap mBitmapImg;
    DatabaseReference reference;
    StorageReference storeItemImageRef;
    ArrayList<ItemModelData> userList;

    CircleImageView groupImage;
    EditText groupName;
    Button submitBtn;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);


         groupImage = findViewById(R.id.groupImage);
         groupName = findViewById(R.id.groupName);
         submitBtn = findViewById(R.id.submitButton);
         context = CreateGroup.this;

         userList = ContactListAdapter.userList;

         groupImage.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Dexter.withActivity(CreateGroup.this)
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
                if (mFilePathImg != null && !groupName.getText().equals(null)) {
                    storeDataToFireBase();
                } else {
                    Toast.makeText(CreateGroup.this, "Please select Image and enter name properly", Toast.LENGTH_SHORT).show();
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
                groupImage.setImageBitmap(mBitmapImg);


            } catch (Exception e) {
                Toast.makeText(this, "There was a problem uploading your image", Toast.LENGTH_SHORT).show();

            }
        } else {
            Toast.makeText(this, "Can't find the image ", Toast.LENGTH_SHORT).show();
        }

    }

    private void storeDataToFireBase() {
        ProgressDialog dialog = new ProgressDialog(CreateGroup.this);
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
                Toast.makeText(CreateGroup.this, "Upload failed!! Please try again.", Toast.LENGTH_SHORT).show();
            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                task.getResult().getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {


                        makeGroup(uri);

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




    private void makeGroup(Uri uri) {
        String key = FirebaseDatabase.getInstance().getReference().child("chat").push().getKey();

        DatabaseReference groupInfo = FirebaseDatabase.getInstance().getReference().child("chat").child(key).child("groupInfo");

        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("user");

        HashMap newChatMap = new HashMap();
        newChatMap.put("checked", true);
        newChatMap.put("name", groupName.getText().toString());
        newChatMap.put("image", uri.toString().trim());
        newChatMap.put("userId",FirebaseAuth.getInstance().getUid());

        Boolean validChat = false;
        for (ItemModelData mUser : userList) {
            if (mUser.isSelected()) {
                validChat = true;
                Map groupMember = new HashMap();
                groupMember.put("memberId",mUser.getId());
                groupMember.put("memberName",mUser.getName());

                //groupInfo.push().updateChildren(groupMember);

                userDb.child(mUser.getId()).child("chat").child(key).updateChildren(newChatMap);
            }
        }

        if (validChat) {
            userDb.child(FirebaseAuth.getInstance().getUid()).child("chat").child(key).updateChildren(newChatMap);
            DatabaseReference ref = userDb.child(FirebaseAuth.getInstance().getUid());
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    Map groupAdmin = new HashMap();
                    groupAdmin.put("memberName",snapshot.child("name").getValue());
                    groupAdmin.put("memberId",snapshot.getKey());
                    //groupInfo.push().updateChildren(groupAdmin);
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        }
        ((Activity)context).finish();
    }


}