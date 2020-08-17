package com.topband.audiovideodemo

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import androidx.core.graphics.scale
import java.util.concurrent.TimeUnit

class SurfaceView2 : SurfaceView,SurfaceHolder.Callback, Runnable {

    private lateinit var surfaceHolder:SurfaceHolder
    private lateinit var canvas: Canvas
    private var isDrawing = true
    private lateinit var mPaint: Paint
    private lateinit var bitMap:Bitmap

    constructor(context: Context):super(context){
       init()
    }
    constructor(context: Context,attributes: AttributeSet):super(context,attributes){
        init()
    }
    constructor(context: Context,attributes: AttributeSet,def: Int):super(context,attributes,def){
        init()
    }

    private fun init() {
        surfaceHolder = holder
        surfaceHolder.addCallback(this)
        isFocusableInTouchMode = true

        mPaint = Paint()
        mPaint.color = Color.GREEN
        mPaint.strokeWidth = 10f
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.style = Paint.Style.FILL_AND_STROKE

        bitMap = BitmapFactory.decodeResource(resources,R.drawable.th)
        post {
            bitMap = bitMap.scale(width,height)
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        isDrawing = false
        bitMap.recycle()
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        isDrawing = true
        Thread(this).start()
    }

    /**
     * 设置view的height或width为wrap_content时的值
     * 这里没有处理view的margin和padding
     * 所以在xml中设置margin和padding是没有效果的
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSpecMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val widthSpecSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightSpecMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val heightSpecSize = View.MeasureSpec.getSize(heightMeasureSpec)
        if (widthSpecMode == View.MeasureSpec.AT_MOST && heightSpecMode == View.MeasureSpec.AT_MOST) {
            setMeasuredDimension(context.resources.displayMetrics.widthPixels, dip2px(context, 100f))
        } else if (widthSpecMode == View.MeasureSpec.AT_MOST) {
            setMeasuredDimension(context.resources.displayMetrics.widthPixels, heightSpecSize)
        } else if (heightSpecMode == View.MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, dip2px(context, 100f))
        }
    }


    override fun run() {
        while (isDrawing){
             draw()
             TimeUnit.MILLISECONDS.sleep(10)
        }
    }

    private fun draw(){
        try {
            canvas = surfaceHolder.lockCanvas()
            canvas.drawCircle((measuredWidth/2).toFloat(), (measuredHeight/2).toFloat(),dip2px(context,50f).toFloat(),mPaint)
            canvas.drawBitmap(bitMap,0f,0f,mPaint)
        } finally {
            surfaceHolder.unlockCanvasAndPost(canvas)
        }
    }
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    fun dip2px(context: Context?, dpValue: Float): Int {
        if (context != null) {
            val scale = context.resources.displayMetrics.density
            return (dpValue * scale + 0.5).toInt()
        }
        return 0
    }
}