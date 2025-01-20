package com.sansriti.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class VideoListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private VideoListAdapter adapter;
    private List<String> videoNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list); // Layout for the video list

        recyclerView = findViewById(R.id.videoRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Example video names (you can replace these with actual video filenames)
        videoNames = new ArrayList<>();
        videoNames.add("SELF_DEFENSE_PART1");
        videoNames.add("SELF_DEFENSE_PART2");
        videoNames.add("SELF_DEFENSE_PART3");
        videoNames.add("SELF_DEFENSE_PART4");
        videoNames.add("SELF_DEFENSE_PART5");
        videoNames.add("SELF_DEFENSE_PART6");
        videoNames.add("SELF_DEFENSE_PART7");
        videoNames.add("SELF_DEFENSE_PART8");
        videoNames.add("SELF_DEFENSE_PART9");
        videoNames.add("SELF_DEFENSE_PART10");
        videoNames.add("SELF_DEFENSE_PART11");
        videoNames.add("SELF_DEFENSE_PART12");
        videoNames.add("SELF_DEFENSE_PART13");
        videoNames.add("SELF_DEFENSE_PART14");
        videoNames.add("SELF_DEFENSE_PART15");
        videoNames.add("SELF_DEFENSE_PART16");

        // Create an instance of VideoListAdapter with a listener
        adapter = new VideoListAdapter(videoNames, position -> {
            // Get the video name and pass it to VideoActivity
            String videoName = videoNames.get(position);
            Intent intent = new Intent(VideoListActivity.this, VideoActivity.class);
            intent.putExtra("VIDEO_NAME", videoName);
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
    }
}


