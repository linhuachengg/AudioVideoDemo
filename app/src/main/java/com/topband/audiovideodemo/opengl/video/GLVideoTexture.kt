package com.topband.audiovideodemo.opengl.video

import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.GLUtils
import android.util.Log
import com.topband.audiovideodemo.opengl.RenderUtil
import java.nio.FloatBuffer

class GLVideoTexture {

    private var mSurfaceTexture: SurfaceTexture? = null
    private var mSftCb: ((SurfaceTexture) -> Unit)? = null
    private var isSet = false
    private val vertexShaderCode =
    // This matrix member variable provides a hook to manipulate
        // the coordinates of the objects that use this vertex shader
                "uniform mat4 uMVPMatrix;" +
                "attribute vec4 vPosition;" +
                "varying  vec4 vColor;"+
                "attribute vec4 aColor;"+
                "attribute vec2 vCoordinate;"+
                "varying vec2 aCoordinate;"+
                "void main() {" +
                "gl_Position = uMVPMatrix * vPosition;" +
                "vColor = aColor;"+
                "aCoordinate=vCoordinate;"+
                "}"

    private val fragmentShaderCode =
        "#extension GL_OES_EGL_image_external : require\n"+
        "precision mediump float;" +
                "varying vec4 vColor;" +
                "uniform samplerExternalOES vTexture;"+
                "varying vec2 aCoordinate;"+
                "void main() {" +
                "  gl_FragColor = texture2D(vTexture,aCoordinate);" +
                "}"

    var rectCoords = floatArrayOf(     // in counterclockwise order:
        -0.5f,  0.5f,  0.0f,      // left top
        -0.5f, -0.5f,  0.0f,    // bottom left
        0.5f,  -0.5f,  0.0f,      // bottom right
        0.5f,  0.5f,   0.0f
    )
    var textureCoords = floatArrayOf(
        0.0f, 1.0f,
        0.0f, 0.0f,
        1.0f, 0.0f,
        1.0f, 1.0f
    )
    // Set color with red, green, blue and alpha (opacity) values
    val color = floatArrayOf(
        // 0.63671875f, 0.76953125f, 0.22265625f, 1.0f
        0.0f, 1.0f, 0.0f, 0.5f ,
        1.0f, 0.0f, 0.0f, 0.5f,
        0.0f, 1.0f, 0.0f, 0.5f ,
        0.0f, 0.0f, 1.0f, 0.5f
    )
    private var mProgram: Int
    private var vertexBuffer: FloatBuffer = RenderUtil.floatArrayToFloatBuffer(rectCoords)
    private var colorBuffer: FloatBuffer = RenderUtil.floatArrayToFloatBuffer(color)
    private var textureBuffer: FloatBuffer = RenderUtil.floatArrayToFloatBuffer(textureCoords)

    private var positionHandle: Int = 0
    private var mColorHandle: Int = 0
    private var vPMatrixHandle: Int = 0
    private var textureHandle: Int = 0
    private var textureid =  IntArray(1)

    private var sampleHandle:Int = 0


    val COORDS_PER_VERTEX = 3
    private val vertexCount: Int = rectCoords.size / COORDS_PER_VERTEX
    private val vertexStride: Int = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    init {
        val vertexShader: Int = RenderUtil.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = RenderUtil.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        Log.d("lhc -->","compile info vertexShader:${GLES20.glGetShaderInfoLog(vertexShader)}")
        Log.d("lhc -->","compile info fragmentShader:${GLES20.glGetShaderInfoLog(fragmentShader)}")
        mProgram = RenderUtil.craeteLinkProgram(vertexShader, fragmentShader)
        // get handle to vertex shader's vPosition member
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition")

        // get handle to shape's transformation matrix
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix")

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor")

        textureHandle = GLES20.glGetAttribLocation(mProgram, "vCoordinate")

        sampleHandle = GLES20.glGetUniformLocation(mProgram, "vTexture")


        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(positionHandle)

        GLES20.glEnableVertexAttribArray(mColorHandle)

        GLES20.glEnableVertexAttribArray(textureHandle)
        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
            positionHandle,
            COORDS_PER_VERTEX,
            GLES20.GL_FLOAT,
            false,
            vertexStride,
            vertexBuffer
        )

        GLES20.glVertexAttribPointer(
            mColorHandle,
            4,
            GLES20.GL_FLOAT,
            false,
            4 * 4,
            colorBuffer
        )

        GLES20.glVertexAttribPointer(
            textureHandle,
            2,
            GLES20.GL_FLOAT,
            false,
            2 * 4,
            textureBuffer
        )

        GLES20.glGenTextures(1, textureid, 0)
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureid[0])
        // 为当前绑定的纹理对象设置环绕、过滤方式
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glUseProgram(mProgram)

        GLES20.glUniform1i(sampleHandle, 0)

        // Disable vertex array
        //glDisableVertexAttribArray(positionHandle)

        // Disable vertex array
        //glDisableVertexAttribArray(mColorHandle)
    }

    fun draw(mvpMatrix: FloatArray) {
        // Add program to OpenGL ES environment
        if (!isSet){
            setTextureID(textureid[0])
            isSet = true
        }
        mSurfaceTexture?.updateTexImage()
        GLES20.glUseProgram(mProgram)
        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0)
        // Draw the triangle
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureid[0])
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vertexCount)

    }

     fun setTextureID(id: Int) {
         mSurfaceTexture = SurfaceTexture(id)
         mSftCb?.invoke(mSurfaceTexture!!)
         mSurfaceTexture?.setOnFrameAvailableListener {

         }
    }

     fun getSurfaceTexture(cb: (st: SurfaceTexture) -> Unit) {
        mSftCb = cb
    }
}