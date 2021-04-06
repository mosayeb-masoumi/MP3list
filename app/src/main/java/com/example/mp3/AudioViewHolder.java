package com.example.mp3;

import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.danikula.videocache.HttpProxyCacheServer;

import java.io.IOException;

import static com.example.mp3.MainActivity.mediaPlayer;


public class AudioViewHolder extends RecyclerView.ViewHolder {

    LinearLayout menu;
    ImageView imgPlay, imgPause;
    TextView textCurrentTime, textTotalDuration;
    SeekBar seekbar;
    ProgressBar progressBar;

//    MediaPlayer mediaPlayer = new MediaPlayer();

    Handler handler;

    boolean prepared ;


    @SuppressLint("ClickableViewAccessibility")
    public AudioViewHolder(@NonNull View itemView) {
        super(itemView);
        menu = itemView.findViewById(R.id.menu);
        imgPlay = itemView.findViewById(R.id.img_play);
        imgPause = itemView.findViewById(R.id.img_pause);
        textCurrentTime = itemView.findViewById(R.id.textCurrentTime);
        textTotalDuration = itemView.findViewById(R.id.textTotalDuration);
        seekbar = itemView.findViewById(R.id.seekbar);
        progressBar = itemView.findViewById(R.id.progressbar);

        prepared = false;
        seekbar.setMax(100);
        handler = new Handler();
    }


    public void bindData(String model, int position) {

        //refresh ui
        imgPlay.setVisibility(View.VISIBLE);
        imgPause.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        seekbar.setProgress(0);
        textCurrentTime.setText(R.string.zero);
        textTotalDuration.setText(R.string.zero);
        handler.removeCallbacks(updater);

        if(Constant.LASTPOSITION ==position){
            Log.i("TAG", "bindData: ");
            progressBar.setVisibility(View.VISIBLE);
            imgPause.setVisibility(View.GONE);
            imgPlay.setVisibility(View.GONE);
        }
//        else{
//            mediaPlayer.reset();
//        }

        Constant.COMPLETED = true;
    }


    public void setOnAudioHolderListener(AudioItemInteraction listener, String url, int position) {

        imgPlay.setOnClickListener(v -> {
            playSong(url , position , listener);
        });

        imgPause.setOnClickListener(v -> {
            Constant.COMPLETED = false;
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

    private void playSong(String url, int position, AudioItemInteraction listener) {

        try {

            if(Constant.LASTPOSITION != position){
                //new row clicked
                listener.notifyDataSetChanged();  // re refresh list and show all items from the scratch
                Constant.LASTPOSITION = position;

                imgPlay.setVisibility(View.GONE);
                imgPause.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setOnBufferingUpdateListener((mediaPlayer, i) -> seekbar.setSecondaryProgress(i));
                mediaPlayer.reset();
            }else{
                imgPlay.setVisibility(View.GONE);
                imgPause.setVisibility(View.VISIBLE);

                if(!Constant.COMPLETED){
                    mediaPlayer.start();
                }

            }



            // to read from cache
            HttpProxyCacheServer proxyServer = new HttpProxyCacheServer.Builder(itemView.getContext()).maxCacheSize(1024 * 1024 * 1024).build();
            String proxyUrl = proxyServer.getProxyUrl(url);
            mediaPlayer.setDataSource(proxyUrl);
//            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();

//
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

                Constant.COMPLETED = true;
                seekbar.setProgress(0);
                seekbar.setSecondaryProgress(0);
                imgPlay.setVisibility(View.VISIBLE);
                imgPause.setVisibility(View.GONE);
                textCurrentTime.setText(R.string.zero);
                textTotalDuration.setText(R.string.zero);
                mediaPlayer.reset();
                handler.removeCallbacks(updater);
            });


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


        } catch (Exception exception) {
            Log.i("TAG", "playSong: ");
        }

        updateSeekbar();


    }















/******************************* update minutes and seconds *********************************/

    private final Runnable updater = new Runnable() {
        @Override
        public void run() {
            updateSeekbar();
            long currentDuration = mediaPlayer.getCurrentPosition();

            if(currentDuration>0){
                textCurrentTime.setText(milliSecondToTimer(currentDuration));
            }else{
                textCurrentTime.setText(R.string.zero);
            }

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
