//
// Created by live on 2020/5/5.
//
#include <jni.h>
#include <string>

#ifndef AUDIOVIDEODEMO_VIDEOPLAYER_H
#define AUDIOVIDEODEMO_VIDEOPLAYER_H
#ifdef __cplusplus
 extern "C"{
#endif
#include "libavcodec/avcodec.h"
#include "libavformat/avformat.h"
#include "libswscale/swscale.h"
#include <android/native_window.h>
#include <android/native_window_jni.h>
#include <stdio.h>
#include <unistd.h>
#include <pthread.h>
#include <libavutil/imgutils.h>
#include <android/log.h>
#include <sys/time.h>
#ifdef __cplusplus
}
#endif

#define TAG "VideoPlayer"

class VideoPlayer {
private:
      int startTime = 0;
      int duration;
      pthread_cond_t qready=PTHREAD_COND_INITIALIZER;
      pthread_mutex_t qlock=PTHREAD_MUTEX_INITIALIZER;
      bool requestPause = false;
      const char* filePath;
      jobject surface;
      JNIEnv * env;
      ANativeWindow * nativeWindow;
      AVCodecContext * pCodecCtx;
      AVCodec * pCodec;

public:
    VideoPlayer();
    int startPlay(VideoPlayer *videoPlayer);
    static void * realPlay(void * videoPlayer);
    void stopPlay();
    void pause();
    void canclePause();
    void seek(long time);
    void setFilepath(const char * filepath);
    void setEnv(JNIEnv * env);
    void setSurface(jobject surface);
    void resetSurface(JNIEnv * env,jobject surface);

};


#endif //AUDIOVIDEODEMO_VIDEOPLAYER_H
