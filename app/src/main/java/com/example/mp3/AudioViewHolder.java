package com.example.mp3;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;


public class AudioViewHolder extends RecyclerView.ViewHolder {

    LinearLayout menu;
    ImageView imgPlay, imgPause;
    TextView textCurrentTime, textTotalDuration;
    SeekBar seekbar;
    ProgressBar progressBar;

    MediaPlayer mediaPlayer = new MediaPlayer();

    Handler handler;

    boolean prepared ;


    @SuppressLint("ClickableViewAccessibility")
    public AudioViewHolder(@NonNull View itemView) {
        super(itemView);
        menu = (LinearLayout) itemView.findViewById(R.id.menu);
        imgPlay = itemView.findViewById(R.id.img_play);
        imgPause = itemView.findViewById(R.id.img_pause);
        textCurrentTime = itemView.findViewById(R.id.textCurrentTime);
        textTotalDuration = itemView.findViewById(R.id.textTotalDuration);
        seekbar = itemView.findViewById(R.id.seekbar);
        progressBar = itemView.findViewById(R.id.progressbar);

        prepared = false;
        seekbar.setMax(100);
//   /     mediaPlayer = new MediaPlayer();
        handler = new Handler();


        mediaPlayer.setOnBufferingUpdateListener((mediaPlayer, i) -> seekbar.setSecondaryProgress(i));

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                imgPlay.setVisibility(View.GONE);
                imgPause.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

                textTotalDuration.setText(milliSecondToTimer(mediaPlayer.getDuration()));
                mediaPlayer.start();
                updateSeekbar();

                prepared = true;
            }
        });

        mediaPlayer.setOnCompletionListener(mediaPlayer -> {
            Constant.ISRUNNING = false;
            seekbar.setProgress(0);
            seekbar.setSecondaryProgress(0);
            imgPlay.setVisibility(View.VISIBLE);
            imgPause.setVisibility(View.GONE);
            textCurrentTime.setText("0:00");
            textTotalDuration.setText("0:00");
            mediaPlayer.reset();
//                 prepareMediaPlayer();
        });




//        seekbar.setOnTouchListener((view, motionEvent) -> {
//
//            SeekBar seekBar = (SeekBar) view;
//            int playPosition = (mediaPlayer.getDuration() / 100) * seekBar.getProgress();
//            mediaPlayer.seekTo(playPosition);
//            textCurrentTime.setText(milliSecondToTimer(mediaPlayer.getCurrentPosition()));
//            return false;
//        });




        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (fromUser) {
                    long seekBarProgress = seekBar.getProgress();
                    int playPosition = (int) ((mediaPlayer.getDuration() / 100) * seekBarProgress);
                    mediaPlayer.seekTo(playPosition);
                    textCurrentTime.setText(milliSecondToTimer(mediaPlayer.getCurrentPosition()));
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


    public void bindData(String model) {
    }


    public void setOnAudioHolderListener(AudioItemInteraction listener, String url, int position) {

        imgPlay.setOnClickListener(v -> {

            if (!Constant.ISRUNNING) {
                Constant.ISRUNNING = true;


//                if(Constant.POSITION !=position){
//                    Constant.POSITION = position;
////                    mediaPlayer.reset();
//                }

//                mediaPlayer.stop();
//                mediaPlayer.release();

                playSong(url);

            } else {
                Toast.makeText(itemView.getContext(), "stop running player", Toast.LENGTH_SHORT).show();
            }

        });

        imgPause.setOnClickListener(v -> {
            Constant.ISRUNNING   = false;
            imgPlay.setVisibility(View.VISIBLE);
            imgPause.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);

            if (mediaPlayer.isPlaying()) {
                handler.removeCallbacks(updater);
                mediaPlayer.pause();
            }

        });


        menu.setOnClickListener(v -> Toast.makeText(itemView.getContext(), "menu clicked!", Toast.LENGTH_SHORT).show());


    }

    private void playSong(String url) {


        imgPlay.setVisibility(View.GONE);
        imgPause.setVisibility(View.VISIBLE);
//        progressBar.setVisibility(View.VISIBLE);




        try {
//            mediaPlayer.reset();
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();

        } catch (Exception exception) {

//            Toast.makeText(itemView.getContext(), "" + exception.getMessage(), Toast.LENGTH_SHORT).show();
        }



        mediaPlayer.start();
        updateSeekbar();

    }

//        else{
//
//            imgPlay.setVisibility(View.GONE);
//            imgPause.setVisibility(View.VISIBLE);
//            progressBar.setVisibility(View.GONE);
//            mediaPlayer.start();
//            updateSeekbar();
//        }


//    private void prepareMediaPlayer(String url) {
//
//        try {
//
//            mediaPlayer.setDataSource(url);
//            mediaPlayer.prepare();
//            textTotalDuration.setText(milliSecondToTimer(mediaPlayer.getDuration()));
//
//        } catch (Exception exception) {
//
//            Toast.makeText(itemView.getContext(), "" + exception.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }


    private Runnable updater = new Runnable() {
        @Override
        public void run() {
            updateSeekbar();
            long currentDuration = mediaPlayer.getCurrentPosition();
            textCurrentTime.setText(milliSecondToTimer(currentDuration));
        }
    };


    private void updateSeekbar() {
        if (mediaPlayer.isPlaying()) {
            seekbar.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration()) * 100));
            handler.postDelayed(updater, 1000);
        }

    }


    private String milliSecondToTimer(long milliSeconds) {

        String timerString = "";
        String secondsString;
        int hour = (int) (milliSeconds / (1000 * 60 * 60));
        int minutes = (int) (milliSeconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliSeconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        if (hour > 0) {
            timerString = hour + ":";
        }

        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        timerString = timerString + minutes + ":" + secondsString;
        return timerString;
    }
}
