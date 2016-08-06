package me.pkhope.picturepro.utils;

import android.graphics.Bitmap;

/**
 * Created by pkhope on 2016/8/6.
 */
public class PicUtils {

    static {
        System.loadLibrary("JniLibNameXM");
    }

    public native String getOriginalPixels(Bitmap source, Bitmap mosaic);
}
