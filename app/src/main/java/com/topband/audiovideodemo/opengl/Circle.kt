package com.topband.audiovideodemo.opengl

import android.opengl.GLES20.*
import android.util.Log
import java.nio.FloatBuffer

class Circle {
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
                "uniform vec4 vColor;" +
                "void main() {" +
                "  gl_FragColor = vColor;" +
                "}"

    var circleCpprds = createPositions(360)
    // Set color with red, green, blue and alpha (opacity) values
    val color = floatArrayOf(
         0.63671875f, 0.76953125f, 0.22265625f, 1.0f
    )
    private var mProgram: Int
    private var vertexBuffer: FloatBuffer = RenderUtil.floatArrayToFloatBuffer(circleCpprds)
    private var colorBuffer: FloatBuffer = RenderUtil.floatArrayToFloatBuffer(color)
    private var positionHandle: Int = 0
    private var mColorHandle: Int = 0
    private var vPMatrixHandle: Int = 0
    val COORDS_PER_VERTEX = 3
    private val vertexCount: Int = circleCpprds.size / COORDS_PER_VERTEX
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
        mColorHandle = glGetUniformLocation(mProgram, "vColor")

        // Enable a handle to the triangle vertices
        glEnableVertexAttribArray(positionHandle)

        // Prepare the triangle coordinate data
        glVertexAttribPointer(
            positionHandle,
            COORDS_PER_VERTEX,
            GL_FLOAT,
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
        glUseProgram(mProgram)

        glUniform4fv(mColorHandle,1,color,0)
        // Pass the projection and view transformation to the shader
        glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0)
        // Draw the triangle
        glDrawArrays(GL_POINTS, 0, circleCpprds.size/3)

    }
    private  fun createPositions(n:Int):FloatArray{
       val data = ArrayList<Float>()
        data.add(0.0f)           //设置圆心坐标
        data.add(0.0f)
        data.add(0.0f)
        for(i in 0..n){
            data.add( ((Math.cos(i.toFloat()*2.0f*Math.PI/n.toFloat()).toFloat())))
            data.add(((Math.sin(i.toFloat()*2.0f*Math.PI/n.toFloat()).toFloat())))
            data.add(0.0f)
        }
        val f = FloatArray(data.size)
        for (i in 0 until data.size){
            f[i] = 0.5f*data[i]
        }
        Log.d("lhc-->",""+data.size)
        return f
    }
}