package com.simon.app.xfermodedemo

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Xfermode
import android.graphics.Shader
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.RectF
import android.util.Log

/**
 * desc: 自定义
 * auther: xw
 * date: 2018/3/2
 * @auther: xw
 */
class CustomerView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private val paint = Paint()

    private val bmWidth = 250
    private val bmHeight = 250

    private val ROW_MAX = 4   // number of samples per row
    private var mSrcB: Bitmap? = null
    private var mDstB: Bitmap? = null
    private var mBG: Shader? = null     // background checker-board pattern
    private val sModes = arrayOf<Xfermode>(
            PorterDuffXfermode(PorterDuff.Mode.CLEAR),
            PorterDuffXfermode(PorterDuff.Mode.SRC),
            PorterDuffXfermode(PorterDuff.Mode.DST),
            PorterDuffXfermode(PorterDuff.Mode.SRC_OVER),
            PorterDuffXfermode(PorterDuff.Mode.DST_OVER),
            PorterDuffXfermode(PorterDuff.Mode.SRC_IN),
            PorterDuffXfermode(PorterDuff.Mode.DST_IN),
            PorterDuffXfermode(PorterDuff.Mode.SRC_OUT),
            PorterDuffXfermode(PorterDuff.Mode.DST_OUT),
            PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP),
            PorterDuffXfermode(PorterDuff.Mode.DST_ATOP),
            PorterDuffXfermode(PorterDuff.Mode.XOR),
            PorterDuffXfermode(PorterDuff.Mode.DARKEN),
            PorterDuffXfermode(PorterDuff.Mode.LIGHTEN),
            PorterDuffXfermode(PorterDuff.Mode.MULTIPLY),
            PorterDuffXfermode(PorterDuff.Mode.SCREEN))
    private val sLabels = arrayOf(
            "Clear", "Src", "Dst", "SrcOver",
            "DstOver", "SrcIn", "DstIn", "SrcOut",
            "DstOut", "SrcATop", "DstATop", "Xor",
            "Darken", "Lighten", "Multiply", "Screen")

    init {
        paint.apply {
            color = Color.parseColor("#FFDFDBDC")
            isAntiAlias = true
            style = Paint.Style.FILL
        }

        mSrcB = makeSrc(bmWidth, bmHeight)
        mDstB = makeDst(bmWidth, bmHeight)
        // make a ckeckerboard pattern
        val bm = Bitmap.createBitmap(intArrayOf(-0x1, -0x333334, -0x333334, -0x1), 2, 2, Bitmap.Config.RGB_565)
        mBG = BitmapShader(bm, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
        val matrix = Matrix()
        matrix.setScale(6F, 6F)
        mBG!!.setLocalMatrix(matrix)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {

        canvas?.apply {
            drawColor(Color.WHITE)
            val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            labelPaint.textSize = 40f
            labelPaint.textAlign = Paint.Align.CENTER
            val paint = Paint()
            paint.isFilterBitmap = false
            translate(15f, 20f)
            var x = 0
            var y = 0
            //起点
            var drawX0 = 0f
            var drawY0 = 0f
            //终点
            var drawX1 = 0f
            var drawY1 = 0f

            for (i in 0 until sModes.size) {
                drawX0 = x.toFloat()
                drawY0 = y.toFloat()
                drawX1 = x.toFloat() + bmWidth
                drawY1 = y.toFloat() + bmHeight

                Log.i(TAG, "x=$x, y=$y")

                // draw the label
                drawText(sLabels[i], x + bmWidth / 2f, y.toFloat(), labelPaint)

                translate(drawX0, drawY0)

                // draw the border
                paint.style = Paint.Style.STROKE
                paint.shader = null
                //外围的线
                drawRect(drawX0 - 0.5f, drawY0 + labelPaint.textSize - 0.5f, drawX1 + 0.5f, drawY1 + 0.5f, paint)

                // draw the checker-board pattern
                paint.style = Paint.Style.FILL
                paint.shader = mBG

                //画背景
                drawRect(drawX0, drawY0, drawX1, drawY1, paint)

                // draw the src/dst example into our offscreen bitmap
                val sc = saveLayer(drawX0, drawY0, drawX1, drawY1, null)
                translate(drawX0, drawY0)

                //绘制DST
                drawBitmap(mDstB, 0f, 0f, paint)
                paint.xfermode = sModes[i]

                //绘制SRC
                drawBitmap(mSrcB, 0f, 0f, paint)
                paint.xfermode = null

                restoreToCount(sc)

                x += bmWidth + 15
                // wrap around when we've drawn enough for one row
                if (i % ROW_MAX == ROW_MAX - 1) {
                    x = 0
                    y += bmHeight + 30
                }
            }
        }
    }

    // create a bitmap with a circle, used for the "dst" image
    private fun makeDst(w: Int, h: Int): Bitmap {
        val bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bm)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.parseColor("#FFFFCC44")
        canvas.drawOval(RectF(0f, 0f, w * 3f / 4, h * 3f / 4), paint)
        return bm
    }

    // create a bitmap with a rect, used for the "src" image
    private fun makeSrc(w: Int, h: Int): Bitmap {
        val bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bm)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.parseColor("#FF66AAFF")
        canvas.drawRect(w / 3f, h / 3f, w * 19f / 20, h * 19f / 20, paint)
        return bm
    }

    companion object {
        /** TAG  */
        private val TAG = CustomerView::class.java.simpleName
    }
}