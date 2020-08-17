package com.topband.audiovideodemo.opengl

import android.opengl.GLES20
import android.util.Log
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.opengles.GL
import java.nio.file.Files.size



class Ball {
    private val vertexShaderCode =
    // This matrix member variable provides a hook to manipulate
        // the coordinates of the objects that use this vertex shader
        "uniform mat4 uMVPMatrix;" +
                "attribute vec4 vPosition;" +
                "varying  vec4 vColor;" +
                "void main() {" +
                "gl_Position = uMVPMatrix * vPosition;" +
                "float color;"+
                "if(vPosition.z>0.0){" +
                    "color=vPosition.z;" +
                "}else{" +
                    "color=-vPosition.z;" +
                "}"+
                "vColor = vec4(color,color,color,1.0);"+
                "}"

    private val fragmentShaderCode =
        "precision mediump float;" +
                "varying vec4 vColor;" +
                "void main() {" +
                "  gl_FragColor = vColor;" +
                "}"

    var cubeCoords = createPositions(1)
    // Set color with red, green, blue and alpha (opacity) values

    private var mProgram: Int
    private var vertexBuffer: FloatBuffer = RenderUtil.floatArrayToFloatBuffer(cubeCoords)

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
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,cubeCoords.size/3)
    }

    private  fun createPositions(step:Int):FloatArray{
        val data = ArrayList<Float>()
        var r1: Float
        var r2: Float
        var h1: Float
        var h2: Float
        var sin: Float
        var cos: Float
        run {
            var i = -90f
            while (i < 90 + step) {
                r1 = Math.cos(i * Math.PI / 180.0).toFloat()
                r2 = Math.cos((i + step) * Math.PI / 180.0).toFloat()
                h1 = Math.sin(i * Math.PI / 180.0).toFloat()
                h2 = Math.sin((i + step) * Math.PI / 180.0).toFloat()
                // 固定纬度, 360 度旋转遍历一条纬线
                val step2 = step * 2
                var j = 0.0f
                while (j < 360.0f + step) {
                    cos = Math.cos(j * Math.PI / 180.0).toFloat()
                    sin = -Math.sin(j * Math.PI / 180.0).toFloat()

                    data.add(r2 * cos)
                    data.add(h2)
                    data.add(r2 * sin)
                    data.add(r1 * cos)
                    data.add(h1)
                    data.add(r1 * sin)
                    j += step2
                }
                i += step
            }
        }
        val f = FloatArray(data.size)
        for (i in f.indices) {
            f[i] = data.get(i)
        }
        Log.d("lhc-->",""+f.size)
       return  f
    }
}