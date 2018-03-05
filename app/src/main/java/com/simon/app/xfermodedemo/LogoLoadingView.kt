package com.simon.app.xfermodedemo

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View


/**
 * desc: load
 * auther: xw
 * date: 2018/3/5
 * @auther: xw
 */
class LogoLoadingView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var bitmap: Bitmap? = null
    private var xfermode: PorterDuffXfermode? = null

    private var currentTop = 0

    private var rectF: RectF? = null

    init {
        paint.apply {
            style = Paint.Style.FILL
            isDither = true//设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
            isFilterBitmap = true //加快显示速度，本设置项依赖于dither和xfermode的设置
        }
        bitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher2)
        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

        currentTop = bitmap!!.height
        rectF = RectF(0f, currentTop.toFloat(), bitmap!!.width.toFloat(), bitmap!!.height.toFloat())
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.apply {
            rectF!!.top = currentTop.toFloat()

            val saveLayer = saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null)
            drawBitmap(bitmap, 0f, 0f, null)
            paint.xfermode = xfermode
            paint.color = Color.RED
            drawRect(rectF, paint)
            paint.xfermode = null
            restoreToCount(saveLayer)

            if (currentTop > 0) {
                currentTop--
                postInvalidate()
            }
        }
    }
}