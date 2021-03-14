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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);


        List<String> links = new ArrayList<>();
        links.add("https://file-examples-com.github.io/uploads/2017/11/file_example_MP3_700KB.mp3");
        links.add("https://file-examples-com.github.io/uploads/2017/11/file_example_MP3_1MG.mp3");
        links.add("https://file-examples-com.github.io/uploads/2017/11/file_example_MP3_700KB.mp3");
        links.add("https://file-examples-com.github.io/uploads/2017/11/file_example_MP3_1MG.mp3");



        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new AudioAdapter(links, this);
        adapter.setListener(this);
        recyclerView.setAdapter(adapter);

    }


    @Override
    public void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }
}