package com.topband.audiovideodemo.ffmpeg

import android.content.Intent
import android.os.Bundle
import android.text.Layout
import android.util.Log
import android.view.Gravity
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.topband.audiovideodemo.util.ContentUtil

class FFmpegVideoActivity : AppCompatActivity() {
    private lateinit var surfaceHolder: SurfaceHolder
    private var isPause = false
    private  var isStart = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var frameLayout = FrameLayout(this)
        var layoutParamsMatch = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT)
        frameLayout.layoutParams = layoutParamsMatch
        var surfaceView =  SurfaceView(this)
         surfaceHolder = surfaceView.holder
        surfaceView.layoutParams = layoutParamsMatch
        surfaceView.holder.addCallback(object:SurfaceHolder.Callback{
            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
                Toast.makeText(this@FFmpegVideoActivity,"surfaceChanged",Toast.LENGTH_SHORT).show()
               if(isStart) Log.e("VideoPlayer","start true") else Log.e("VideoPlayer","start false")
                if (isStart){
                    NativeLib.resetSurface(holder.surface)
                    Log.e("VideoPlayer","reset surface")
                }
            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
                Toast.makeText(this@FFmpegVideoActivity,"surfaceDestroyed",Toast.LENGTH_SHORT).show()
                if (isStart){
                    if (!isPause) {
                        NativeLib.pause()
                        isPause = true
                    }
                }

            }

            override fun surfaceCreated(holder: SurfaceHolder) {
                Toast.makeText(this@FFmpegVideoActivity,"surfaceCreated",Toast.LENGTH_SHORT).show()
            }

        })

        var linearLayout = LinearLayout(this)
        var linearLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayoutParams.gravity = Gravity.TOP
        var button = Button(this)
        var layoutParamsWrap =  FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT)
        button.layoutParams = layoutParamsWrap
        button.text = "Select file"
        button.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "*/*"
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            this.startActivityForResult(intent, 123)
        }
        var buttonPause = Button(this)
        buttonPause.layoutParams = layoutParamsWrap
        buttonPause.text = "pause"
        buttonPause.setOnClickListener {
            if (isPause){
                isPause = false
                NativeLib.cancelPause()
            } else {
                isPause = true
                NativeLib.pause()
            }
        }

        linearLayout.addView(button)
        linearLayout.addView(buttonPause)

        frameLayout.addView(surfaceView)
        frameLayout.addView(linearLayout)
        setContentView(frameLayout)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null && data.data != null) {
            val filePath: String = ContentUtil.getPath(this, data.data)
            onSelectedFile(filePath)
        }
    }

    private fun onSelectedFile(filePath: String) {
        isStart = true
        Thread {
            NativeLib.play(filePath,surfaceHolder.surface)
        }.start()
    }


}