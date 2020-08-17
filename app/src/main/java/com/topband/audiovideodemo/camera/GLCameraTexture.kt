package com.topband.audiovideodemo.camera

import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.GLUtils
import android.util.Log
import com.topband.audiovideodemo.opengl.RenderUtil
import java.nio.Buffer
import java.nio.FloatBuffer
import android.R.array
import java.nio.IntBuffer
import android.graphics.Bitmap
import android.os.Environment
import android.os.Environment.getExternalStorageDirectory
import android.widget.Toast
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files.exists






class GLCameraTexture {

    private var mSurfaceTexture: SurfaceTexture? = null
    private var mSftCb: ((SurfaceTexture) -> Unit)? = null
    private var isSet = false
    private var isTake = false
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
                "gl_Position =   vPosition;" +
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
                "  gl_FragColor = texture2D(vTexture,aCoordinate)*vColor;" +
                "}"

    var rectCoords = floatArrayOf(     // in counterclockwise order:
        -1.0f,  1.0f,  0.0f,      // left top
        -1.0f, -1.0f,  0.0f,    // bottom left
        1.0f,  -1.0f,  0.0f,      // bottom right
        1.0f,  1.0f,   0.0f
    )
    var textureCoords = floatArrayOf(
        0.0f,1.0f,
        1.0f,1.0f,
        1.0f,0.0f,
        0.0f,0.0f
    )
    // Set color with red, green, blue and alpha (opacity) values
    val color = floatArrayOf(
        // 0.63671875f, 0.76953125f, 0.22265625f, 1.0f
        0.0f, 1.0f, 0.0f, 1.0f ,
        1.0f, 0.0f, 0.0f, 1.0f,
        0.0f, 1.0f, 0.0f, 1.0f ,
        0.0f, 0.0f, 1.0f, 1.0f
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
        if (isTake){
            doTakePicture()
        }
    }

     fun setTextureID(id: Int) {
         mSurfaceTexture = SurfaceTexture(id)
         mSftCb?.invoke(mSurfaceTexture!!)
    }

     fun setUnit(cb: (st: SurfaceTexture) -> Unit) {
        mSftCb = cb
    }

    fun take(){
       isTake = true
    }

    //回调数据的宽，init 赋值
    var mFrameCallbackWidth: Int = 720
    var mFrameCallbackHeight: Int = 1080
    //用于存储拍照回调数据的buffer，大小是宽乘以高
    private var mOutPutIntBuffer: IntBuffer? = null

    private fun doTakePicture() {
        isTake = false
        Log.e("lhc","width:$mFrameCallbackWidth,height:$mFrameCallbackHeight")
        if (mOutPutIntBuffer == null) {
            mOutPutIntBuffer = IntBuffer.allocate(mFrameCallbackWidth * mFrameCallbackHeight)
        }

        //todo 这里可以用离屏渲染的方式，增加水印，后面再加
        //        mCamera2BaseFilter.draw(mTransformMatrix);

        //通过这个api获取画的这一帧数据
        GLES20.glReadPixels(
            0, 0, mFrameCallbackWidth, mFrameCallbackHeight,
            GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, mOutPutIntBuffer
        )

        //获取到图片数据了
        val origin = mOutPutIntBuffer!!.array()
        Log.e("lhc","origin:${origin.size}")
        val result = IntArray(mFrameCallbackWidth * mFrameCallbackHeight)
        //解决方向颠倒问题
        for (i in 0 until mFrameCallbackHeight) {
            for (j in 0 until mFrameCallbackWidth) {
                val pix = origin[i * mFrameCallbackWidth + j]
                val pb = pix shr 16 and 0xff
                val pr = pix shl 16 and 0x00ff0000
                val pix1 = pix and -0xff0100 or pr or pb
                result[(mFrameCallbackHeight - i - 1) * mFrameCallbackWidth + j] = pix1
            }
        }
        Thread(Runnable {
            val bitmap = Bitmap.createBitmap(mFrameCallbackWidth, mFrameCallbackHeight, Bitmap.Config.ARGB_8888)
            val byteBuffer = IntBuffer.wrap(result)
            bitmap.copyPixelsFromBuffer(byteBuffer)
            saveBitmap(bitmap)
            bitmap.recycle()
        }).start()
    }
    fun saveBitmap(bitmap: Bitmap) {
        val path = getSD() + "/OpenGLDemo/photo/"
        val folder = File(path)
        if (!folder.exists() && !folder.mkdirs()) {
            return
        }
        val dataTake = System.currentTimeMillis()
        val jpegName = path + "_" + dataTake + ".jpg"
        try {
            val fos = FileOutputStream(jpegName)
            val bos = BufferedOutputStream(fos)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
            bos.flush()
            bos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    protected fun getSD(): String {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
    }

}