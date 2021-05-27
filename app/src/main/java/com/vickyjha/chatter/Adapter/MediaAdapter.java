package com.vickyjha.chatter.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vickyjha.chatter.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaViewHolder> {
    private Context context;
    private ArrayList<String> mediaList;
    public MediaAdapter(Context context,ArrayList<String> mediaList){
        this.context = context;
        this.mediaList = mediaList;
    }

    @NonNull
    @NotNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_media,null,false);
        MediaViewHolder mediaViewHolder  = new MediaViewHolder(view);
        return mediaViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MediaAdapter.MediaViewHolder holder, final int position) {
        MediaViewHolder mediaViewHolder = (MediaViewHolder)holder;
        Glide.with(context).load(Uri.parse(mediaList.get(position))).into(mediaViewHolder.imageView);

    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }


    public class MediaViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public MediaViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.media);

        }
    }
}
