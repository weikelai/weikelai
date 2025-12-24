package com.example.chapter03;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class VideoNativeActivity extends AppCompatActivity {

    private VideoView videoView;
    private static final String VIDEO_URL =
            "https://www.w3schools.com/html/mov_bbb.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_native);

        videoView = findViewById(R.id.videoView);
        Button play = findViewById(R.id.btnPlay);

        play.setOnClickListener(v -> {
            videoView.setVideoURI(Uri.parse(VIDEO_URL));

            MediaController controller = new MediaController(this);
            videoView.setMediaController(controller);
            controller.setAnchorView(videoView);

            videoView.start();
        });
    }
}