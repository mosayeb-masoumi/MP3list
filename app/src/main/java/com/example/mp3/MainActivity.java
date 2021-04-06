package com.example.mp3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AudioItemInteraction {

    RecyclerView recyclerView;
    AudioAdapter adapter;

    public static MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);


        List<Model> list = new ArrayList<>();
        list.add(new Model("1" , "https://file-examples-com.github.io/uploads/2017/11/file_example_MP3_700KB.mp3" ,"audio"));
        list.add(new Model("1" , "https://file-examples-com.github.io/uploads/2017/11/file_example_MP3_700KB.mp3" ,"audio"));
        list.add(new Model("1" , "https://file-examples-com.github.io/uploads/2017/11/file_example_MP3_700KB.mp3" ,"audio"));
        list.add(new Model("2" , "text1" ,"text"));
        list.add(new Model("7" , "text7" ,"text"));
        list.add(new Model("3" , "https://file-examples-com.github.io/uploads/2017/11/file_example_MP3_1MG.mp3" ,"audio"));
        list.add(new Model("4" , "text2" ,"text"));
        list.add(new Model("5" , "https://file-examples-com.github.io/uploads/2017/11/file_example_MP3_700KB.mp3" ,"audio"));
        list.add(new Model("6" , "text6" ,"text"));




        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new AudioAdapter(list, this);
        adapter.setListener(this);
        recyclerView.setAdapter(adapter);

    }


    @Override
    public void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }


//    @Override
//    protected void onStop() {
//        super.onStop();
//
//        Constant.LASTPOSITION = -1;
//        adapter.notifyDataSetChanged();
//
//        if (mediaPlayer != null) {
//            if (mediaPlayer.isPlaying())
//                mediaPlayer.stop();
//        }
//
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Constant.LASTPOSITION = -1;
        adapter.notifyDataSetChanged();

        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying())
                mediaPlayer.stop();
        }
    }
}