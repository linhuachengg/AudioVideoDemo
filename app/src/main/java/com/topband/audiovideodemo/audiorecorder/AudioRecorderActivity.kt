package com.topband.audiovideodemo.audiorecorder

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.topband.audiovideodemo.R
import com.topband.audiovideodemo.util.PCMUtil
import kotlinx.android.synthetic.main.activity_audio.*

class AudioRecorderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio)
        var audioRecorderHelper = AudioRecorderHelper()
        val audioTrackManager = AudioTrackManager.getInstance()
        start.setOnClickListener {
            audioRecorderHelper.start()
        }
        stop.setOnClickListener {
           audioRecorderHelper.stop()
        }
        play.setOnClickListener {
            audioTrackManager.startPlay("/storage/emulated/0/Music/test.pcm")
        }
        convert.setOnClickListener {
            PCMUtil.convertPcm2Wav("/storage/emulated/0/Music/test.pcm","/storage/emulated/0/Music/test.wav",16000,1,16)
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA),
                100
            )
        }
    }
}