package com.topband.audiovideodemo.opengl.video

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent

class GLVideoSurfaceView(context: Context) : GLSurfaceView(context) {
    private val renderer: GLVideoRender

    init {
        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2)

        renderer = GLVideoRender(context)

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer)
        renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
    }
    private  val TOUCH_SCALE_FACTOR: Float = 180.0f / 320f

    private var previousX: Float = 0f
    private var previousY: Float = 0f

    override fun onTouchEvent(e: MotionEvent): Boolean {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        val x: Float = e.x
        val y: Float = e.y

        when (e.action) {
            MotionEvent.ACTION_MOVE -> {
                requestRender()
            }
        }
        previousX = x
        previousY = y
        return true
    }
}