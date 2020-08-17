package com.topband.audiovideodemo.ffmpeg;

import android.view.Surface;

public class NativeLib {
    static {
        System.loadLibrary("native-lib");
    }



    public native static void play(String file, Surface surface);

    public native static void pause();

    public native static void cancelPause();

    public native static void resetSurface( Surface surface);
}
