package com.sansriti.myapplication;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.VideoView;
import android.widget.MediaController;

public class VideoActivity extends AppCompatActivity {

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);  // The layout for VideoActivity

        videoView = findViewById(R.id.videoView);

        // Get the video name passed from VideoListActivity
        Intent intent = getIntent();
        String videoName = intent.getStringExtra("VIDEO_NAME");

        // Create URI for the video from raw folder
        int videoResId = getResources().getIdentifier(videoName.toLowerCase(), "raw", getPackageName());
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + videoResId);

        // Set up the video player
        videoView.setVideoURI(videoUri);
        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);

        // Start playing the video
        videoView.start();
    }
}


