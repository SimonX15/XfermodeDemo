package com.simon.app.xfermodedemo

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.Log
import android.view.View


/**
 * desc:
 * auther: xw
 * date: 2018/3/5
 * @auther: xw
 */
class CustomerView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var bmWidth = 0
    private var bmHeight = 0

    //源图像
    private var mSrcBm: Bitmap? = null
    //目标图像
    private var mDstBm: Bitmap? = null

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
        mPaint.apply {
            color = ContextCompat.getColor(context, R.color.bg)
            mPaint.isFilterBitmap = false
            style = Paint.Style.FILL
        }

        labelPaint.apply {
            labelPaint.textSize = 40f
            //居中
            labelPaint.textAlign = Paint.Align.CENTER
        }
    }


    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        initBm()
        val bmShader = createBmShader()
        canvas?.apply {
            //整体背景
            canvas.drawColor(Color.WHITE)

            //画布偏移
            canvas.translate(OFFSET.toFloat(), 60f)

            var x = 0
            var y = 0

            for (i in 0 until sModes.size) {
                //画四围
                mPaint.color = Color.BLACK
                mPaint.style = Paint.Style.STROKE
                mPaint.shader = null
                Log.i(TAG, "x=$x, y=$y")

                //label
                canvas.drawText(sLabels[i], x + bmWidth / 2f, y - 0.5f, labelPaint)

                //四围
                canvas.drawRect(x - 1f, y + 20f - 1f, x + bmWidth + 1f, y + 20f + bmHeight + 1f, mPaint)

                //背景
                mPaint.style = Paint.Style.FILL
                mPaint.shader = bmShader
                canvas.drawRect(x.toFloat(), y + 20f, (x + bmWidth).toFloat(), y + 20f + bmHeight, mPaint)

                //新建bitmap图层
//                val saveCount = saveLayer(x.toFloat(), y.toFloat(), x + bmWidth.toFloat(), y + bmHeight.toFloat(), null)
                val saveCount = saveLayer(x.toFloat(), y.toFloat(), x + bmWidth.toFloat(), y + bmHeight.toFloat(), null)
                canvas.translate(x.toFloat(), y.toFloat() + 20)
                //绘制DST
                drawBitmap(mDstBm, 0f, 0f, mPaint)
                mPaint.xfermode = sModes[i]
                //绘制SRC
                drawBitmap(mSrcBm, 0f, 0f, mPaint)
                mPaint.xfermode = null
                restoreToCount(saveCount)

                //坐标跟着变化
                x += bmWidth + OFFSET
                if (i % ROW_MAX == ROW_MAX - 1) {
                    x = 0
                    y += bmHeight + 80
                }
            }
        }
    }

    private fun initBm() {
        if (bmWidth == 0) {
            bmWidth = (width - OFFSET * 5) / ROW_MAX
            bmHeight = bmWidth
//            Log.i(TAG, "bmWidth=$bmWidth, bmHeight=$bmHeight")
        }

        if (mSrcBm == null) {
            mSrcBm = createSRC(bmWidth, bmHeight)
        }

        if (mDstBm == null) {
            mDstBm = createDST(bmWidth, bmHeight)
        }
    }


    /** 源图像 */
    private fun createSRC(width: Int, height: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        mPaint.color = ContextCompat.getColor(context, R.color.src)
        canvas.drawRect(width / 3f, height / 3f, width * 19 / 20f, height * 19 / 20f, mPaint)
        return bitmap
    }

    /** 目标图像 */
    private fun createDST(width: Int, height: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        mPaint.color = ContextCompat.getColor(context, R.color.dst)
        canvas.drawCircle(width / 3f, height / 3f, width / 3f, mPaint)
        return bitmap
    }

    /** 背景 */
    private fun createBmShader(): BitmapShader {
        val bitmap = Bitmap.createBitmap(intArrayOf(-0x1, -0x333334, -0x333334, -0x1), 2, 2, Bitmap.Config.ARGB_8888)
        val shader = BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
        val matrix = Matrix()
        matrix.setScale(8F, 8F)
        shader.setLocalMatrix(matrix)
        return shader
    }

    companion object {
        /** TAG  */
        private val TAG = CustomerView::class.java.simpleName

        /** 偏移量 */
        private val OFFSET = 15

        /** 最大行数 */
        private val ROW_MAX = 4
    }
}