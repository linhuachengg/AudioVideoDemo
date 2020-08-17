package com.topband.audiovideodemo.audiorecorder

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Environment
import android.util.Log
import com.topband.audiovideodemo.PrintUtils
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.nio.ByteBuffer

class AudioRecorderHelper : Runnable{

    private lateinit var audioRecorder:AudioRecord
    private lateinit var pcmFile: File
    private var isStop = false
    private var buffer = ByteArray(1024)
    private lateinit var fileOutputStream : FileOutputStream
    private lateinit var dataOutputStream: DataOutputStream
    init {
            val bufferSize = AudioRecord.getMinBufferSize(
                16000,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT
            ) * 16
        audioRecorder = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                16000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize
            )
        pcmFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),"test.pcm")
        Log.d("lhc","pcmFile --> ${pcmFile.absolutePath}")
        }

    public fun start(){
        if (audioRecorder.recordingState == AudioRecord.STATE_UNINITIALIZED){
            throw Exception("STATE_UNINITIALIZED")
        }
        if (pcmFile.exists()){
            pcmFile.delete()
        }
        pcmFile.createNewFile()
        fileOutputStream = pcmFile.outputStream()
        dataOutputStream = DataOutputStream(fileOutputStream)
        audioRecorder.startRecording()
        Thread(this).start()
    }

    public fun stop(){
        audioRecorder.stop()
        isStop = true
    }
    override fun run() {
        while (!isStop){
            val size = audioRecorder.read(buffer,0,buffer.size)
             if (size > 0){
                 dataOutputStream.write(buffer,0,size)
                 Log.d("write-->",PrintUtils.byteArray2String(buffer))
             }
           }
        }
}