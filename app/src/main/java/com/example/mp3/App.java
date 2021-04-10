package com.example.mp3;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import java.io.File;

public class App extends Application {

    public static Context myContext;
    public static String path_save_aud="";
    public static String path_save_vid="";

    @Override
    public void onCreate() {
        super.onCreate();

        myContext= this;



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){  //for android api 11 and upper
            path_save_aud= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + myContext.getResources().getString(R.string.app_name)+ File.separator+"audio";
            path_save_vid=  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+ File.separator + myContext.getResources().getString(R.string.app_name)+ File.separator+"video";

        }
        else{
            path_save_aud=  Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + myContext.getResources().getString(R.string.app_name)+ File.separator+"audio";
            path_save_vid=  Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + myContext.getResources().getString(R.string.app_name)+ File.separator+"video";
        }



        final File newFile2 = new File(path_save_aud);
        newFile2.mkdir();
        newFile2.mkdirs();
        final File newFile4 = new File(path_save_vid);
        newFile4.mkdir();
        newFile4.mkdirs();

    }
}
