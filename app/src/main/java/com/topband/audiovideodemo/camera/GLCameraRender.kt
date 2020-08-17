package com.topband.audiovideodemo.camera

import android.content.Context
import android.graphics.SurfaceTexture
import android.graphics.drawable.BitmapDrawable
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.SystemClock
import android.util.Log
import android.view.Surface
import androidx.core.graphics.drawable.toBitmap
import com.topband.audiovideodemo.R
import com.topband.audiovideodemo.video.AudioThread
import com.topband.audiovideodemo.video.VideoThread

class GLCameraRender(var context: Context,var mSftCb:(SurfaceTexture) -> Unit) : GLSurfaceView.Renderer {

    // vPMatrix is an abbreviation for "Model View Projection Matrix"
    private val vPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private lateinit var videoTexture: GLCameraTexture


    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        videoTexture = GLCameraTexture()
        videoTexture.setUnit(mSftCb)
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        glClear(GL_COLOR_BUFFER_BIT)
    }

    override fun onDrawFrame(unused: GL10) {
        glClear(GL_COLOR_BUFFER_BIT)
        val scratch = FloatArray(16)


        // Set the camera position (View matrix)
       // Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, -3.0f, 0f, 0f, 0f, 0f, -1.0f, 0.0f)

        // Calculate the projection and view transformation
       // Matrix.multiplyMM(scratch, 0, projectionMatrix, 0, viewMatrix, 0)

       // Matrix.multiplyMM(scratch, 0, vPMatrix, 0, rotationMatrix, 0)

        videoTexture.draw(scratch)

    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        glViewport(0, 0, width, height)
        val ratio: Float = width.toFloat() / height.toFloat()
        videoTexture.mFrameCallbackHeight = height
        videoTexture.mFrameCallbackWidth = width
        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
       //Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
        Matrix.orthoM (projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
       // Matrix.orthoM (projectionMatrix, 0,0f,width.toFloat(), height.toFloat(), 0f, -width.toFloat(), width.toFloat())
    }

    fun takePic(){
        videoTexture.take()
    }
}
