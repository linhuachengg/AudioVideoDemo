package com.topband.audiovideodemo.opengl.video

import android.content.Context
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

class GLVideoRender(var context: Context) : GLSurfaceView.Renderer {

    // vPMatrix is an abbreviation for "Model View Projection Matrix"
    private val vPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val rotationMatrix = FloatArray(16)

    private lateinit var videoTexture: GLVideoTexture


    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {

        videoTexture = GLVideoTexture()
        videoTexture.getSurfaceTexture {
            VideoThread("http://vfx.mtime.cn/Video/2019/03/18/mp4/190318231014076505.mp4",
                Surface(it)
            ).start()
            AudioThread("http://vfx.mtime.cn/Video/2019/03/18/mp4/190318231014076505.mp4").start()
        }
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        glClear(GL_COLOR_BUFFER_BIT)

    }

    override fun onDrawFrame(unused: GL10) {
        glClear(GL_COLOR_BUFFER_BIT)
        val scratch = FloatArray(16)
        Matrix.setRotateM(rotationMatrix, 0, 0f, 0f, 0f, -1.0f)

        // Set the camera position (View matrix)
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, -3.0f, 0f, 0f, 0f, 0f, -1.0f, 0.0f)

        // Calculate the projection and view transformation
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        Matrix.multiplyMM(scratch, 0, vPMatrix, 0, rotationMatrix, 0)

        videoTexture.draw(scratch)

    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        glViewport(0, 0, width, height)
        val ratio: Float = width.toFloat() / height.toFloat()
        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
    }

}
