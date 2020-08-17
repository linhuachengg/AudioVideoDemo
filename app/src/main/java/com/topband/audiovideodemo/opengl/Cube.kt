package com.topband.audiovideodemo.opengl

import android.opengl.GLES20
import android.util.Log
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class Cube {

    private val vertexShaderCode =
    // This matrix member variable provides a hook to manipulate
        // the coordinates of the objects that use this vertex shader
        "uniform mat4 uMVPMatrix;" +
                "attribute vec4 vPosition;" +
                "varying  vec4 vColor;"+
                "attribute vec4 aColor;"+
                "void main() {" +
                "  gl_Position = uMVPMatrix * vPosition;" +
                "vColor = aColor;"+
                "}"

    private val fragmentShaderCode =
        "precision mediump float;" +
                "varying vec4 vColor;" +
                "void main() {" +
                "  gl_FragColor = vColor;" +
                "}"

    var cubeCoords = floatArrayOf(     // in counterclockwise order:
        -1.0f,1.0f,1.0f,    //正面左上0
        -1.0f,-1.0f,1.0f,   //正面左下1
        1.0f,-1.0f,1.0f,    //正面右下2
        1.0f,1.0f,1.0f,     //正面右上3
        -1.0f,1.0f,-1.0f,    //反面左上4
        -1.0f,-1.0f,-1.0f,   //反面左下5
        1.0f,-1.0f,-1.0f,    //反面右下6
        1.0f,1.0f,-1.0f     //反面右上7
    )
    // Set color with red, green, blue and alpha (opacity) values
    val color = floatArrayOf(
        0f,1f,0f,1f,
        0f,1f,0f,1f,
        0f,1f,0f,1f,
        0f,1f,0f,1f,
        1f,0f,0f,1f,
        1f,0f,0f,1f,
        1f,0f,0f,1f,
        1f,0f,0f,1f
    )
    var  index:ShortArray = shortArrayOf(
        6,7,4,6,4,5,    //后面
        6,3,7,6,2,3,    //右面
        6,5,1,6,1,2,    //下面
        0,3,2,0,2,1,    //正面
        0,1,5,0,5,4,    //左面
        0,7,3,0,4,7   //上面
    )


    private var mProgram: Int
    private var vertexBuffer: FloatBuffer = RenderUtil.floatArrayToFloatBuffer(cubeCoords)
    private var colorBuffer: FloatBuffer =  RenderUtil.floatArrayToFloatBuffer(color)
    private var indexBuffer : ShortBuffer = RenderUtil.shortArrayToShortBuffer(index)
    private var positionHandle: Int = 0
    private var mColorHandle: Int = 0
    private var vPMatrixHandle: Int = 0
    val COORDS_PER_VERTEX = 3
    private val vertexCount: Int = cubeCoords.size / COORDS_PER_VERTEX
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

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(positionHandle)

        GLES20.glEnableVertexAttribArray(mColorHandle)
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

        // Disable vertex array
        //glDisableVertexAttribArray(positionHandle)

        // Disable vertex array
        //glDisableVertexAttribArray(mColorHandle)
    }

    fun draw(mvpMatrix: FloatArray) {
        // Add program to OpenGL ES environment
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        GLES20.glUseProgram(mProgram)
        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0)
        // Draw the triangle
        GLES20.glDrawElements(GLES20.GL_TRIANGLES,index.size, GLES20.GL_UNSIGNED_SHORT,indexBuffer)

    }
}