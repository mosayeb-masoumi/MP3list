package com.example.mp3.util;

import android.content.Context;
import android.graphics.Typeface;

public class FontUtil {

    private Typeface font_regular;
    private Typeface font_regular_light;
    private static FontUtil ourInstance = null;

    public static FontUtil getInstance(Context context) {
        if (ourInstance == null) ourInstance = new FontUtil(context);
        return ourInstance;
    }

    private FontUtil(Context context) {
        font_regular = Typeface.createFromAsset(context.getAssets(), getPathRegularFont());
        font_regular_light = Typeface.createFromAsset(context.getAssets(), getPathRegularFontLight());
    }

    public String getPathRegularFont() {
        return "font1.TTF";
    }
    public String getPathRegularFontLight() {
        return "font1.TTF";
    }


}
