package com.topband.audiovideodemo.video;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;
import android.view.Surface;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

public class VideoThread extends Thread {
    private final  String TAG = this.getClass().getSimpleName();
    private MediaExtractor mediaExtractor = new MediaExtractor();
    private MediaCodec mediaCodec = null;
    private Surface surface = null;
    private boolean isPlaying = true;
    private long startTime = -1L;

    public VideoThread(String path, Surface surface){
        try {
            mediaExtractor.setDataSource(path);
            this.surface = surface;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        int trackCount = mediaExtractor.getTrackCount();
        int videoTrackIndex = -1;
        for (int i = 0; i < trackCount; i++){
            MediaFormat mediaFormat = mediaExtractor.getTrackFormat(i);
            String mime = mediaFormat.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("video/")){
                videoTrackIndex = i;
                break;
            }
        }
       if (videoTrackIndex >= 0){
        MediaFormat videoFormat = mediaExtractor.getTrackFormat(videoTrackIndex);
         int width = videoFormat.getInteger(MediaFormat.KEY_WIDTH);
         int height = videoFormat.getInteger(MediaFormat.KEY_HEIGHT);
         long duration = videoFormat.getLong(MediaFormat.KEY_DURATION);
           Log.d("lhc-->","width:"+width+" height:"+height+ " duration:" + duration);
         mediaExtractor.selectTrack(videoTrackIndex);
           try {
               mediaCodec = MediaCodec.createDecoderByType(videoFormat.getString(MediaFormat.KEY_MIME));
               mediaCodec.configure(videoFormat,surface,null,0);
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
       if (mediaCodec == null){
           Log.d("lhc","mediacodec null");
           return;
       }
       mediaCodec.start();
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
        boolean isEOS = false;
        long startMs = System.currentTimeMillis();
        while (!Thread.interrupted()) {
            if (!isPlaying) {
                continue;
            }
            if (startTime == -1L){
                startTime = System.currentTimeMillis();
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
                    //outputBuffers = videoCodec.getOutputBuffers();
                    Log.v(TAG, "output buffers changed");
                    break;
                default:
                    //直接渲染到Surface时使用不到outputBuffer
                    //ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
                    //延时操作
                    //如果缓冲区里的可展示时间>当前视频播放的进度，就休眠一下
                    // sleepRender(bufferInfo, startMs);
                    //渲染
                    if (  bufferInfo.presentationTimeUs/1000 > System.currentTimeMillis() - startTime ){
                        try {
                            TimeUnit.MICROSECONDS.sleep(bufferInfo.presentationTimeUs - (System.currentTimeMillis() - startTime)*1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
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
}
