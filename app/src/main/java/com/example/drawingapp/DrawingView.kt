package com.example.drawingapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
class DrawingView(context:Context,attrs:AttributeSet):View(context,attrs) {
    private var mDrawPath: CustomPath? = null
    private var mBitMap: Bitmap? = null
    private var mDrawPaint:Paint? = null
    private var mCanvasPaint:Paint? = null
    private var mBrushSize:Float = 20.0f
    private var color = Color.BLACK
    private var canvas:Canvas? = null

    init {
        setupDrawing()
    }

    private fun setupDrawing(){
        mDrawPaint = Paint()
        mDrawPath = CustomPath(color,mBrushSize)
        mDrawPaint!!.color = color
        mDrawPaint!!.style = Paint.Style.STROKE
        mDrawPaint!!.strokeJoin = Paint.Join.ROUND
        mDrawPaint!!.strokeCap = Paint.Cap.ROUND
        mCanvasPaint = Paint(Paint.DITHER_FLAG)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mBitMap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888)
        canvas = Canvas(mBitMap!!)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawBitmap(mBitMap!!,0.0f,0.0f,mCanvasPaint)
        if (!mDrawPath!!.isEmpty){
            mDrawPaint!!.strokeWidth = mDrawPath!!.brushThickness
            mDrawPaint!!.color = mDrawPath!!.color
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        println(event.action)
        var touchX = event.x
        var touchY = event.x
        when(event.action){
            MotionEvent.ACTION_DOWN -> {
                mDrawPath!!.color = color
                mDrawPath!!.brushThickness = mBrushSize
                mDrawPath!!.reset()
                mDrawPath!!.moveTo(touchX,touchY)
            }
            MotionEvent.ACTION_MOVE -> {
                mDrawPath!!.lineTo(touchX, touchY)
            }
            MotionEvent.ACTION_UP -> {
                mDrawPath = CustomPath(color,mBrushSize)
            }
            else -> return false
        }
//        invalidate()
        return true
    }

    internal inner class CustomPath(var color: Int, var brushThickness:Float): Path(){

    }
}