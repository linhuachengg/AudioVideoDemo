package com.topband.audiovideodemo.camera

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent

class GLCameraSurfaceView : GLSurfaceView {
    constructor(context: Context):super(context)
    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)
    private var renderer: GLCameraRender? = null

    fun init(mSftCb:(SurfaceTexture) -> Unit){
        setEGLContextClientVersion(2)

        renderer = GLCameraRender(context,mSftCb)

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer)
        renderMode = RENDERMODE_WHEN_DIRTY
    }

    fun render(){
        requestRender()
    }

    fun takePicture(){
        renderer?.takePic()
    }
}