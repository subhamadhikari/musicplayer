package com.example.musicbottom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RecyclerAdapter.onItemListClick {
    public static final int REQUEST_CODE = 1;
    RecyclerView recyclerView ;
    RecyclerAdapter recyclerAdapter;
    ArrayList<Songs> songsNames;
    private ConstraintLayout playerSheet;
    private BottomSheetBehavior bottomSheetBehavior;
    private MediaPlayer mediaPlayer = null;
    private boolean isPlaying = false;
    private Songs songToPlay = null;
    private ImageButton playBtn;
    private TextView fileName , playerState;
    private SeekBar seekBar;
    private Handler seekBarHandler;
    private Runnable updateSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getPermission();

        playBtn = findViewById(R.id.player_play_btn);
        fileName = findViewById(R.id.player_fileName);
        playerState = findViewById(R.id.player_header_state);


        //------------------------------------------------------------------------------------------

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        if (!(songsNames.size() < 1)){
            recyclerAdapter = new RecyclerAdapter(this,songsNames,this);
            recyclerView.setAdapter(recyclerAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false));
        }
        //------------------------------------------------------------------------------------------
        seekBar  =findViewById(R.id.seekBar);
        playerSheet = findViewById(R.id.playerSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(playerSheet);

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        //------------------------------------------------------------------------------------------
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPlaying){
                    pauseAudio();
                }else {
                    if (songToPlay != null ){
                        resumeAudio();
                    }

                }
            }
        });

        if (songToPlay != null) {
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    pauseAudio();
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    int i = seekBar.getProgress();
                    mediaPlayer.seekTo(i);
                    resumeAudio();
                }
            });
        }



    }

    private void getPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODE);
        }else {
            songsNames = getAudioFiles(this);
//            recyclerUI();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                songsNames = getAudioFiles(this);
//                recyclerUI();

            }else {
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODE);
        }
        }
    }

    public ArrayList<Songs> getAudioFiles(Context ctx){
        ArrayList<Songs> tempList = new ArrayList<>();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media._ID
        };

        Cursor cursor = ctx.getContentResolver().query(uri,projection,null,null,null);

        if (cursor != null){
            while (cursor.moveToNext()){
                String title = cursor.getString(0);
                String path = cursor.getString(1);
                String duration = cursor.getString(2);
                String id = cursor.getString(3);

                Songs song = new Songs(title,path,duration,id);
                tempList.add(song);
            }
            cursor.close();
        }

        return tempList;

    }

    public void recyclerUI(){

    }

    @Override
    public void onClickListener(Songs song, int position) {
        songToPlay = song ;
        if (isPlaying){
            stopAudio();
            playAudio(songToPlay);
        }else {
            playAudio(songToPlay);
        }
    }

    private void pauseAudio(){
        mediaPlayer.pause();
        playBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_circle_outline_24,null));
        isPlaying = false;
        seekBarHandler.removeCallbacks(updateSeekBar);
    }
    private void resumeAudio(){
        mediaPlayer.start();
        playBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_pause_circle_outline_24,null));
        isPlaying = true;

        updateRunnable();
        seekBarHandler.postDelayed(updateSeekBar,500);
    }

    private void stopAudio() {
        mediaPlayer.stop();
        playBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_circle_outline_24,null));
        playerState.setText("Not Playing");
        isPlaying = false;
        seekBarHandler.removeCallbacks(updateSeekBar);
    }

    private void playAudio(Songs songToPlay) {
        mediaPlayer = new MediaPlayer();

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        try {
            mediaPlayer.setDataSource(songToPlay.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        playBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_pause_circle_outline_24,null));
        fileName.setText(songToPlay.getTitle());
        playerState.setText("Playing");

        isPlaying = true;

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stopAudio();
                playerState.setText("Finished");
            }
        });

        seekBar.setMax(mediaPlayer.getDuration());
        seekBarHandler = new Handler();
        updateRunnable();
        seekBarHandler.postDelayed(updateSeekBar,0);

    }

    private void updateRunnable() {
        updateSeekBar = new Runnable() {
            @Override
            public void run() {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                seekBarHandler.postDelayed(this,500);
            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isPlaying){
            stopAudio();
        }
    }
}