package com.sansriti.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private String[] videoNames;
    private Context context;

    public VideoAdapter(Context context, String[] videoNames) {
        this.context = context;
        this.videoNames = videoNames;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        holder.textView.setText(videoNames[position]);
    }

    @Override
    public int getItemCount() {
        return videoNames.length;
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public VideoViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // When an item is clicked, open the VideoActivity to play the video
                    Intent intent = new Intent(context, VideoActivity.class);
                    intent.putExtra("VIDEO_NAME", videoNames[getAdapterPosition()]); // Send the selected video name
                    context.startActivity(intent);
                }
            });
        }
    }
}
