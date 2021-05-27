package com.vickyjha.chatter.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.drawable.IconCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vickyjha.chatter.ChatActivity;
import com.vickyjha.chatter.Data.ItemModelData;
import com.vickyjha.chatter.R;

import org.jetbrains.annotations.NotNull;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.UserListViewHolder> implements java.io.Serializable{

  public  static  ArrayList<ItemModelData> userList;
    Context context;
    private int checkVariable;

    public ContactListAdapter(ArrayList<ItemModelData> userList,Context context,int checkVariable){

        this.userList = userList;
        this.context = context;
        this.checkVariable = checkVariable;
    }



    @NonNull
    @NotNull
    @Override
    public UserListViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contactlistitem, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);

        UserListViewHolder rcv = new UserListViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ContactListAdapter.UserListViewHolder holder, final int position) {

        UserListViewHolder userListViewHolder = (UserListViewHolder)holder;
        userListViewHolder.mName.setText(userList.get(position).getName());
        Glide.with((Activity)context).asBitmap().load(userList.get(position).getImageId().trim()).into(userListViewHolder.mImage);

        if(checkVariable == 1){
            userListViewHolder.checkBox.setVisibility(View.VISIBLE);
            userListViewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    userList.get(holder.getAdapterPosition()).setSelected(isChecked);
                }
            });
        }






        userListViewHolder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("chat");
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        boolean check = true;

                        if(snapshot.exists()){
                            Log.v("vicky","******************************************************** "+ userList.get(position).getId() );
                            for(DataSnapshot childSnap : snapshot.getChildren()){
                                if(childSnap.child("userId").getValue().toString().equals(userList.get(position).getId())){
                                    check = false;
                                    Intent intent = new Intent(context, ChatActivity.class);
                                    intent.putExtra("chatId",childSnap.getKey().toString());
                                    context.startActivity(intent);

                                    break;
                                }


                            }




                        }

                        if(check && snapshot.exists()){
                            Log.v("vicky","******************************************************** "+ userList.get(position).getId() );

                            String key = FirebaseDatabase.getInstance().getReference().child("chat").push().getKey();
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid());

                            Map dat = new HashMap();
                            dat.put("check", true);
                            dat.put("name", userList.get(position).getName());
                            dat.put("image", userList.get(position).getImageId());
                            dat.put("userId", userList.get(position).getId());

                            FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("chat").child(key).updateChildren(dat);

                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                    Map data = new HashMap();
                                    data.put("check", true);
                                    data.put("name", snapshot.child("name").getValue().toString());
                                    data.put("image", snapshot.child("image").getValue().toString());
                                    data.put("userId", FirebaseAuth.getInstance().getUid());
                                    FirebaseDatabase.getInstance().getReference().child("user").child(userList.get(position).getId()).child("chat").child(key).updateChildren(data);

                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                }
                            });



                        }

                        if(!snapshot.exists()){

                            Log.v("vicky","******************************************************** "+ userList.get(position).getId() );

                            String key = FirebaseDatabase.getInstance().getReference().child("chat").push().getKey();
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid());

                            Map dat = new HashMap();
                            dat.put("check", true);
                            dat.put("name", userList.get(position).getName());
                            dat.put("image", userList.get(position).getImageId());
                            dat.put("userId", userList.get(position).getId());

                            FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("chat").child(key).updateChildren(dat);

                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                    Map data = new HashMap();
                                    data.put("check", true);
                                    data.put("name", snapshot.child("name").getValue().toString());
                                    data.put("image", snapshot.child("image").getValue().toString());
                                    data.put("userId", FirebaseAuth.getInstance().getUid());
                                    FirebaseDatabase.getInstance().getReference().child("user").child(userList.get(position).getId()).child("chat").child(key).updateChildren(data);

                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                }
                            });




                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });



            }
        });


    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class UserListViewHolder extends RecyclerView.ViewHolder{
        TextView mName;
        ImageView mImage;
        CardView mCardView;
        CheckBox checkBox;
        UserListViewHolder(View view){
            super(view);
            mName = view.findViewById(R.id.name);
            mImage = view.findViewById(R.id.profile_image);
            mCardView = view.findViewById(R.id.item);
            checkBox = view.findViewById(R.id.checkbox);

        }
    }
}
