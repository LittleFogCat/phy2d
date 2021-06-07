package top.littlefogcat.common.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.ViewGroup
import top.littlefogcat.common.R
import top.littlefogcat.common.getColor
import top.littlefogcat.common.ui.JoystickView.Companion.FLAG_MOVABLE
import top.littlefogcat.math.FreeVector

/**
 * 摇杆View。
 * 摇杆分为外圈和内圈，内圈为操作舵，外圈为操作盘。在默认情况下，内圈为一个圆形，外圈为一个较大的圆形。
 * 可以通过[innerCircleImage]和[outCircleImage]来改变其图形。
 *
 * [outRadius]为外圈半径，[innerRadius]为内圈半径，[outRadiusPercent]为外圈半径占屏幕宽度比例
 * [innerRadius]为内圈半径占屏幕宽度比例。
 *
 * 如果设置了[outRadius]和[innerRadius]，那么[outRadiusPercent]和[innerRadiusPercent]的设置无效。
 * 如果都不进行设置，那么进行默认设置，内圈半径为屏幕宽度的1/32，外圈半径为屏幕宽度的1/12。
 *
 * 当设置了flag: [FLAG_MOVABLE]的时候，摇杆的位置会随着手指初次点击屏幕时的位置而改变。
 *
 * @Author：littlefogcat
 * @Email：littlefogcat@foxmail.com
 */
@Suppress("unused", "SpellCheckingInspection", "MemberVisibilityCanBePrivate", "DEPRECATION")
@SuppressLint("RtlHardcoded")
class JoystickView : ViewGroup {
    companion object {
        const val TAG = "JoystickView"

        /**
         * 表示是否可以移动的flag。如果设置了该flag，那么当手指接触到屏幕时，当前View的中心就会自动移动到
         * 手指按下的位置。否则，View的位置是固定的。
         */
        const val FLAG_MOVABLE = 1

        /**
         * 设置该flag，则不会变为半透明
         */
        const val FLAG_NO_HIDE = 2
    }

    private var innerView: JoystickInnerView

    private val screenWidth: Int
        get() {
            // 获取屏幕宽度
            val activity = context as Activity
            val metrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(metrics)
            return metrics.widthPixels
        }

    /**
     * 外圈半径
     */
    private var outRadius: Float = -1f

    /**
     * 内圈半径
     */
    private var innerRadius: Float = -1f

    /**
     * 外圈半径（按屏幕宽度百分比）
     */
    private var outRadiusPercent: Float = 0f

    /**
     * 内圈半径（按屏幕宽度百分比）
     */
    private var innerRadiusPercent: Float = 0f

    /**
     * flags，标记了一些特性
     */
    private var flags = 0

    /**
     * 内圈图片
     */
    var innerCircleImage: Bitmap? = null

    /**
     * 外圈图片
     */
    var outCircleImage: Bitmap? = null

    /**
     * 指示器图片
     */
    var indicatorImage: Bitmap? = null

    /**
     * 半透明背景颜色
     */
    val outCircleColor: Int

    /**
     * 外圈花纹颜色
     */
    val outCircleDecorationColor: Int

    /**
     *  内圈操作舵背景色
     */
    var innerCircleColor: Int

    /**
     * 内圈操作舵花纹颜色
     */
    var innerCircleDecorationColor: Int

    // 内圈遮罩/高光
    val innerCircleColorMask1: Int
    val innerCircleColorMask2: Int

    /**
     * 隐藏时的透明度，通过[R.styleable.JoystickView_alphaWhenHide]设置。
     */
    val alphaWhenHide: Float

    private val paint = Paint()
    private val path = Path()
    private var touchEventHandler: TouchEventHandler

    /**
     * 当操作摇杆时，通过此回调通知。
     */
    private var onJoystickListener: ((JoystickEvent) -> Unit)? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        val sw = screenWidth
        val a = context.obtainStyledAttributes(attrs, R.styleable.JoystickView)
        innerRadius = a.getDimension(R.styleable.JoystickView_innerRadius, -1f)
        innerRadiusPercent = a.getFloat(R.styleable.JoystickView_innerRadiusPercent, 1f / 27)
        outRadius = a.getDimension(R.styleable.JoystickView_outRadius, -1f)
        outRadiusPercent = a.getFloat(R.styleable.JoystickView_outRadiusPercent, 1f / 8.7f)
        alphaWhenHide = a.getFloat(R.styleable.JoystickView_alphaWhenHide, 0.3f)
        outCircleColor = a.getColor(R.styleable.JoystickView_outBgColor, getColor(R.color.outCircleColor))
        outCircleDecorationColor = a.getColor(R.styleable.JoystickView_outDecColor, getColor(R.color.outCircleDecorationColor))
        innerCircleColor = a.getColor(R.styleable.JoystickView_innerBgColor, getColor(R.color.innerCircleColor))
        innerCircleDecorationColor = a.getColor(R.styleable.JoystickView_innerDecColor, getColor(R.color.innerCircleDecorationColor))
        innerCircleColorMask1 = a.getColor(R.styleable.JoystickView_innerCircleMask1, getColor(R.color.innerCircleMask1))
        innerCircleColorMask2 = a.getColor(R.styleable.JoystickView_innerCircleMask2, getColor(R.color.innerCircleMask2))
        a.recycle()

