package top.littlefogcat.common.ui

import android.content.Context
import android.graphics.*
import android.view.View

/**
 * @Author：littlefogcat
 * @Email：littlefogcat@foxmail.com
 */
internal class JoystickInnerView(
    context: Context,
    private val bgColor: Int,
    private val decColor: Int,
    colorMask1: Int,
    colorMask2: Int,
    private val r: Float,
    private val onDraw: ((Canvas) -> Unit)? = null
) : View(context) {
    companion object {
        const val TAG = "JoystickInnerView"
    }

    private val paint = Paint()
    private val path = Path()
    private val gradient = LinearGradient(
        0f, -0.6f, 0f, 1.4f * r,
        colorMask1,
        colorMask2,
        Shader.TileMode.MIRROR
    )
    private val cx = r
    private val cy = r

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension((r * 2).toInt(), (r * 2).toInt())
    }

    override fun onDraw(canvas: Canvas) {
        if (onDraw != null) {
            onDraw.invoke(canvas)
            return
        }
        // 绘制背景
        paint.reset()
        paint.color = bgColor
        canvas.drawCircle(cx, cy, r, paint)

        paint.color = decColor
        // 绘制内圈指示器
        var x: Float = cx
        var y: Float = cy
        val arg1 = r / 12
        val arg2 = r / 6
        val arg3 = r / 3

        path.reset()
        y -= arg1
        path.moveTo(x, y)
        x -= arg2
        y -= arg2
        path.lineTo(x, y)
        path.arcFromTo(x, y, cx, y - arg3, r)
        x = cx
        y -= arg3
        path.arcFromTo(x, y, cx + arg2, y + arg3, r)
        canvas.drawPath(path, paint)

        path.reset()
        x = cx + arg1
        y = cy
        path.moveTo(x, y)
        x += arg2
        y -= arg2
        path.lineTo(x, y)
        path.arcFromTo(x.also { x += arg3 }, y.also { y = cy }, x, y, r)
        path.arcFromTo(x.also { x -= arg3 }, y.also { y += arg2 }, x, y, r, true)
        canvas.drawPath(path, paint)

        path.reset()
        x = cx
        y = cy + arg1
        path.moveTo(x, y)
        x += arg2
        y += arg2
        path.lineTo(x, y)
        path.arcFromTo(x.also { x -= arg2 }, y.also { y += arg3 }, x, y, r, true)
        path.arcFromTo(x.also { x -= arg2 }, y.also { y -= arg3 }, x, y, r, true)
        canvas.drawPath(path, paint)

        path.reset()
        x = cx - arg1
        y = cy
        path.moveTo(x, y)
        x -= arg2
        y += arg2
        path.lineTo(x, y)
        path.arcFromTo(x.also { x -= arg3 }, y.also { y = cy }, x, y, r, true)
        path.arcFromTo(x.also { x += arg3 }, y.also { y -= arg2 }, x, y, r)
        canvas.drawPath(path, paint)

        // 画环装装饰
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = r * 0.07f
        val arcR = r * 0.85f
        canvas.drawArc(
            cx - arcR,
            cy - arcR,
            cx + arcR,
            cy + arcR,
            0f,
            360f,
            false,
            paint
        )

        // 画遮罩
        paint.style = Paint.Style.FILL
        paint.shader = gradient
        canvas.drawCircle(cx, cy, r, paint)
    }

    fun setTranslation(x: Float, y: Float) {
        translationX = x
        translationY = y
    }

}