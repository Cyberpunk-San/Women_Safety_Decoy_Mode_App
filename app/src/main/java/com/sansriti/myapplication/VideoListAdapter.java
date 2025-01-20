package com.sansriti.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoViewHolder> {

    private List<String> videoNames;
    private OnItemClickListener listener;

    public VideoListAdapter(List<String> videoNames, OnItemClickListener listener) {
        this.videoNames = videoNames;
        this.listener = listener;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        holder.textView.setText(videoNames.get(position));
    }

    @Override
    public int getItemCount() {
        return videoNames.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public VideoViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
            itemView.setOnClickListener(v -> {
                // When an item is clicked, call the listener
                listener.onItemClick(getAdapterPosition());
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
