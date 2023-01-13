package com.example.mplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.widget.Button;

import com.chibde.visualizer.SquareBarVisualizer;

public class PlayerActivity extends AppCompatActivity {

    Button play,next,prev,replay,forward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        play=findViewById(R.id.playBtn);
        next=findViewById(R.id.nextBtn);
        prev=findViewById(R.id.prevBtn);
        replay=findViewById(R.id.replayBtn);
        forward=findViewById(R.id.forBtn);

        play.setOnClickListener(view -> {
            play.setBackground(getDrawable(R.drawable.ic_pause));
        });

        SquareBarVisualizer squareBarVisualizer = findViewById(R.id.visualizer);
        // set custom color to the line.
                squareBarVisualizer.setColor(ContextCompat.getColor(this, R.color.purple_200));
        // define custom number of bars you want in the visualizer between (10 - 256).
                squareBarVisualizer.setDensity(65);
        // Set Spacing
                squareBarVisualizer.setGap(2);
        // Set your media player to the visualizer.
        //        squareBarVisualizer.setPlayer(mediaPlayer.getAudioSessionId());

    }
}