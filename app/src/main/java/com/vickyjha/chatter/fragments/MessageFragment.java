package com.vickyjha.chatter.fragments;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vickyjha.chatter.Adapter.ChatListAdapter;
import com.vickyjha.chatter.Adapter.ContactListAdapter;
import com.vickyjha.chatter.Data.ChatModelData;
import com.vickyjha.chatter.Data.ItemModelData;
import com.vickyjha.chatter.LoginActivity;
import com.vickyjha.chatter.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class MessageFragment extends Fragment {

    ArrayList<ChatModelData> chatList;
    RecyclerView chatRecView;
    ChatListAdapter chatListAdapter;
    private RecyclerView.LayoutManager chatListLayoutManager;
    private ImageView logout;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message,container,false);
        chatList = new ArrayList<>();
        initializeRecview(view);
        getChatList(view);
        logout = view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(view.getContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                ((Activity)view.getContext()).finish();
                return;
            }
        });
        return view;
    }

    private void getChatList(View view){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("chat");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    for(DataSnapshot childSnapshot : snapshot.getChildren()){
                        String imageID = "",name ="";
                        if(childSnapshot.child("image").exists()&&!childSnapshot.child("image").getValue().equals(null))
                            imageID = childSnapshot.child("image").getValue().toString();

                        if(childSnapshot.child("name").exists()&&!childSnapshot.child("name").getValue().equals(null));
                            name = childSnapshot.child("name").getValue().toString();
                        ChatModelData chatModelData = new ChatModelData(childSnapshot.getKey(),imageID,name);
                        chatList.add(chatModelData);
                        chatListAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void initializeRecview(View view) {
        chatRecView= view.findViewById(R.id.messageRecView);
        chatRecView.setNestedScrollingEnabled(false);
        chatRecView.setHasFixedSize(false);
        chatListLayoutManager = new LinearLayoutManager(view.getContext().getApplicationContext(), RecyclerView.VERTICAL, false);
        chatRecView.setLayoutManager(chatListLayoutManager);
        chatListAdapter = new ChatListAdapter(chatList,view.getContext());
        chatRecView.setAdapter(chatListAdapter);
    }



}