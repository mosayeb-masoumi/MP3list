package com.example.mp3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

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
        links.add("https://file-examples-com.github.io/uploads/2017/11/file_example_MP3_2MG.mp3");


        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new AudioAdapter(links, this);
//        adapter.setListener(this);
        recyclerView.setAdapter(adapter);

    }
}