        // 处理外圈半径
        if (outRadius <= 0) {
            if (outRadiusPercent <= 0) {
                throw IllegalArgumentException("Attr [outRadius] and [outRadiusPercent] not set correctly.")
            }
            outRadius = outRadiusPercent * sw
        }
        // 处理内圈半径
        if (innerRadius <= 0) {
            if (innerRadiusPercent <= 0) {
                throw IllegalArgumentException("Attr [innerRadius] and [innerRadiusPercent] not set correctly.")
            }
            innerRadius = innerRadiusPercent * sw
        }
        if (innerRadius >= outRadius)
            throw IllegalArgumentException(
                "[innerRadius] must be less than [outRadius]. " +
                        "Current [innerRadius] = $innerRadius, [outRadius] = $outRadius"
            )

//        Log.d(TAG, "init: innerRadius = $innerRadius, outRadius = $outRadius")

        // 设置paint属性
        paint.isAntiAlias = true

        // 初始化操作舵
        innerView = JoystickInnerView(
            context,
            innerCircleColor,
            innerCircleDecorationColor,
            innerCircleColorMask1,
            innerCircleColorMask2,
            innerRadius
        )
        innerView.layoutParams = LayoutParams((innerRadius * 2).toInt(), (innerRadius * 2).toInt())
        addView(innerView)

        // 设置走onDraw方法
        setWillNotDraw(false)

        // 初始化TouchEventHandler
        touchEventHandler = TouchEventHandler()

        // 初始化半透明
        alpha = alphaWhenHide
    }

    /**
     * 添加flag
     */
    fun addFlags(flags: Int) {
        this.flags = this.flags or flags
    }

    /**
     * 移除flag
     */
    fun removeFlags(flags: Int) {
        this.flags = this.flags and flags.inv()
    }

    fun isMovable(): Boolean = (flags and FLAG_MOVABLE) != 0

    fun isHidable(): Boolean = (flags and FLAG_NO_HIDE) == 0

    /**
     * 外圈坐标(0.1r, 0.1r)
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val r = outRadius
        measureChildren(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension((2.2 * r).toInt(), (2.2 * r).toInt())
    }

    /**
     * 操作舵处于中心
     */
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
//        Log.d(TAG, "onLayout: $left $top $right $bottom")

        val l = 1.1f * outRadius - innerRadius
        val t = 1.1f * outRadius - innerRadius
        val r = l + innerView.measuredWidth
        val b = t + innerView.measuredHeight
