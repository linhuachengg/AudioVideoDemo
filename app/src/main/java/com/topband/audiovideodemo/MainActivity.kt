package com.topband.audiovideodemo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import com.topband.audiovideodemo.audiorecorder.AudioRecorderActivity
import com.topband.audiovideodemo.camera.CameraTest
import com.topband.audiovideodemo.camera.OpenglCameraTest
import com.topband.audiovideodemo.ffmpeg.FFmpegVideoActivity
import com.topband.audiovideodemo.ffmpeg.NativeLib
import com.topband.audiovideodemo.opengl.OpenGLES20Activity
import com.topband.audiovideodemo.opengl.video.GLVideoActivity
import com.topband.audiovideodemo.video.VideoActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.log

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Example of a call to a native method
        sample_text.text = "ffmpeg video${getString(R.string.app_name)}"

        sample_text.setOnClickListener {
            startActivity(Intent(this,FFmpegVideoActivity().javaClass))
        }
        audio_recorder.setOnClickListener {
            startActivity(Intent(this,AudioRecorderActivity().javaClass))
        }
        video.setOnClickListener {
            startActivity(Intent(this,VideoActivity().javaClass))
        }
        opengl_trangle.setOnClickListener {
            startActivity(Intent(this,OpenGLES20Activity().javaClass))
        }
        opengl_video.setOnClickListener {
            startActivity(Intent(this,GLVideoActivity().javaClass))
        }
        camera.setOnClickListener {
            startActivity(Intent(this,CameraTest().javaClass))
        }
        gl_camera.setOnClickListener {
            startActivity(Intent(this,OpenglCameraTest().javaClass))
        }
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            &&ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE),100)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 100){
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Log.e("da","failed");
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

}
