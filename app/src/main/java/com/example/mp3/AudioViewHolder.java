package com.example.mp3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.danikula.videocache.HttpProxyCacheServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static com.example.mp3.MainActivity.mediaPlayer;


public class AudioViewHolder extends RecyclerView.ViewHolder {


    CardView row_card;
    TextView row_txt;

    LinearLayout menu;
    ImageView imgPlay, imgPause;
    TextView textCurrentTime, textTotalDuration;
    SeekBar seekbar;
    ProgressBar progressBar;

    Handler handler;
    boolean prepared;

    String endPointaud="";
    String downloadState="";


    @SuppressLint("ClickableViewAccessibility")
    public AudioViewHolder(@NonNull View itemView) {
        super(itemView);

        row_card = itemView.findViewById(R.id.row_card);
        row_txt = itemView.findViewById(R.id.row_txt);


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


    public void bindData(AudioItemInteraction listener, Model model, int position) {

        if (model.type.equals("audio")) {
            row_card.setVisibility(View.VISIBLE);
            row_txt.setVisibility(View.GONE);

            //refresh ui
            imgPlay.setVisibility(View.VISIBLE);
            imgPause.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            seekbar.setProgress(0);
            textCurrentTime.setText(R.string.zero);
            textTotalDuration.setText(R.string.zero);
            handler.removeCallbacks(updater);


            if (Constant.LASTPOSITION == position) {
                Log.i("TAG", "bindData: ");
                endPointaud = (model.getLink().substring(model.getLink().lastIndexOf("/") + 1));
                playSong2(listener,model.link, position);

            } else {
                progressBar.setVisibility(View.GONE);
                imgPause.setVisibility(View.GONE);
                imgPlay.setVisibility(View.VISIBLE);
            }


            Constant.COMPLETED = true;
        } else if (model.type.equals("text")) {

            row_card.setVisibility(View.GONE);
            row_txt.setVisibility(View.VISIBLE);

            row_txt.setText(model.link);
        }

    }


    public void setOnAudioHolderListener(AudioItemInteraction listener, Model model, int position) {

        imgPlay.setOnClickListener(v -> {
            //to pause other application playing music while our app is playing
            AudioManager mAudioManager_ = (AudioManager) itemView.getContext().getSystemService(Context.AUDIO_SERVICE);
            int result = mAudioManager_.requestAudioFocus(focusChangeListener,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            }


            loadSong(model.link, position, listener);
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


        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupWindow popupwindow_obj = popupAudioDisplay(model, position, model, listener);
                popupwindow_obj.showAsDropDown(menu, 0, 0);
            }
        });


    }

