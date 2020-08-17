package com.topband.audiovideodemo.opengl.video

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class GLVideoActivity : AppCompatActivity() {
    private lateinit var gLView:GLVideoSurfaceView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gLView = GLVideoSurfaceView(this)
        setContentView(gLView)
    }
}