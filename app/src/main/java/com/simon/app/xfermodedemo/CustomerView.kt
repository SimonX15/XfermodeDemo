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
import jdk.nashorn.internal.runtime.regexp.joni.encoding.CharacterType.W
import android.graphics.Canvas.CLIP_TO_LAYER_SAVE_FLAG
import android.graphics.Canvas.FULL_COLOR_LAYER_SAVE_FLAG
import android.graphics.Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
import android.graphics.Canvas.CLIP_SAVE_FLAG
import android.graphics.Canvas.MATRIX_SAVE_FLAG
import sun.swing.SwingUtilities2.drawRect
import java.awt.font.ShapeGraphicAttribute.STROKE
import android.graphics.Paint.Align
import android.graphics.Paint.ANTI_ALIAS_FLAG


/**
 * desc: 自定义
 * auther: xw
 * date: 2018/3/2
 * @auther: xw
 */
class CustomerView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private val paint = Paint()

    private val W = 200
    private val H = 200

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

        mSrcB = makeSrc(W, H)
        mDstB = makeDst(W, H)
        // make a ckeckerboard pattern
        val bm = Bitmap.createBitmap(intArrayOf(-0x1, -0x333334, -0x333334, -0x1), 2, 2, Bitmap.Config.RGB_565)
        mBG = BitmapShader(bm, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
        val m = Matrix()
        m.setScale(6F, 6F)
        mBG!!.setLocalMatrix(m)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {

        canvas?.apply {
            drawColor(Color.WHITE)
            val labelP = Paint(Paint.ANTI_ALIAS_FLAG)
            labelP.textAlign = Paint.Align.CENTER
            val paint = Paint()
            paint.isFilterBitmap = false
            translate(15f, 35f)
            var x = 0
            var y = 0
            for (i in 0 until sModes.size) {
                // draw the border
                paint.style = Paint.Style.STROKE
                paint.shader = null
                drawRect(x - 0.5f, y - 0.5f,
                        x.toFloat() + W.toFloat() + 0.5f, y + H + 0.5f, paint)
                // draw the checker-board pattern
                paint.style = Paint.Style.FILL
                paint.shader = mBG
                drawRect(x.toFloat(), y.toFloat(), x + W.toFloat(), y + H.toFloat(), paint)
                // draw the src/dst example into our offscreen bitmap
                val sc = saveLayer(x.toFloat(), y.toFloat(), x + W.toFloat(), y + H.toFloat(), null,
                        Canvas.MATRIX_SAVE_FLAG or
                                Canvas.CLIP_SAVE_FLAG or
                                Canvas.HAS_ALPHA_LAYER_SAVE_FLAG or
                                Canvas.FULL_COLOR_LAYER_SAVE_FLAG or
                                Canvas.CLIP_TO_LAYER_SAVE_FLAG)
                translate(x, y)
                drawBitmap(mDstB, 0, 0, paint)
                paint.xfermode = sModes[i]
                drawBitmap(mSrcB, 0, 0, paint)
                paint.xfermode = null
                restoreToCount(sc)
                // draw the label
                drawText(sLabels[i], x + W / 2, y - labelP.textSize / 2, labelP)
                x += W + 10
                // wrap around when we've drawn enough for one row
                if ((i % ROW_MAX) === ROW_MAX - 1) {
                    x = 0
                    y += H + 30
                }
            }
        }
    }

    // create a bitmap with a circle, used for the "dst" image
    private fun makeDst(w: Int, h: Int): Bitmap {
        val bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val c = Canvas(bm)
        val p = Paint(Paint.ANTI_ALIAS_FLAG)
        p.color = Color.parseColor("#FFFFCC44")
        c.drawOval(RectF(0f, 0f, (w * 3 / 4).toFloat(), (h * 3 / 4).toFloat()), p)
        return bm
    }

    // create a bitmap with a rect, used for the "src" image
    private fun makeSrc(w: Int, h: Int): Bitmap {
        val bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val c = Canvas(bm)
        val p = Paint(Paint.ANTI_ALIAS_FLAG)
        p.color = Color.parseColor("#FF66AAFF")
        c.drawRect(w / 3f, h / 3f, w * 19 / 20f, h * 19 / 20f, p)
        return bm
    }
}