    private PopupWindow popupAudioDisplay(Model model, int position, Model model1, AudioItemInteraction listener) {
        String url = model.link;
        endPointaud = (url.substring(url.lastIndexOf("/") + 1));


        final PopupWindow popupWindow = new PopupWindow(itemView.getContext());
        LayoutInflater inflater = (LayoutInflater) itemView.getContext().getSystemService(itemView.getContext().LAYOUT_INFLATER_SERVICE);

        View view = null;
        view = inflater.inflate(R.layout.popup_share_down, null);


        TextView txt = (TextView) view.findViewById(R.id.txt1);

        RelativeLayout down = (RelativeLayout) view.findViewById(R.id.down);
        RelativeLayout share = (RelativeLayout) view.findViewById(R.id.share);
        ImageView img = (ImageView) view.findViewById(R.id.img1);


        if (isAudioDownloaded()) {
            downloadState = "downloaded";
            txt.setText("پخش صوت");
            img.setImageResource(R.drawable.voice_icon);
        } else {
            txt.setText("دانلود");
            img.setImageResource(R.drawable.download_icon2);
            downloadState = "no_downloaded";
        }


        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (isAudioDownloaded()) {
                    endPointaud = (model.link.substring(model.getLink().lastIndexOf("/") + 1));
                    loadSong(model.getLink(), position, listener);

                } else {
                    listener.blogItemDownloadClicked(model, downloadState, "audio");
                }

                popupWindow.dismiss();

            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, " ");
                String sAux = "" + model.link + "\n" + "لینک دریافت اپلیکیشن  :" + "\n" + "https://mp3List.app/";
                i.putExtra(Intent.EXTRA_TEXT, sAux);
                itemView.getContext().startActivity(Intent.createChooser(i, "choose one"));
                popupWindow.dismiss();
            }
        });

        popupWindow.setFocusable(true);
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(view);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        return popupWindow;
    }


    private void loadSong(String url, int position, AudioItemInteraction listener) {


        if (Constant.LASTPOSITION != position) {

            Constant.LASTPOSITION = position;
            listener.notifyDataSetChanged();  // re refresh list and show all items from the scratch

        } else {
            imgPlay.setVisibility(View.GONE);
            imgPause.setVisibility(View.VISIBLE);

            if (!Constant.COMPLETED) {
                mediaPlayer.start();
                updateSeekbar();
            }
        }

    }


    private void playSong2(AudioItemInteraction listener, String url, int position) {

        try {

            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    handler.removeCallbacks(updater);
                    mediaPlayer.stop();
                }
            }


            imgPlay.setVisibility(View.GONE);
            imgPause.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);

            mediaPlayer = new MediaPlayer();


            if (isAudioDownloaded()) {

                mediaPlayer.reset();
                String path = App.path_save_aud + "/" + endPointaud;

                try {
                    FileInputStream fileInputStream = new FileInputStream(path);
                    mediaPlayer.setDataSource(fileInputStream.getFD());
                    fileInputStream.close();
                    mediaPlayer.prepare();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {

                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setOnBufferingUpdateListener((mediaPlayer, i) -> seekbar.setSecondaryProgress(i));
                mediaPlayer.reset();

                // to read from cache
                HttpProxyCacheServer proxyServer = new HttpProxyCacheServer.Builder(itemView.getContext()).maxCacheSize(1024 * 1024 * 1024).build();
                String proxyUrl = proxyServer.getProxyUrl(url);
                mediaPlayer.setDataSource(proxyUrl);

                mediaPlayer.prepareAsync();

            }



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

                // add these 2 below lines ,when music completed , refresh list
                Constant.LASTPOSITION = -1;
                listener.notifyDataSetChanged();
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



    private boolean isAudioDownloaded() {

        String path = App.path_save_aud;
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
//        Log.d("Files", "Size: " + files.length);
        if (files != null) {

            for (int i = 0; i < files.length; i++) {

                if (files[i].getName().equals(endPointaud)) {
                    // file has been downloaded
                    return true;
                }
            }
        }
        return false;
    }


    /******************************* update minutes and seconds *********************************/

    private final Runnable updater = new Runnable() {
        @Override
        public void run() {
            updateSeekbar();
            long currentDuration = mediaPlayer.getCurrentPosition();

            if (currentDuration > 0) {
                textCurrentTime.setText(milliSecondToTimer(currentDuration));
            } else {
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



    //to pause other application playing music while our app is playing
    AudioManager.OnAudioFocusChangeListener focusChangeListener = new AudioManager.OnAudioFocusChangeListener()
    {   @Override
    public void onAudioFocusChange (int focusChange)
    {   switch (focusChange)
    {   case AudioManager.AUDIOFOCUS_GAIN:
        Log.e("DEBUG", "##### AUDIOFOCUS_GAIN");
        break;
        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
            Log.e("DEBUG", "##### AUDIOFOCUS_LOSS_TRANSIENT");
            break;
        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
            Log.e("DEBUG", "##### AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
            break;
        case AudioManager.AUDIOFOCUS_LOSS:
            Log.e("DEBUG", "##### AUDIOFOCUS_LOSS");
            break;
    }
    }
    };
}
