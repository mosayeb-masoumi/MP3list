package com.example.mp3;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ui.PlayerView;
import com.potyvideo.library.AndExoPlayerView;

public class AudioViewHolder extends RecyclerView.ViewHolder {

    TextView startTime,endTime,title;
    LinearLayout menu;
    RelativeLayout mainLay,play_lay;
    ProgressBar loading;
    SeekBar seekBar;
    ImageView img,playPause;
    AndExoPlayerView andExoPlayerView;
    PlayerView c;

    public AudioViewHolder(@NonNull View itemView) {
        super(itemView);

        startTime = (TextView) itemView.findViewById(R.id.start);
        endTime = (TextView) itemView.findViewById(R.id.end);
        title = (TextView) itemView.findViewById(R.id.title);
        menu = (LinearLayout) itemView.findViewById(R.id.menu);
        playPause = (ImageView) itemView.findViewById(R.id.playPause);
        img = (ImageView) itemView.findViewById(R.id.img);
        seekBar = (SeekBar) itemView.findViewById(R.id.seekbar);
        mainLay=(RelativeLayout) itemView.findViewById(R.id.main);
        play_lay=(RelativeLayout)itemView.findViewById(R.id.play_lay);
        loading=(ProgressBar) itemView.findViewById(R.id.loading);
        andExoPlayerView = itemView.findViewById(R.id.andExoPlayerView);
        c= itemView.findViewById(R.id.exoplayerView);
        itemView.findViewById(R.id.title).setSelected(true);
    }

    public void bindData(String model) {
    }

    public void setOnAudioHolderListener(AudioItemInteraction listener, String model, int position) {
    }
}
