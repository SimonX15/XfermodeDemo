package com.simon.app.xfermodedemo

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

/**
 * desc: 圆图
 *
 * auther: xw
 *
 * date: 2018/3/6
 *
 * @auther: xw
 */
class CircleImageView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    var paint = Paint(Paint.ANTI_ALIAS_FLAG)

    var bitmap: Bitmap? = null

    var circleBG: Bitmap? = null

    var bmHeight = 0

    var bmWidth = 0

    init {
//        context.obtainStyledAttributes(attrs,)

        paint.apply {
            isDither = true
            isFilterBitmap = true
        }

        bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_bg)

        bmWidth = bitmap!!.width
        bmHeight = bitmap!!.height

        bmWidth = Math.min(bmWidth, bmHeight)
        bmHeight = bmWidth

        circleBG = createCircleBG()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        canvas?.apply {
            val layer = saveLayer(0f, 0f, bmWidth.toFloat(), bmHeight.toFloat(), null)
            drawBitmap(bitmap, 0f, 0f, null)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
            drawBitmap(circleBG, 0f, 0f, paint)
            paint.xfermode = null
            restoreToCount(layer)
        }
    }

    private fun createCircleBG(): Bitmap {
        val bitmap = Bitmap.createBitmap(bmWidth, bmHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawCircle(bmWidth / 2f, bmHeight / 2f, bmWidth / 2f, paint)
        return bitmap
    }
}