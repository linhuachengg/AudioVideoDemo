package com.topband.audiovideodemo.opengl

import android.opengl.GLES30.*
import android.util.Log
import com.topband.audiovideodemo.opengl.RenderUtil.Companion.craeteLinkProgram
import com.topband.audiovideodemo.opengl.RenderUtil.Companion.floatArrayToFloatBuffer
import com.topband.audiovideodemo.opengl.RenderUtil.Companion.loadShader
import java.nio.FloatBuffer

class VBO {

    private val vertexShaderCode =
    // This matrix member variable provides a hook to manipulate
        // the coordinates of the objects that use this vertex shader
        "uniform mat4 uMVPMatrix;" +
                "attribute vec4 vPosition;" +
                "void main() {" +
                "  gl_Position = uMVPMatrix * vPosition;" +
                "}"

    private val fragmentShaderCode =
        "precision mediump float;" +
                "void main() {" +
                "  gl_FragColor = vec4(1.0f, 0.0f, 0.0f, 0.0f);" +
                "}"

    var triangleCoords = floatArrayOf(     // in counterclockwise order:
        0.0f, 0.5f, 0.0f,      // top
        -0.4f, -0.3f, 0.0f,    // bottom left
        0.4f, -0.3f, 0.0f      // bottom right
    )

    private var mProgram: Int
    private var vertexBuffer: FloatBuffer = floatArrayToFloatBuffer(triangleCoords)
    private var positionHandle: Int = 0
    private var vPMatrixHandle: Int = 0
    private var intArray = IntArray(1)

    val COORDS_PER_VERTEX = 3
    private val vertexCount: Int = triangleCoords.size / COORDS_PER_VERTEX
    private val vertexStride: Int = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    init {
        val vertexShader: Int = loadShader(GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = loadShader(GL_FRAGMENT_SHADER, fragmentShaderCode)
        Log.d("lhc -->","compile info vertexShader:${glGetShaderInfoLog(vertexShader)}")
        Log.d("lhc -->","compile info fragmentShader:${glGetShaderInfoLog(fragmentShader)}")
        mProgram = craeteLinkProgram(vertexShader,fragmentShader)

        // get handle to vertex shader's vPosition member
        positionHandle = glGetAttribLocation(mProgram, "vPosition")

        // get handle to shape's transformation matrix
        vPMatrixHandle = glGetUniformLocation(mProgram, "uMVPMatrix")

        glGenBuffers(1,intArray,0)
        glBindBuffer(GL_ARRAY_BUFFER, intArray[0])
        glBufferData(GL_ARRAY_BUFFER,triangleCoords.size*4, vertexBuffer, GL_STATIC_DRAW)

        // Prepare the triangle coordinate data
        glVertexAttribPointer(
            positionHandle,
            COORDS_PER_VERTEX,
            GL_FLOAT,
            false,
            vertexStride,
            0
        )
    }

    fun draw(mvpMatrix: FloatArray) {
        // Add program to OpenGL ES environment
        glUseProgram(mProgram)


        glEnableVertexAttribArray(positionHandle)
        // Pass the projection and view transformation to the shader
        glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0)
        // Draw the triangle

        glDrawArrays(GL_TRIANGLES, 0, vertexCount)

    }
}