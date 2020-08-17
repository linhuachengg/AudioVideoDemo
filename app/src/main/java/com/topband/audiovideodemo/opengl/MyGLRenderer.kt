package com.topband.audiovideodemo.opengl

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.SystemClock
import android.util.Log
import androidx.core.graphics.drawable.toBitmap
import com.topband.audiovideodemo.R

class MyGLRenderer(var context: Context) : GLSurfaceView.Renderer {
    private lateinit var mTriangle: Triangle
    private lateinit var circle: Circle
    private lateinit var cube: Cube
    private lateinit var ball: Ball
    private lateinit var rect : Rect
    private lateinit var texture: Texture
    private lateinit var vbo: VBO

    // vPMatrix is an abbreviation for "Model View Projection Matrix"
    private val vPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val rotationMatrix = FloatArray(16)
    private  var start: Long = System.currentTimeMillis()
    @Volatile
    var angle: Float = 0f

    @Volatile
    var leftRight: Float = 0f

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
         // Set the background frame color
         //cube = Cube()
//         ball = Ball()
         mTriangle = Triangle()
//        vbo = VBO()
         // circle = Circle()
//        rect = Rect()

//        texture = Texture((context.resources.getDrawable(R.drawable.th)).toBitmap(),(context.resources.getDrawable(R.drawable.b)).toBitmap())
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        glClear(GL_COLOR_BUFFER_BIT)
//        glEnable(GL_DEPTH_TEST)
    }

    override fun onDrawFrame(unused: GL10) {
        glClear(GL_COLOR_BUFFER_BIT)
        val scratch = FloatArray(16)
        // Create a rotation transformation for the triangle
        val time = (System.currentTimeMillis() - start)
         angle = 0.090f * time.toInt()
        Log.d("lhc-->",""+ angle)
        Matrix.setRotateM(rotationMatrix, 0, angle, 0f, 0f, -1.0f)

        // Set the camera position (View matrix)
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, -3.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)

        // Calculate the projection and view transformation
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        Matrix.multiplyMM(scratch, 0, vPMatrix, 0, rotationMatrix, 0)

//        ball.draw(scratch)
       mTriangle.draw(scratch)
//        vbo.draw(scratch)
//        circle.draw(scratch)
//        rect.draw(scratch)
//        texture.draw(scratch)
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        glViewport(0, 0, width, height)
        val ratio: Float = width.toFloat() / height.toFloat()

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
    }

}
