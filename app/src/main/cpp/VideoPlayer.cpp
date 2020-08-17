//
// Created by live on 2020/5/5.
//

#include <chrono>
#include "VideoPlayer.h"


int VideoPlayer::startPlay(VideoPlayer * videoPlayer) {
     int flag;
     pthread_t  playThread;
     flag = pthread_create(&playThread,NULL,VideoPlayer::realPlay,videoPlayer);
    if(flag)
    {
        __android_log_print(ANDROID_LOG_ERROR,TAG,"pthread 1 create error ");
        return flag;
    }
     void *arg;
    flag = pthread_join(playThread,&arg);
    if(flag)
    {
        __android_log_print(ANDROID_LOG_ERROR,TAG,"join error ");
        return flag;
    }

    return 0;
}

  void * VideoPlayer::realPlay(void* videoPlayer) {

    VideoPlayer * videoPlayer1 = (VideoPlayer *)(videoPlayer);
    __android_log_print(ANDROID_LOG_ERROR,TAG, "open file:%s\n", videoPlayer1->filePath);
    //注册所有组件
      std::chrono::time_point<std::chrono::system_clock, std::chrono::milliseconds> tp = std::chrono::time_point_cast<std::chrono::milliseconds>(std::chrono::system_clock::now());
      auto tmp = std::chrono::duration_cast<std::chrono::milliseconds>(tp.time_since_epoch());
    videoPlayer1->startTime = static_cast<int>(tmp.count());
    av_register_all();
    //分配上下文
    AVFormatContext * pFormatCtx = avformat_alloc_context();
    int  error_code;
    char buf[1024];
    //打开视频文件
    if((error_code =avformat_open_input(&pFormatCtx,  videoPlayer1->filePath, NULL, NULL))!=0) {
        av_strerror(error_code, buf, 1024);
        __android_log_print(ANDROID_LOG_ERROR,TAG,"Couldn't open file %s: %d(%s)",  videoPlayer1->filePath, error_code, buf);
        return 0;
    }
    //检索多媒体流信息
    if(avformat_find_stream_info(pFormatCtx, NULL)<0) {
        __android_log_print(ANDROID_LOG_ERROR,TAG, "Couldn't find stream information.");

    }
    //寻找视频流de轨道
    int videoStream = -1, i;
    for (i = 0; i < pFormatCtx->nb_streams; i++) {
        if (pFormatCtx->streams[i]->codec->codec_type == AVMEDIA_TYPE_VIDEO
            && videoStream < 0) {
            videoStream = i;
            break;
        }
    }
    if(videoStream==-1) {
        __android_log_print(ANDROID_LOG_ERROR,TAG, "couldn't find a video stream.");
        return 0;
    }

    //获取视频总时长
    if (pFormatCtx->duration != AV_NOPTS_VALUE) {
        videoPlayer1->duration  =  (pFormatCtx->duration / AV_TIME_BASE);
        __android_log_print(ANDROID_LOG_ERROR,TAG, "duration==%ld",  videoPlayer1->duration);
    }

    //获取codec上下文指针
     videoPlayer1->pCodecCtx = pFormatCtx->streams[videoStream]->codec;
    //寻找视频流的解码器
     videoPlayer1->pCodec = avcodec_find_decoder( videoPlayer1->pCodecCtx->codec_id);
    if( videoPlayer1->pCodec==NULL) {
        __android_log_print(ANDROID_LOG_ERROR,TAG, "couldn't find Codec.");
        return 0;
    }
    if(avcodec_open2( videoPlayer1->pCodecCtx,  videoPlayer1->pCodec, NULL) < 0) {
        __android_log_print(ANDROID_LOG_ERROR,TAG, "Couldn't open codec.");
        return 0;
    }

    // 获取视频宽高
    int videoWidth =  videoPlayer1->pCodecCtx->width;
    int videoHeight =  videoPlayer1->pCodecCtx->height;
    // 设置native window的buffer大小,可自动拉伸
     videoPlayer1->nativeWindow = ANativeWindow_fromSurface( videoPlayer1->env,  videoPlayer1->surface);
    ANativeWindow_setBuffersGeometry( videoPlayer1->nativeWindow,   videoPlayer1->pCodecCtx->width,  videoPlayer1->pCodecCtx->height, WINDOW_FORMAT_RGBA_8888);

    ANativeWindow_Buffer windowBuffer;
    if(avcodec_open2( videoPlayer1->pCodecCtx,  videoPlayer1->pCodec, NULL)<0) {
        __android_log_print(ANDROID_LOG_ERROR,TAG, "Couldn't open codec.");
        return 0;
    }
    //申请内存
    AVFrame * pFrame = av_frame_alloc();
    AVFrame * pFrameRGBA = av_frame_alloc();
    if(pFrameRGBA == NULL || pFrame == NULL) {
        __android_log_print(ANDROID_LOG_ERROR,TAG, "Couldn't allocate video frame.");
        return 0;
    }
    // buffer中数据用于渲染,且格式为RGBA
    int numBytes=av_image_get_buffer_size(AV_PIX_FMT_RGBA,  videoPlayer1->pCodecCtx->width,  videoPlayer1->pCodecCtx->height, 1);

    uint8_t * buffer=(uint8_t *)av_malloc(numBytes*sizeof(uint8_t));
    av_image_fill_arrays(pFrameRGBA->data, pFrameRGBA->linesize, buffer, AV_PIX_FMT_RGBA,
                         videoPlayer1->pCodecCtx->width,  videoPlayer1->pCodecCtx->height, 1);

    // 由于解码出来的帧格式不是RGBA的,在渲染之前需要进行格式转换
    struct SwsContext *sws_ctx = sws_getContext( videoPlayer1->pCodecCtx->width,
                                                 videoPlayer1->pCodecCtx->height,
                                                 videoPlayer1->pCodecCtx->pix_fmt,
                                                 videoPlayer1->pCodecCtx->width,
                                                 videoPlayer1->pCodecCtx->height,
                                                AV_PIX_FMT_RGBA,
                                                SWS_BILINEAR,
                                                NULL,
                                                NULL,
                                                NULL);

    int frameFinished;
    AVPacket packet;

    while(av_read_frame(pFormatCtx, &packet)>=0) {
        pthread_mutex_lock(&videoPlayer1->qlock);
        while(videoPlayer1->requestPause){
            pthread_cond_wait(&videoPlayer1->qready,&videoPlayer1->qlock);
        }
        pthread_mutex_unlock(&videoPlayer1->qlock);
        //判断是否为视频流
        if(packet.stream_index==videoStream) {
            //对该帧进行解码
            AVStream *stream=pFormatCtx->streams[packet.stream_index];
            avcodec_decode_video2( videoPlayer1->pCodecCtx, pFrame, &frameFinished, &packet);
            __android_log_print(ANDROID_LOG_ERROR, TAG, "pts %d", (int)(pFrame->pts*av_q2d(stream->time_base)));
            std::chrono::time_point<std::chrono::system_clock, std::chrono::milliseconds> tp2 = std::chrono::time_point_cast<std::chrono::milliseconds>(std::chrono::system_clock::now());
            auto tmp2 = std::chrono::duration_cast<std::chrono::milliseconds>(tp.time_since_epoch());
            __android_log_print(ANDROID_LOG_ERROR, TAG, "del %d", (int)(tmp2.count() - videoPlayer1->startTime));
            if (frameFinished) {
                // lock native window
                ANativeWindow_lock(videoPlayer1->nativeWindow, &windowBuffer, 0);
                // 格式转换
                sws_scale(sws_ctx, (uint8_t const * const *)pFrame->data,
                          pFrame->linesize, 0,  videoPlayer1->pCodecCtx->height,
                          pFrameRGBA->data, pFrameRGBA->linesize);
                // 获取stride
                uint8_t * dst = static_cast<uint8_t *>(windowBuffer.bits);
                int dstStride = windowBuffer.stride * 4;
                uint8_t * src = pFrameRGBA->data[0];
                int srcStride = pFrameRGBA->linesize[0];
                // 由于window的stride和帧的stride不同,因此需要逐行复制
                int h;
                for (h = 0; h < videoHeight; h++) {
                    memcpy(dst + h * dstStride, src + h * srcStride, (size_t) srcStride);
                }
                ANativeWindow_unlockAndPost(videoPlayer1->nativeWindow);
            }
            //延迟等待


            // usleep((unsigned long) (1000 * 40 * play_rate));
        }
        av_packet_unref(&packet);
    }
    //释放内存以及关闭文件
    av_free(buffer);
    av_free(pFrameRGBA);
    av_free(pFrame);
    avcodec_close( videoPlayer1->pCodecCtx);
    avformat_close_input(&pFormatCtx);
    return 0;
}

void VideoPlayer::stopPlay() {

}

void VideoPlayer::pause() {
     this->requestPause = true;
}



void VideoPlayer::seek(long time) {

}

VideoPlayer::VideoPlayer() {

}

void VideoPlayer::setFilepath(const char *filepath) {
  this->filePath = filepath;
}

void VideoPlayer::setEnv(JNIEnv *env) {
 this->env = env;
}

void VideoPlayer::setSurface(jobject surface) {
   this->surface = surface;
}



void VideoPlayer::canclePause() {
    this->requestPause = false;
    pthread_cond_signal(&qready);
}

void VideoPlayer::resetSurface(JNIEnv * env,jobject surface) {
    // 获取native window
    nativeWindow = ANativeWindow_fromSurface(env, surface);
    ANativeWindow_setBuffersGeometry(nativeWindow,  pCodecCtx->width, pCodecCtx->height, WINDOW_FORMAT_RGBA_8888);
    __android_log_print(ANDROID_LOG_ERROR,TAG,"reset Surface");
    this->canclePause();
}


