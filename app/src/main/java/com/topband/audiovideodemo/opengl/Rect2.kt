package com.topband.audiovideodemo.opengl

import android.opengl.GLES30
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class Rect2 {


    private val vertexShaderCode =
                "#version 300 es\n"+
                "in vec4 vPosition;\n" +
                "void main() {\n" +
                // the matrix must be included as a modifier of gl_Position
                // Note that the uMVPMatrix factor *must be first* in order
                // for the matrix multiplication product to be correct.
                "  gl_Position = vPosition;\n" +
                "}"
    private val fragmentShaderCode =
        "#version 300 es\n"+
        "precision mediump float;\n" +
                "uniform vec4 vColor;\n" +
                "out vec4 FragColor;\n"+
                "void main() {\n" +
                "FragColor = vColor;\n" +
                "}"

    private var rectCoords = floatArrayOf(     // in counterclockwise order:
        0.5f, 0.5f, 0.0f,   // 右上角
        0.5f, -0.5f, 0.0f,  // 右下角
        -0.5f, -0.5f, 0.0f, // 左下角
        -0.5f, 0.5f, 0.0f   // 左上角
    )
    val color = floatArrayOf(0.63671875f, 0.76953125f, 0.22265625f, 1.0f)

    private var vertexBuffer: FloatBuffer =
        // (number of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(rectCoords.size * 4).run {
            // use the device hardware's native byte order
            order(ByteOrder.nativeOrder())

            // create a floating point buffer from the ByteBuffer
            asFloatBuffer().apply {
                // add the coordinates to the FloatBuffer
                put(rectCoords)
                // set the buffer to read the first coordinate
                position(0)
            }
        }

    private var drawList = shortArrayOf(
        0, 1, 3, // 第一个三角形
        1, 2, 3  // 第二个三角形
    )

    private var drawListBuffer : ShortBuffer =

        ByteBuffer.allocateDirect(drawList.size*2).run {
            order(ByteOrder.nativeOrder())
            asShortBuffer().apply {
                // add the coordinates to the FloatBuffer
                put(drawList)
                // set the buffer to read the first coordinate
                position(0)
            }
        }

    private var mProgram: Int
    private var VBO = IntArray(1)
    private var VAO = IntArray(1)
    private var EBO = IntArray(1)

    private var positionHandle:Int = 0
    private var mColorHandle = 0

    init {
        val vertexShader: Int = RenderUtil.loadShader30(GLES30.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = RenderUtil.loadShader30(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode)
        Log.d("lhc -->", "compile info${GLES30.glGetShaderInfoLog(vertexShader)}")
        Log.d("lhc -->", "compile info${GLES30.glGetShaderInfoLog(fragmentShader)}")

        // create empty OpenGL ES Program
        mProgram = GLES30.glCreateProgram().also {

            // add the vertex shader to program
            GLES30.glAttachShader(it, vertexShader)

            // add the fragment shader to program
            GLES30.glAttachShader(it, fragmentShader)

            // creates OpenGL ES program executables
            GLES30.glLinkProgram(it)
        }

//        GLES30.glGenVertexArrays(1, VAO, 0)
//        GLES30.glGenBuffers(1, VBO, 0)
//        GLES30.glGenBuffers(1, EBO, 0)
//
//        GLES30.glBindVertexArray(VAO[0])
//
//        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, VBO[0])
//        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, rectCoords.size * 4, vertexBuffer, GLES30.GL_STATIC_DRAW)
//
//        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, EBO[0])
//        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER, drawList.size * 2, drawListBuffer, GLES30.GL_STATIC_DRAW)


        // get handle to vertex shader's vPosition member
        positionHandle = GLES30.glGetAttribLocation(mProgram, "vPosition")

        // get handle to fragment shader's vColor member
        mColorHandle = GLES30.glGetUniformLocation(mProgram, "vColor")

        // Prepare the triangle coordinate data
        GLES30.glVertexAttribPointer(
            positionHandle,
            3,
            GLES30.GL_FLOAT,
            false,
            3 * 4,
            vertexBuffer
        )
        GLES30.glUniform4fv(mColorHandle, 1, color, 0)
        // Enable a handle to the triangle vertices
        GLES30.glEnableVertexAttribArray(positionHandle)
//        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0)
//        GLES30.glBindVertexArray(0)
    }



    fun draw() {
        GLES30.glUseProgram(mProgram)
        // Draw the triangle
        GLES30.glDrawElements( GLES30.GL_TRIANGLES, 6,  GLES30.GL_UNSIGNED_INT, 0)
        // Disable vertex array
        GLES30.glDisableVertexAttribArray(positionHandle)
    }
}