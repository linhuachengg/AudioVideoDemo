package com.topband.audiovideodemo.video;

import android.media.*;
import android.util.Log;
import android.view.Surface;
import com.topband.audiovideodemo.audiorecorder.AudioTrackManager;

import java.io.IOException;
import java.nio.ByteBuffer;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class AudioThread extends Thread {
    private MediaExtractor mediaExtractor = new MediaExtractor();
    private MediaCodec mediaCodec = null;
    private boolean isPlaying = true;
    private AudioTrack audioTrack = null;
    private short[] mAudioOutTempBuf;
    MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
    private int audioInputBufferSize;
    public AudioThread(String path){
        try {
            mediaExtractor.setDataSource(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        int trackCount = mediaExtractor.getTrackCount();
        int audioTrackIndex = -1;
        for (int i = 0; i < trackCount; i++){
            MediaFormat mediaFormat = mediaExtractor.getTrackFormat(i);
            String mime = mediaFormat.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("audio/")){
                audioTrackIndex = i;
                break;
            }
        }
        if (audioTrackIndex >= 0){
            MediaFormat audioFormat = mediaExtractor.getTrackFormat(audioTrackIndex);
            if (audioTrack == null) {
                int audioChannels = audioFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
                int audioSampleRate = audioFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE);
                int minBufferSize = AudioTrack.getMinBufferSize(audioSampleRate,
                        (audioChannels == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO),
                        AudioFormat.ENCODING_PCM_16BIT);
                audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                        audioSampleRate,
                        (audioChannels == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO),
                        AudioFormat.ENCODING_PCM_16BIT,
                        minBufferSize,
                        AudioTrack.MODE_STREAM);
                mAudioOutTempBuf = new short[minBufferSize / 2];
                audioTrack.play();
            }
            mediaExtractor.selectTrack(audioTrackIndex);
            try {
                mediaCodec = MediaCodec.createDecoderByType(audioFormat.getString(MediaFormat.KEY_MIME));
                mediaCodec.configure(audioFormat,null,null,0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mediaCodec == null){
            Log.d("lhc","mediacodec null");
            return;
        }
        mediaCodec.start();
        ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
        ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers();
        boolean isEOS = false;
        long startMs = System.currentTimeMillis();
        while (!Thread.interrupted()) {
            if (!isPlaying) {
                continue;
            }
            //将资源传递到解码器
            if (!isEOS) {
                isEOS = putBufferToCoder(mediaExtractor, mediaCodec, inputBuffers);
            }
            int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 1000);
            switch (outputBufferIndex) {
                case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                    Log.v(TAG, "format changed");
                    break;
                case MediaCodec.INFO_TRY_AGAIN_LATER:
                    Log.v(TAG, "解码当前帧超时");
                    break;
                case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                   outputBuffers = mediaCodec.getOutputBuffers();
                    Log.v(TAG, "output buffers changed");
                    break;
                default:
                    //直接渲染到Surface时使用不到outputBuffer
                    ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
                    //延时操作
                    //如果缓冲区里的可展示时间>当前视频播放的进度，就休眠一下
                    // sleepRender(bufferInfo, startMs);
                    render(outputBuffer,bufferInfo);
                    //渲染
                    mediaCodec.releaseOutputBuffer(outputBufferIndex, true);
                    break;
            }

            if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                Log.v(TAG, "buffer stream end");
                break;
            }
        }//end while
        mediaCodec.stop();
        mediaCodec.release();
        mediaExtractor.release();
    }
    //将缓冲区传递至解码器
    private boolean putBufferToCoder(MediaExtractor extractor, MediaCodec decoder, ByteBuffer[] inputBuffers) {
        boolean isMediaEOS = false;
        int inputBufferIndex = decoder.dequeueInputBuffer(1000);
        if (inputBufferIndex >= 0) {
            ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
            int sampleSize = extractor.readSampleData(inputBuffer, 0);
            if (sampleSize < 0) {
                decoder.queueInputBuffer(inputBufferIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                isMediaEOS = true;
                Log.v(TAG, "media eos");
            } else {
                decoder.queueInputBuffer(inputBufferIndex, 0, sampleSize, extractor.getSampleTime(), 0);
                extractor.advance();
            }
        }
        return isMediaEOS;
    }

    private void render(ByteBuffer outputBuffer ,
                MediaCodec.BufferInfo bufferInfo) {
        // 8bit --> 16bit
        if (mAudioOutTempBuf.length < bufferInfo.size / 2) {
            mAudioOutTempBuf = new short[bufferInfo.size / 2];
        }
        outputBuffer.position(0);
        outputBuffer.asShortBuffer().get(mAudioOutTempBuf, 0, bufferInfo.size/2);
        audioTrack.write(mAudioOutTempBuf, 0, bufferInfo.size / 2);
    }
}

