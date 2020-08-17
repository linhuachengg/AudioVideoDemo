#include <jni.h>
#include <string>
#include "VideoPlayer.h"
#ifdef __cplusplus
extern "C" {
#endif
    #include "libavcodec/avcodec.h"
#ifdef __cplusplus
 }
#endif
VideoPlayer* videoPlayer;

extern "C"
JNIEXPORT void JNICALL
Java_com_topband_audiovideodemo_ffmpeg_NativeLib_play(JNIEnv *env, jclass clazz, jstring file,
                                                      jobject surface) {
     videoPlayer = new  VideoPlayer();
     videoPlayer->setEnv(env);
     videoPlayer->setSurface(surface);
     videoPlayer->setFilepath( (env)->GetStringUTFChars(file, JNI_FALSE));
     videoPlayer->startPlay(videoPlayer);

}
extern "C"
JNIEXPORT void JNICALL
Java_com_topband_audiovideodemo_ffmpeg_NativeLib_pause(JNIEnv *env, jclass clazz) {
     videoPlayer->pause();
}
extern "C"
JNIEXPORT void JNICALL
Java_com_topband_audiovideodemo_ffmpeg_NativeLib_cancelPause(JNIEnv *env, jclass clazz) {
    videoPlayer->canclePause();
}extern "C"
JNIEXPORT void JNICALL
Java_com_topband_audiovideodemo_ffmpeg_NativeLib_resetSurface(JNIEnv *env, jclass clazz,
                                                              jobject surface) {
    videoPlayer->resetSurface(env,surface);
}