//        Log.d(TAG, "onLayout: $l $t $r $b")
//        Log.d(TAG, "onLayout: ${innerView.measuredWidth}")
        innerView.layout(l.toInt(), t.toInt(), r.toInt(), b.toInt())
    }

    /**
     * 绘制外圈背景
     */
    override fun onDraw(canvas: Canvas) {
//        Log.d(TAG, "onDraw: ")
        val l = width // View的边长
        val r = outRadius

        // 绘制外圈
        // 外圈的坐标在[0.1l, 0.1l]的位置
        // 如果设置了[outCircleImage]，则直接绘制；否则，绘制默认图像。
        val c = l * 0.5f // 外圈圆心坐标
        if (outCircleImage != null) {
            canvas.drawBitmap(outCircleImage!!, 0F, 0F, paint)
        } else {
            // 绘制背景
            paint.color = outCircleColor
            canvas.drawCircle(c, c, outRadius, paint)

            // 绘制花纹
            // 高度0.25 厚度0.08 宽度0.8 圆心1.1
            paint.color = outCircleDecorationColor
            // 上
            var top = 0.25f * r
            var bottom = 0.5f * r
            var left = 0.74f * r
            var leftIn = 0.85f * r
            var topIn = 0.33f * r
            var rightIn = 1.35f * r
            var right = 1.46f * r
            path.apply {
                reset()
                moveTo(c, top)
                lineTo(left, bottom)
                lineTo(leftIn, bottom)
                lineTo(c, topIn)
                lineTo(rightIn, bottom)
                lineTo(right, bottom)
            }
            canvas.drawPath(path, paint)
            path.reset()
            path.moveTo(c, topIn + 0.05f * r)
            path.lineTo(leftIn + 0.1f * r, bottom - 0.022f * r)
            path.lineTo(rightIn - 0.1f * r, bottom - 0.022f * r)
            canvas.drawPath(path, paint)
            // 下
            top = 1.95f * r
            bottom = 1.7f * r
            topIn = 1.87f * r
            path.apply {
                reset()
                moveTo(c, top)
                lineTo(left, bottom)
                lineTo(leftIn, bottom)
                lineTo(c, topIn)
                lineTo(rightIn, bottom)
                lineTo(right, bottom)
            }
            canvas.drawPath(path, paint)
            path.reset()
            path.moveTo(c, topIn - 0.05f * r)
            path.lineTo(leftIn + 0.1f * r, bottom + 0.022f * r)
            path.lineTo(rightIn - 0.1f * r, bottom + 0.022f * r)
            canvas.drawPath(path, paint)
            // 左
            top = 0.25f * r
            bottom = 0.5f * r
            left = 1.46f * r
            leftIn = 1.35f * r
            topIn = 0.33f * r
            rightIn = 0.85f * r
            right = 0.74f * r
            path.apply {
                reset()
                moveTo(top, c)
                lineTo(bottom, left)
                lineTo(bottom, leftIn)
                lineTo(topIn, c)
                lineTo(bottom, rightIn)
                lineTo(bottom, right)
            }
            canvas.drawPath(path, paint)
            path.reset()
            path.moveTo(topIn + 0.05f * r, c)
            path.lineTo(bottom - 0.022f * r, leftIn - 0.1f * r)
            path.lineTo(bottom - 0.022f * r, rightIn + 0.1f * r)
            canvas.drawPath(path, paint)
            // 右
            top = 1.95f * r
            bottom = 1.7f * r
            topIn = 1.87f * r
            path.apply {
                reset()
                moveTo(top, c)
                lineTo(bottom, left)
                lineTo(bottom, leftIn)
                lineTo(topIn, c)
                lineTo(bottom, rightIn)
                lineTo(bottom, right)
            }
            canvas.drawPath(path, paint)
            path.reset()
            path.moveTo(topIn - 0.05f * r, c)
            path.lineTo(bottom + 0.022f * r, leftIn - 0.1f * r)
            path.lineTo(bottom + 0.022f * r, rightIn + 0.1f * r)
            canvas.drawPath(path, paint)
        }

    }

    private fun Canvas.drawOutDecoration(
        c: Float,
        top: Float,
        bottom: Float,
        left: Float,
        leftIn: Float,
        topIn: Float,
        rightIn: Float,
        right: Float
    ) {
        path.apply {
            reset()
            moveTo(c, top)
            lineTo(left, bottom)
            lineTo(leftIn, bottom)
            lineTo(c, topIn)
            lineTo(rightIn, bottom)
            lineTo(right, bottom)
        }
        drawPath(path, paint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return touchEventHandler.handle(event)
    }

    /**
     * 设置[onJoystickListener]
     */
    fun setJoystickListener(l: (JoystickEvent) -> Unit) {
        this.onJoystickListener = l
    }

    class JoystickEvent(
        val type: Type,
        val vector: FreeVector
    ) {
        enum class Type {
            ACTION_UP,
            ACTION_DOWN,
            ACTION_MOVE,
            OTHER
        }
    }


    private inner class TouchEventHandler {
        /**
         * 圆心横坐标
         */
        val cx: Float = 1.1f * outRadius

        /**
         * 圆心纵坐标
         */
        val cy: Float = 1.1f * outRadius

        /**
         * 内圈与外圈圆心最大距离
         */
        val maxDist = outRadius - innerRadius

        fun handle(event: MotionEvent): Boolean {
            val eventType: JoystickEvent.Type = when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (isHidable()) alpha = 1f
                    if (!isMovable()) moveInnerViewTo(event.x, event.y)
                    JoystickEvent.Type.ACTION_DOWN
                }
                MotionEvent.ACTION_MOVE -> {
                    moveInnerViewTo(event.x, event.y)
                    JoystickEvent.Type.ACTION_MOVE
                }
                MotionEvent.ACTION_UP -> {
                    if (isHidable()) alpha = alphaWhenHide
                    innerView.setTranslation(0f, 0f)
                    JoystickEvent.Type.ACTION_UP
                }
                else -> JoystickEvent.Type.OTHER
            }
            if (onJoystickListener != null) {
                val dx = event.x - cx
                val dy = event.y - cy
                onJoystickListener!!(
                    JoystickEvent(eventType, FreeVector(dx, dy))
                )
            }
            return true
        }

        /**
         * 将操作舵[innerView]圆心移动到([x], [y])。
         * 操作舵最多移动到与外圈内切的地方；如果目标位置超出外圈，则移动到最接近的、与外圈内切的点。
         */
        fun moveInnerViewTo(x: Float, y: Float) {
            // 目标点与圆心距离
            val dist = ((x - cx).p2 + (y - cy).p2).sqrt
            if (dist < maxDist) {
                // 如果距离在圈内，直接移动到目标位置
                innerView.setTranslation(x - cx, y - cy)
            } else {
                // 计算最接近的相切点
                // 设目标圆心为(rx, ry)，根据相似三角形原理，
                // ∵ (y - cy) / dist = (ry - cy) / maxDist
                // ∴ ry = (y - cy) / dist * maxDist + cy
                // rx 同理
                val ry = (y - cy) / dist * maxDist + cy
                val rx = (x - cx) / dist * maxDist + cx
                innerView.setTranslation(rx - cx, ry - cy)
            }
        }

    }

}