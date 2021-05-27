package com.vickyjha.chatter.Adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.vickyjha.chatter.Data.MessageModelData;
import com.vickyjha.chatter.R;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> implements java.io.Serializable{

    ArrayList<MessageModelData> messageList;
    RecyclerView.LayoutManager mediaLayoutManager;
    MediaAdapter mediaAdapter;
    Context context;
    public MessageAdapter(ArrayList<MessageModelData> messageList, Context context){
        this.messageList = messageList;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemmessage, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);

        MessageViewHolder rcv = new MessageViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MessageAdapter.MessageViewHolder holder, final int position) {

        MessageViewHolder messageViewHolder = (MessageViewHolder) holder;

        messageViewHolder.time.setText(messageList.get(position).getTime());

        if(messageList.get(position).getMediaUrlList().size() > 0) {
            messageViewHolder.imageView.setVisibility(View.VISIBLE);


            Glide.with(context).load(Uri.parse(messageList.get(position).getMediaUrlList().get(0))).into(messageViewHolder.imageView);
        }else messageViewHolder.imageView.setVisibility(View.GONE);


        if(!messageList.get(position).getMessage().isEmpty()){
            messageViewHolder.message.setVisibility(View.VISIBLE);
            messageViewHolder.message.setText(messageList.get(position).getMessage());
        }else messageViewHolder.message.setVisibility(View.GONE);



        if(messageList.get(position).getName().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    600, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_START, messageViewHolder.parentView.getId());
            messageViewHolder.layout.setLayoutParams(params);
            messageViewHolder.layout.setBackgroundResource(R.drawable.send_message_design);
            Log.v("hello","********************************************");

        }else{
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    600, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_END, messageViewHolder.parentView.getId());
            messageViewHolder.layout.setLayoutParams(params);
            messageViewHolder.layout.setBackgroundResource(R.drawable.recieve_message_design);
        }

    }

    @Override
    public int getItemCount() {

        return messageList.size();

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        TextView message;
        TextView time;
        RelativeLayout layout;
        RelativeLayout parentView;
        ImageView imageView;


        public MessageViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            time = itemView.findViewById(R.id.time);
            layout = itemView.findViewById(R.id.layout);
            parentView = itemView.findViewById(R.id.parent_view);
             imageView= itemView.findViewById(R.id.messageImage);

        }
    }


}

