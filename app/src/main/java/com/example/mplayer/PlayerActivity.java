package com.example.mplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.chibde.visualizer.SquareBarVisualizer;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    Button play,next,prev,replay,forward;
    TextView textSong,textStart,textEnd;
    SeekBar seekMusicBar;

    SquareBarVisualizer squareBarVisualizer;

    ImageView imageView;

    String songName;
    public static final String EXTRA_NAME="song_name";
    static MediaPlayer mediaPlayer;
    int pos;
    ArrayList<File> mySongs;

    Thread updateSeeker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        play=findViewById(R.id.playBtn);
        next=findViewById(R.id.nextBtn);
        prev=findViewById(R.id.prevBtn);
        replay=findViewById(R.id.replayBtn);
        forward=findViewById(R.id.forBtn);
        textSong=findViewById(R.id.textSong);
        textStart=findViewById(R.id.txtSongStart);
        textEnd=findViewById(R.id.txtSongEnd);
        seekMusicBar=findViewById(R.id.seekBar);
        squareBarVisualizer = findViewById(R.id.visualizer);
        imageView=findViewById(R.id.img);

        if(mediaPlayer !=null){
            mediaPlayer.start();
            mediaPlayer.release();
        }
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();

        mySongs=(ArrayList) bundle.getParcelableArrayList("mySongs");
        String sName=intent.getStringExtra("songName");
        pos=bundle.getInt("pos",0);
        textSong.setSelected(true);
        Uri uri=Uri.parse(mySongs.get(pos).toString());
        songName=mySongs.get(pos).getName();
        textSong.setText(songName);

        mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
        mediaPlayer.start();


        updateSeeker=new Thread(){
            @Override
            public void run() {
                int tDuration=mediaPlayer.getDuration();
                int cPostion=0;
                while (cPostion<tDuration){
                    try {
                        sleep(500);
                        cPostion=mediaPlayer.getCurrentPosition();
                        seekMusicBar.setProgress(cPostion);
                    }catch (IllegalStateException e){
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        seekMusicBar.setMax(mediaPlayer.getDuration());
        updateSeeker.start();

        seekMusicBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                 mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        String endTime=createTime(mediaPlayer.getDuration());
        textEnd.setText(endTime);

        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String cTime=createTime(mediaPlayer.getCurrentPosition());
                textStart.setText(cTime);
                handler.postDelayed(this,1000);
            }
        },1000);


        play.setOnClickListener(view -> {
            if(mediaPlayer.isPlaying()){
                play.setBackgroundResource(R.drawable.ic_play);
                mediaPlayer.pause();
            }else{
                play.setBackgroundResource(R.drawable.ic_pause);
                mediaPlayer.start();

                TranslateAnimation animation=new TranslateAnimation(-25,25,-25,25);
                animation.setInterpolator(new AccelerateInterpolator());
                animation.setDuration(600);
                animation.setFillEnabled(true);
                animation.setFillAfter(true);
                animation.setRepeatMode(Animation.REVERSE);
                animation.setRepeatCount(1);
                imageView.startAnimation(animation);
            }
        });
        next.setOnClickListener(view -> {
            mediaPlayer.stop();
            mediaPlayer.release();
            pos=((pos+1)%mySongs.size());
            Uri uri1=Uri.parse(mySongs.get(pos).toString());
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri1);
            songName=mySongs.get(pos).getName();
            textSong.setText(songName);
            mediaPlayer.start();

            startAnimation(imageView,360f);
            updateSeeker.start();
            String endTime1=createTime(mediaPlayer.getDuration());
            textEnd.setText(endTime1);
            seekMusicBar.setMax(mediaPlayer.getDuration());
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                next.performClick();
            }
        });
        prev.setOnClickListener(view -> {
            mediaPlayer.stop();
            mediaPlayer.release();
            pos=((pos-1)<0 ? (mySongs.size()-1):pos-1);
            Uri uri1=Uri.parse(mySongs.get(pos).toString());
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri1);
            songName=mySongs.get(pos).getName();
            textSong.setText(songName);
            mediaPlayer.start();

            startAnimation(imageView,-360f);
            updateSeeker.start();
            String endTime11=createTime(mediaPlayer.getDuration());
            textEnd.setText(endTime11);
            seekMusicBar.setMax(mediaPlayer.getDuration());
        });
        replay.setOnClickListener(view -> {
           mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-10000);
        });
        forward.setOnClickListener(view -> {
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+10000);
        });

        int audioSessionid=mediaPlayer.getAudioSessionId();
        if(audioSessionid !=-1){
            // set custom color to the line.
            squareBarVisualizer.setColor(ContextCompat.getColor(this, R.color.purple_200));
            // define custom number of bars you want in the visualizer between (10 - 256).
            squareBarVisualizer.setDensity(65);
            // Set Spacing
            squareBarVisualizer.setGap(2);
            // Set your media player to the visualizer.
            squareBarVisualizer.setPlayer(mediaPlayer.getAudioSessionId());
        }

    }
    public void startAnimation(View view,Float degree){
        ObjectAnimator animator=ObjectAnimator.ofFloat(imageView,"rotation",0f,degree);
        animator.setDuration(1000);
        AnimatorSet animatorSet=new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();
    }
    public String createTime(int d){
        String time="";
        int min=d/1000/60;
        int sec=d/1000%60;

        time=time+min+":";
        if(sec<0){
            time+="0";
        }else{
            time+=sec;
        }
        return time;
    }
}