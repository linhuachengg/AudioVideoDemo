package com.topband.audiovideodemo.opengl


import android.graphics.Bitmap
import android.opengl.GLES20.*
import android.opengl.GLES20.glGenTextures
import android.opengl.GLUtils
import android.util.Log
import java.nio.FloatBuffer

class Texture(private var bitmap: Bitmap,private var bitmap2: Bitmap) {
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
                "precision mediump float;" +
                "varying vec4 vColor;" +
                "uniform sampler2D vTexture;\n" +
                "uniform sampler2D texture2;"+
                "varying vec2 aCoordinate;"+
                "void main() {" +
                "gl_FragColor = mix(texture2D(vTexture,aCoordinate),texture2D(texture2,aCoordinate),0.2);" +
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
        0.0f, 1.0f, 0.0f, 0.5f,
        1.0f, 0.0f, 0.0f, 0.5f,
        0.0f, 1.0f, 0.0f, 0.5f,
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
    private var textureid2 =  IntArray(1)

    private var sampleHandle:Int = 0
    private var sampleHandle2:Int = 0

    val COORDS_PER_VERTEX = 3
    private val vertexCount: Int = rectCoords.size / COORDS_PER_VERTEX
    private val vertexStride: Int = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    init {
        val vertexShader: Int = RenderUtil.loadShader(GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = RenderUtil.loadShader(GL_FRAGMENT_SHADER, fragmentShaderCode)
        Log.d("lhc -->","compile info vertexShader:${glGetShaderInfoLog(vertexShader)}")
        Log.d("lhc -->","compile info fragmentShader:${glGetShaderInfoLog(fragmentShader)}")
        mProgram = RenderUtil.craeteLinkProgram(vertexShader, fragmentShader)
        // get handle to vertex shader's vPosition member
        positionHandle = glGetAttribLocation(mProgram, "vPosition")

        // get handle to shape's transformation matrix
        vPMatrixHandle = glGetUniformLocation(mProgram, "uMVPMatrix")

        // get handle to fragment shader's vColor member
        mColorHandle = glGetAttribLocation(mProgram, "aColor")

        textureHandle = glGetAttribLocation(mProgram, "vCoordinate")

        sampleHandle =  glGetUniformLocation(mProgram,"vTexture")

        sampleHandle2 = glGetUniformLocation(mProgram,"texture2")

        // Enable a handle to the triangle vertices
        glEnableVertexAttribArray(positionHandle)

        glEnableVertexAttribArray(mColorHandle)

        glEnableVertexAttribArray(textureHandle)
        // Prepare the triangle coordinate data
        glVertexAttribPointer(
            positionHandle,
            COORDS_PER_VERTEX,
            GL_FLOAT,
            false,
            vertexStride,
            vertexBuffer
        )

        glVertexAttribPointer(
            mColorHandle,
            4,
            GL_FLOAT,
            false,
            4 * 4,
            colorBuffer
        )

        glVertexAttribPointer(
            textureHandle,
            2,
            GL_FLOAT,
            false,
            2*4,
            textureBuffer
        )

        glGenTextures(1,textureid,0)
        glBindTexture(GL_TEXTURE_2D, textureid[0])

        // 为当前绑定的纹理对象设置环绕、过滤方式
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)

        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0)
        bitmap.recycle()

        glGenTextures(1,textureid2,0)
        glBindTexture(GL_TEXTURE_2D, textureid2[0])

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)

        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap2, 0)

        bitmap2.recycle()

        glUseProgram(mProgram)

        glUniform1i(sampleHandle, 0)

        glUniform1i(sampleHandle2, 1)

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
        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, textureid[0])
        glActiveTexture(GL_TEXTURE1)
        glBindTexture(GL_TEXTURE_2D, textureid2[0])

        glDrawArrays(GL_TRIANGLE_FAN, 0, vertexCount)

    }
}