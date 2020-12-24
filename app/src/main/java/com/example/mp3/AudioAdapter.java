package com.example.mp3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AudioAdapter extends RecyclerView.Adapter<AudioViewHolder> {

    List<String> links;
    Context context;


    public AudioAdapter(List<String> links, Context context) {
        this.links = links;
        this.context = context;


    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new AudioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, int position) {
        String model = links.get(position);
        holder.bindData(model);
        holder.setOnAudioHolderListener(listener, model, position);
    }

    private AudioItemInteraction listener = null;
    public void setListener(AudioItemInteraction listener) {
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return links.size();
    }
}
