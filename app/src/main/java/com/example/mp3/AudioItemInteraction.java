package com.example.mp3;

import android.media.MediaPlayer;
import android.os.Handler;
import android.widget.SeekBar;
import android.widget.TextView;

public interface AudioItemInteraction {

    void playItemOnClick(String model, int position, SeekBar seekbar, TextView textCurrentTime,
                         TextView textTotalDuration, MediaPlayer mediaPlayer);
    void pauseItemOnClick(String model, int position, SeekBar seekbar, TextView textCurrentTime,
                          TextView textTotalDuration, MediaPlayer mediaPlayer, Handler handler);

}
