package com.example.mp3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.downloader.Progress;
import com.example.mp3.util.CustomTypefaceSpan;
import com.example.mp3.util.FontUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AudioItemInteraction {

    RecyclerView recyclerView;
    AudioAdapter adapter;

    public static MediaPlayer mediaPlayer;


    String url = "";
    String path = "";
    String endPoint = "";
    int itemType;
    ProgressDialog progresss;

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



        // Setting timeout globally for the download network requests:
        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setReadTimeout(20_000)
                .setConnectTimeout(20_000)
                .build();
        PRDownloader.initialize(getApplicationContext(), config);

    }


    @Override
    public void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void blogItemDownloadClicked(Model model, String downloadState, String type) {


        url =model.getLink();
        if (checkWriteExternalPermission()) {
                downloadFile(url);

        }else {
            askExternalStoragePermission();
        }
    }


    private void downloadFile(String url) {


            path = App.path_save_aud;
            endPoint = (url.substring(url.lastIndexOf("/") + 1));  // substring from last "/" to end


//        url = "https://api.tazkereh.app//BlogSection/c31712af-a902-4a34-a593-d8e909d130ab.mp3";

        progresss = new ProgressDialog(new ContextThemeWrapper(MainActivity.this, R.style.CustomFontDialog));
        PRDownloader.download(url, path, endPoint)
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {

                        Typeface font = Typeface.createFromAsset(getAssets(), FontUtil.getInstance(MainActivity.this).getPathRegularFont());
                        SpannableStringBuilder spannableSB = new SpannableStringBuilder("درحال دانلود...");
                        spannableSB.setSpan(new CustomTypefaceSpan("myfont.ttf", font), 0, spannableSB.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                        //    progresss = new ProgressDialog(getContext());
                        progresss.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        progresss.setCancelable(false);
                        progresss.setTitle("درحال دانلود");
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                            Drawable drawable = new ProgressBar(MainActivity.this).getIndeterminateDrawable().mutate();
                            drawable.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.green),
                                    PorterDuff.Mode.SRC_IN);
                            progresss.setIndeterminateDrawable(drawable);
                        }
                        progresss.show();
                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {
                        Log.i("TAG", "onDownloadComplete: ");
                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {
                        Log.i("TAG", "onDownloadComplete: ");
                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {

                        progresss.setMax((int) progress.totalBytes / 1000);
                        progresss.setProgress((int) progress.currentBytes / 1000);
                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {

                        Toast.makeText(MainActivity.this, "دانلود کامل شد!", Toast.LENGTH_SHORT).show();
                        progresss.dismiss();

                    }

                    @Override
                    public void onError(Error error) {
                        Toast.makeText(MainActivity.this, "خطا در دریافت ویدئو", Toast.LENGTH_SHORT).show();
                        progresss.dismiss();
                    }

           });
    }

    private void askExternalStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 555);
    }

    private boolean checkWriteExternalPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 555) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                downloadFile(url);
            } else {

                Toast.makeText(this, "برای دانلود نیاز به دسترسی می باشد", Toast.LENGTH_SHORT).show();
            }
        }

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