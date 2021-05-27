package com.vickyjha.chatter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.vickyjha.chatter.Adapter.ChatListAdapter;
import com.vickyjha.chatter.Adapter.MediaAdapter;
import com.vickyjha.chatter.Adapter.MessageAdapter;
import com.vickyjha.chatter.Data.ChatModelData;
import com.vickyjha.chatter.Data.MessageModelData;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    ArrayList<MessageModelData> messageList;
    ArrayList<String> mediaList,mediaUrlList;
    RecyclerView messageRecView,mediaRecView;
    MessageAdapter messageListAdapter;
    MediaAdapter mediaListAdapter;
    private RecyclerView.LayoutManager messageListLayoutManager,mediaListLayoutManager;
    private EditText enterMessage;
    private ImageView send;
    private String chatId;
    Context context;
    private CircleImageView profileImage;
    private TextView name;
    private ImageView addMedia;
    private ProgressBar progressBar;
    ImageView back,call,videoCall;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        messageList = new ArrayList<>();
        mediaIdList = new ArrayList<>();
        mediaList = new ArrayList<>();
        context = ChatActivity.this;
        enterMessage = findViewById(R.id.enterMessage);
        progressBar = findViewById(R.id.progressBar);
        send = findViewById(R.id.sendMessage);
        profileImage = findViewById(R.id.profile_image);
        name = findViewById(R.id.name);
        chatId = getIntent().getStringExtra("chatId");
        addMedia = findViewById(R.id.sendMedia);
        back = findViewById(R.id.chatBack);
        call = findViewById(R.id.chatCall);
        videoCall = findViewById(R.id.chatVideoCall);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ChatActivity.this,"coming soon",Toast.LENGTH_SHORT).show();

            }
        });
        videoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ChatActivity.this,"coming soon",Toast.LENGTH_SHORT).show();

            }
        });
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("chat").child(chatId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Glide.with((Activity)context).asBitmap().load(Uri.parse(snapshot.child("image").getValue().toString())).into(profileImage);


                name.setText(snapshot.child("name").getValue().toString());

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        addMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
                mediaIdList = new ArrayList<>();
            }
        });


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( !mediaList.isEmpty() || !enterMessage.getText().toString().isEmpty()) {
                    saveMessageToFirebase(enterMessage.getText().toString());
                    hideKeyboard((Activity)context);
                    enterMessage.setText(null);
                }
            }
        });
        getMessageFromFirebase();
        initializeRecview();
        initializeMediaRecView();
    }
    private void getMessageFromFirebase(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("chat").child(chatId);
       reference.addChildEventListener(new ChildEventListener() {
           @Override
           public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
               if(snapshot.exists()) {
                   mediaUrlList = new ArrayList<>();
                   if(snapshot.child("media").getChildrenCount()> 0){
                       for(DataSnapshot mediaSnapshot : snapshot.child("media").getChildren()){
                           mediaUrlList.add(mediaSnapshot.getValue().toString());
                       }
                   }
                   String mMess = snapshot.child("message").getValue().toString();
                   String senderID = snapshot.child("senderId").getValue().toString();
                   String time = snapshot.child("time").getValue().toString();
                   MessageModelData messageModelData = new MessageModelData(senderID, mMess, chatId,mediaUrlList,time);
                   messageList.add(messageModelData);
                   messageListLayoutManager.scrollToPosition(messageList.size() - 1);
                   messageListAdapter.notifyDataSetChanged();
               }

           }

           @Override
           public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

           }

           @Override
           public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {

           }

           @Override
           public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

           }

           @Override
           public void onCancelled(@NonNull @NotNull DatabaseError error) {

           }
       });

    }

    ArrayList<String> mediaIdList = new ArrayList<>();
    int totalMediaUploaded = 0;

    private void saveMessageToFirebase(String message){

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("chat").child(chatId);
            String mKey = reference.push().getKey();
            DatabaseReference newMessageDb = reference.child(mKey);
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm");
            String time = simpleDateFormat.format(calendar.getTime());


           final Map<String, Object> data = new HashMap<>();
            data.put("senderId", FirebaseAuth.getInstance().getCurrentUser().getUid());
            data.put("message", message);
            data.put("time",time);
            reference.child(mKey).updateChildren(data);


            if(!mediaList.isEmpty()){
                progressBar.setVisibility(View.VISIBLE);
                for(String mediaUri : mediaList){
                    String mediaId = newMessageDb.child("media").push().getKey();
                    mediaIdList.add(mediaId);
                    mediaListAdapter.notifyDataSetChanged();
                    final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("chat").child(chatId).child(mKey).child(mediaId);



                    UploadTask uploadTask = filePath.putFile(Uri.parse(mediaUri));

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    data.put("/media/"+mediaIdList.get(totalMediaUploaded) + "/", uri.toString());
                                    totalMediaUploaded++;
                                    if(totalMediaUploaded == mediaList.size()){
                                        updateDatabase(newMessageDb,data);

                                    }

                                }
                            });
                        }
                    });

                }
            }





    }

    private void updateDatabase(DatabaseReference ref , Map newMessage ){

        ref.updateChildren(newMessage);
        mediaIdList.clear();
        mediaList.clear();
        mediaListAdapter.notifyDataSetChanged();
        messageListAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.INVISIBLE);

    }

    private void initializeMediaRecView() {
        mediaRecView= findViewById(R.id.mediaRecView);
        mediaRecView.setNestedScrollingEnabled(false);
        mediaRecView.setHasFixedSize(false);
        mediaListLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false);
        mediaRecView.setLayoutManager(mediaListLayoutManager);
        mediaListAdapter = new MediaAdapter(ChatActivity.this,mediaList);
        mediaRecView.setAdapter(mediaListAdapter);
    }
    private void initializeRecview() {
        messageRecView= findViewById(R.id.messageRecView);
        messageRecView.setNestedScrollingEnabled(false);
        messageRecView.setHasFixedSize(false);
        messageListLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        messageRecView.setLayoutManager(messageListLayoutManager);
        messageListAdapter = new MessageAdapter(messageList,ChatActivity.this);
        messageRecView.setAdapter(messageListAdapter);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    int PICK_IMAGE_INTENT = 1;
    private void openGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"select pictures"),PICK_IMAGE_INTENT);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == PICK_IMAGE_INTENT){
                if(data.getClipData() == null){
                    mediaList.add(data.getData().toString());

                }else{
                    for(int i = 0; i<data.getClipData().getItemCount();i++){
                        mediaList.add(data.getClipData().getItemAt(i).getUri().toString());
                    }
                }
                mediaListAdapter.notifyDataSetChanged();

            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mediaListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mediaListAdapter.notifyDataSetChanged();
    }
}

