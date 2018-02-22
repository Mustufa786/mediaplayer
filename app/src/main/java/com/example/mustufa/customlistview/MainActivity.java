package com.example.mustufa.customlistview;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private ArrayList<HashMap<String,String>> musicLibrary;
    private ListView mListView;
    private boolean storagePermssionGranted;
    String[] items;
    private MediaPlayer mediaPlayer;
    private Button mPlay,mNext,mPrevious;
    ArrayList<File> mySongs;
    int pos;
    Uri songUri;
    File[] files;
    AlertDialog.Builder dialogBuilder;
    MyCustomAdapter myCustomAdapter;
    private SeekBar mSeekbar;
    private Thread seekbarReady;



    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: Activity Started..");

        if(storagePermssionGranted) {

            updateSongsList();
        }



            //widgets
        mPlay = findViewById(R.id.play);
        mNext = findViewById(R.id.next);
        mPrevious = findViewById(R.id.previous);
        mSeekbar = findViewById(R.id.seekBar);

        mPlay.setOnClickListener(this);
        mNext.setOnClickListener(this);
        mPrevious.setOnClickListener(this);




        //checking for storage permssion
        checkingStoragePermission();

        seekbarReady = new Thread(){

            @Override
            public void run() {

                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = 0;
                mSeekbar.setMax(totalDuration);

                while (currentPosition < totalDuration)

                    try {
                        Thread.sleep(500);
                        currentPosition = mediaPlayer.getCurrentPosition();
                        mSeekbar.setProgress(currentPosition);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
            }
        };

        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: Button Init...");

        switch (view.getId()) {

            case R.id.play:

                if(mediaPlayer.isPlaying()) {

                    mediaPlayer.seekTo(0);
                    mediaPlayer.pause();

                }else {
                    mediaPlayer.start();
                }

                break;

            case R.id.next:
                if(mediaPlayer != null) {
                    mediaPlayer.seekTo(0);
                    seekbarReady.interrupt();
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.release();

                }

                pos = (pos+1 >= mySongs.size()) ? pos-1 : pos+1;
                songUri = Uri.parse(mySongs.get(pos).toString());
                mediaPlayer = MediaPlayer.create(MainActivity.this,songUri);
                mediaPlayer.start();

                break;

            case R.id.previous:
                mediaPlayer.seekTo(0);
                seekbarReady.interrupt();
                mediaPlayer.stop();
                mediaPlayer.release();

//                pos = (pos-1 <0)? mySongs.size()-1: pos -1;
                if(pos-1<0) {

                   pos = mySongs.size() -1;
                }else {
                    pos = pos-1;
                }
                songUri = Uri.parse(mySongs.get(pos).toString());
                mediaPlayer = MediaPlayer.create(MainActivity.this,songUri);
                mediaPlayer.start();

                break;
        }

    }


     void updateSongsList() {

         mySongs = getSongs(Environment.getExternalStorageDirectory());

        items = new String[mySongs.size()];
        for(int i =0; i<mySongs.size(); i++){

            items[i] = mySongs.get(i).getName();
        }

         mListView = findViewById(R.id.list);
         myCustomAdapter = new MyCustomAdapter(MainActivity.this,items);
         mListView.setAdapter(myCustomAdapter);

         mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                 pos = position;
                 songUri = Uri.parse(mySongs.get(pos).toString());

                 if(mediaPlayer !=null) {
                     mediaPlayer.seekTo(0);
                     mediaPlayer.stop();
                     mediaPlayer.reset();
                     mediaPlayer.release();
                 }
                 mediaPlayer = MediaPlayer.create(MainActivity.this,songUri);
                 mediaPlayer.start();
                 if(!seekbarReady.isAlive()) {
                     seekbarReady.start();
                 }


             }
         });

    }

    public ArrayList<File> getSongs(File file) {

        ArrayList<File> songs = new ArrayList<File>();
        files = file.listFiles();

        for(File song : files) {

            if(song.isDirectory()) {

                songs.addAll(getSongs(song));

            }
            else {
                if(song.getName().endsWith("mp3")) {

                    songs.add(song);
                }
            }
        }

        return songs;
    }

    void checkingStoragePermission() {

        Log.d(TAG, "checkingStoragePermission: Checking for Read storage permssion..");

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {

            storagePermssionGranted = true;
            updateSongsList();

        }else {

            ActivityCompat.requestPermissions(this,new String[]
                    {Manifest.permission.READ_EXTERNAL_STORAGE},0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {

                storagePermssionGranted = true;
                updateSongsList();

                
            }else {
                ActivityCompat.requestPermissions(this,new String[]
                        {Manifest.permission.READ_EXTERNAL_STORAGE},0);
            }


            Log.d(TAG, "onRequestPermissionsResult: Permissision granted..");
            storagePermssionGranted = true;
            updateSongsList();

        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        updateSongsList();

    }

    @Override
    protected void onStart() {
        super.onStart();

        updateSongsList();
    }
}
