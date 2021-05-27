package com.vickyjha.chatter.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.squareup.picasso.Picasso;
import com.vickyjha.chatter.ChatActivity;
import com.vickyjha.chatter.Data.ChatModelData;
import com.vickyjha.chatter.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder> implements java.io.Serializable{

    ArrayList<ChatModelData> chatList;
    Context context;

    public ChatListAdapter(ArrayList<ChatModelData> chatList, Context context){
        this.chatList = chatList;
        this.context = context;
    }


    @NonNull
    @NotNull
    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemchat, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);

        ChatListViewHolder rcv = new ChatListViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ChatListAdapter.ChatListViewHolder holder,final  int position) {

        ChatListViewHolder chatListViewHolder = (ChatListViewHolder)holder;
        chatListViewHolder.title.setText(chatList.get(position).getTitle());
        Glide.with((Activity)context).asBitmap().load(Uri.parse(chatList.get(position).getImageId().trim())).into(chatListViewHolder.image);




        chatListViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("chatId",chatList.get(position).getChatId());
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    class ChatListViewHolder extends RecyclerView.ViewHolder{

        TextView title;
        CardView cardView;
        CircleImageView image;

        public ChatListViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.name);
            cardView = itemView.findViewById(R.id.item);
            image = itemView.findViewById(R.id.profile_image);
        }
    }

}
