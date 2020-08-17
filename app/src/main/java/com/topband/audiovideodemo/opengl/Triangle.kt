package com.topband.audiovideodemo.opengl

import android.opengl.GLES20.*
import android.util.Log
import com.topband.audiovideodemo.opengl.RenderUtil.Companion.craeteLinkProgram
import com.topband.audiovideodemo.opengl.RenderUtil.Companion.floatArrayToFloatBuffer
import com.topband.audiovideodemo.opengl.RenderUtil.Companion.loadShader
import java.nio.FloatBuffer


class Triangle {
    // number of coordinates per vertex in this array

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

    var triangleCoords = floatArrayOf(     // in counterclockwise order:
        0.0f, 0.5f, 0.0f,      // top
        -0.4f, -0.3f, 0.0f,    // bottom left
        0.4f, -0.3f, 0.0f      // bottom right
    )
    // Set color with red, green, blue and alpha (opacity) values
    val color = floatArrayOf(
        // 0.63671875f, 0.76953125f, 0.22265625f, 1.0f
        0.0f, 1.0f, 0.0f, 1.0f ,
        1.0f, 0.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 1.0f, 1.0f
    )
    private var mProgram: Int
    private var vertexBuffer: FloatBuffer = floatArrayToFloatBuffer(triangleCoords)
    private var colorBuffer: FloatBuffer =  floatArrayToFloatBuffer(color)
    private var positionHandle: Int = 0
    private var mColorHandle: Int = 0
    private var vPMatrixHandle: Int = 0
    private var intArray = IntArray(1)
    private var intArray2 = IntArray(1)

    val COORDS_PER_VERTEX = 3
    private val vertexCount: Int = triangleCoords.size / COORDS_PER_VERTEX
    private val vertexStride: Int = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    init {
        val vertexShader: Int = loadShader(GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = loadShader(GL_FRAGMENT_SHADER, fragmentShaderCode)
        Log.d("lhc -->","compile info vertexShader:${glGetShaderInfoLog(vertexShader)}")
        Log.d("lhc -->","compile info fragmentShader:${glGetShaderInfoLog(fragmentShader)}")
        mProgram = craeteLinkProgram(vertexShader,fragmentShader)

        glGenBuffers(1,intArray,0)
        glBindBuffer(GL_ARRAY_BUFFER, intArray[0])
        glBufferData(GL_ARRAY_BUFFER, triangleCoords.size*4, vertexBuffer, GL_STATIC_DRAW)

        // get handle to vertex shader's vPosition member
        positionHandle = glGetAttribLocation(mProgram, "vPosition")

        // get handle to shape's transformation matrix
        vPMatrixHandle = glGetUniformLocation(mProgram, "uMVPMatrix")

        // get handle to fragment shader's vColor member
        mColorHandle = glGetAttribLocation(mProgram, "aColor")

        // Enable a handle to the triangle vertices
        glEnableVertexAttribArray(positionHandle)

        glEnableVertexAttribArray(mColorHandle)
        // Prepare the triangle coordinate data
        glVertexAttribPointer(
            positionHandle,
            COORDS_PER_VERTEX,
            GL_FLOAT,
            false,
            vertexStride,
            0
        )

        glGenBuffers(1,intArray2,0)
        glBindBuffer(GL_ARRAY_BUFFER, intArray2[0])
        glBufferData(GL_ARRAY_BUFFER, color.size*4, colorBuffer, GL_STATIC_DRAW)

        glVertexAttribPointer(
            mColorHandle,
            3,
            GL_FLOAT,
            false,
            4*4,
            0
        )

        // Disable vertex array
        //glDisableVertexAttribArray(positionHandle)

        // Disable vertex array
        //glDisableVertexAttribArray(mColorHandle)
    }

    fun draw(mvpMatrix: FloatArray) {
        // Add program to OpenGL ES environment
        glUseProgram(mProgram)

        // Pass the projection and view transformation to the shader
        glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0)
        // Draw the triangle

        glDrawArrays(GL_TRIANGLES, 0, vertexCount)

    }

}
