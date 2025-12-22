package com.example.chapter03;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MusicActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnPlay;
    private Button btnStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        btnPlay = findViewById(R.id.btn_play);
        btnStop = findViewById(R.id.btn_stop);

        btnPlay.setOnClickListener(this);
        btnStop.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, MusicService.class);
        int id = v.getId();
        if (id == R.id.btn_play) {
            startService(intent);
            btnPlay.setEnabled(false);
            btnStop.setEnabled(true);
        } else if (id == R.id.btn_stop) {
            stopService(intent);
            btnPlay.setEnabled(true);
            btnStop.setEnabled(false);
        }
    }
}
