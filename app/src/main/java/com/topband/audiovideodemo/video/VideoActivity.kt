package com.topband.audiovideodemo.video

import android.os.Bundle
import android.view.SurfaceHolder
import androidx.appcompat.app.AppCompatActivity
import com.topband.audiovideodemo.R
import kotlinx.android.synthetic.main.activity_viceo.*

class VideoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viceo)
        surface.holder.addCallback(object:SurfaceHolder.Callback{
            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
            }

            override fun surfaceCreated(holder: SurfaceHolder?) {
                 VideoThread("http://video.topband-cloud.com/ce665250c7d741bb9651cb6c3b4629ce/e6749ce61de54989b9aa923c8b8e9c66-eb7d285de0108ca8b459ae377806d76b-sd.mp4",holder?.surface).start()
                 AudioThread("http://video.topband-cloud.com/ce665250c7d741bb9651cb6c3b4629ce/e6749ce61de54989b9aa923c8b8e9c66-eb7d285de0108ca8b459ae377806d76b-sd.mp4").start()
            }

        })
    }
}