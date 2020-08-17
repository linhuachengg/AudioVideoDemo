package com.topband.audiovideodemo.opengl

import android.opengl.GLES20
import android.opengl.GLES30
import android.util.Log
import java.nio.*
import java.nio.charset.Charset


class RenderUtil {
    companion object {

        fun loadShader(type: Int, shaderCode: String): Int {

            // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
            // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
            return GLES20.glCreateShader(type).also { shader ->

                // add the source code to the shader and compile it
                GLES20.glShaderSource(shader, shaderCode)
                GLES20.glCompileShader(shader)

            }
        }




        fun loadShader30(type: Int, shaderCode: String): Int {

            // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
            // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
            return GLES30.glCreateShader(type).also { shader ->

                // add the source code to the shader and compile it
                GLES30.glShaderSource(shader, shaderCode)
                GLES30.glCompileShader(shader)
            }
        }

        fun floatArrayToFloatBuffer(floatArray:FloatArray):FloatBuffer{
            // (number of coordinate values * 4 bytes per float)
          return  ByteBuffer.allocateDirect(floatArray.size * 4).run {
                // use the device hardware's native byte order
                order(ByteOrder.nativeOrder())

                // create a floating point buffer from the ByteBuffer
                asFloatBuffer().apply {
                    // add the coordinates to the FloatBuffer
                    put(floatArray)
                    // set the buffer to read the first coordinate
                    position(0)
                }
            }
        }
        fun shortArrayToShortBuffer(shortArray:ShortArray):ShortBuffer{
            // (number of coordinate values * 4 bytes per float)
            return  ByteBuffer.allocateDirect(shortArray.size * 2).run {
                // use the device hardware's native byte order
                order(ByteOrder.nativeOrder())

                // create a floating point buffer from the ByteBuffer
                asShortBuffer().apply {
                    // add the coordinates to the FloatBuffer
                    put(shortArray)
                    // set the buffer to read the first coordinate
                    position(0)
                }
            }
        }

        fun craeteLinkProgram(vertexShader:Int,fragmentShader:Int):Int{
          return  GLES20.glCreateProgram().also {

                // add the vertex shader to program
                GLES20.glAttachShader(it, vertexShader)

                // add the fragment shader to program
                GLES20.glAttachShader(it, fragmentShader)

                // creates OpenGL ES program executables
                GLES20.glLinkProgram(it)
            }
        }
    }
}