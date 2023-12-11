package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
public class PlayingActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private Handler handler;
    private SeekBar seekBar;
    private TextView textViewTime,  textViewNameSong,textViewNameAlbum, textViewNameArtist;
    private ImageView image;
    private ImageButton buttonPlay, buttonPrevious, buttonNext;
    private String folderPath, imagePath,albumPath;
    private int selectedItemPosition;
    int status=0;
    Bitmap ImageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);

        folderPath = getIntent().getStringExtra("folderPath");
        selectedItemPosition=getIntent().getIntExtra("selectedItemPosition",0);
        textViewNameSong=findViewById(R.id.nameSong);
        seekBar = findViewById(R.id.seekBar);
        textViewTime = findViewById(R.id.textViewTime);
        imagePath = getIntent().getStringExtra("imagePath");
        image = findViewById(R.id.image);
        albumPath=getIntent().getStringExtra("AlbumPath");
        textViewNameAlbum=findViewById(R.id.nameAlbum);
        textViewNameArtist=findViewById(R.id.nameArtist);
        buttonPrevious = findViewById(R.id.buttonPrevious);
        buttonPlay = findViewById(R.id.buttonPlay);
        buttonNext = findViewById(R.id.buttonNext);
        mediaPlayer = new MediaPlayer();
        handler = new Handler();

        prepareMediaPlayer(folderPath);
        mediaPlayer.start();
        updateSeekBar();
        buttonPlay.setOnClickListener(v -> {
            if(status==0){
                mediaPlayer.pause();
               status++;

            }else{
                mediaPlayer.start();
                status=0;
            }
            updateSeekBar();

        });

        buttonPrevious.setOnClickListener(v ->{
            playPreviousSong();

        });

        buttonNext.setOnClickListener(v -> {
            playNextSong();

        });
        mediaPlayer.setOnCompletionListener(null);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }
    private void playNextSong() {
        File folder = new File(folderPath);
        File parentFolder = folder.getParentFile();
        File[] files =  parentFolder.listFiles();

        selectedItemPosition = selectedItemPosition + 1 ;
        if(selectedItemPosition==files.length){
            selectedItemPosition =0;
        }
        playSelectedSong();
    }

    private void playPreviousSong() {
        selectedItemPosition -=   1 ;
        if( selectedItemPosition==-1){
            selectedItemPosition =0;
        }
        playSelectedSong();
    }

    private void playSelectedSong() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }

        prepareMediaPlayer(folderPath);
        mediaPlayer.start();
        updateSeekBar();
    }

    private void prepareMediaPlayer(String folderPath) {
        try {
            File folder = new File(folderPath);
            File parentFolder = folder.getParentFile();
            File[] files =  parentFolder.listFiles();

            if (files != null && files.length > 0) {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(files[selectedItemPosition].getAbsolutePath());
                String name= retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                String album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                byte[] ImageBytes = retriever.getEmbeddedPicture();
                if (ImageBytes != null) {
                     ImageBitmap = BitmapFactory.decodeByteArray(ImageBytes, 0, ImageBytes.length);
                }
                File selectedFile = files[selectedItemPosition];
                if (selectedFile.exists()) {
                    if(name!=null){
                        textViewNameSong.setText(name);
                    }


                    if(artist!=null){
                        textViewNameArtist.setText(artist);
                    }
                    if(ImageBitmap!=null){

                        image.setImageBitmap(ImageBitmap);
                    }
                    if(album !=null){
                        textViewNameAlbum.setVisibility(View.VISIBLE);
                        textViewNameAlbum.setText(album);
                    }

                    mediaPlayer.setDataSource(selectedFile.getAbsolutePath());
                    mediaPlayer.prepare();
                } else {
                    Log.d("MyApp", "Current Folder Path: " + folderPath);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }




        seekBar.setMax(mediaPlayer.getDuration());
    }




    private void updateSeekBar() {
        if (mediaPlayer != null && seekBar != null) {
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            int currentMinutes = (int) TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getCurrentPosition());
            int currentSeconds = (int) TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.getCurrentPosition()) % 60;
            int totalMinutes = (int) TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getDuration());
            int totalSeconds = (int) TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.getDuration()) % 60;
            String time = String.format("%d:%02d / %d:%02d", currentMinutes, currentSeconds, totalMinutes, totalSeconds);
            textViewTime.setText(time);

            if (mediaPlayer.isPlaying()) {
                handler.postDelayed(this::updateSeekBar, 1000);

            }
        }
    }

    private String getName(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex >= 0) {
            return fileName.substring(0, lastDotIndex);
        } else {
            return fileName;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}

