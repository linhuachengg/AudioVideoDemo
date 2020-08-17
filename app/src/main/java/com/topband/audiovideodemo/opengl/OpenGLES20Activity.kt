package com.topband.audiovideodemo.opengl

import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class OpenGLES20Activity : AppCompatActivity() {

        private lateinit var gLView: GLSurfaceView

        public override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            // Create a GLSurfaceView instance and set it
            // as the ContentView for this Activity.
            gLView = MyGLSurfaceView(this)
            setContentView(gLView)
        }
}