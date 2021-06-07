package top.littlefogcat.bouncedemo

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.SurfaceHolder
import top.littlefogcat.math.isZero
import top.littlefogcat.phy2d.android.BaseSurfaceView
import top.littlefogcat.phy2d.base.Object
import top.littlefogcat.phy2d.scene.BorderedGravityScene
import top.littlefogcat.phy2d.scene.Scene
import top.littlefogcat.phy2d.shape.Border
import kotlin.math.absoluteValue

/**
 * @Author：littlefogcat
 * @Email：littlefogcat@foxmail.com
 */
class MySurface : BaseSurfaceView {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun preDraw(canvas: Canvas) {
        canvas.drawColor(Color.WHITE)
        paint.strokeWidth = 3f
    }

    override fun onCreateScene(holder: SurfaceHolder, format: Int, width: Int, height: Int): Scene =
        object : BorderedGravityScene(Border(0, 0, width, height)) {
            override fun defaultHandleObjectTimerTick(obj: Object) {
                super.defaultHandleObjectTimerTick(obj)
                if ((obj.bottom.y - border.bottom).isZero(1f) && obj.velocity.y.isZero(0.01f)) {
                    // give friction impulse
                    // I = m△v = Ft
                    // t = 1, F = umg
                    // △v = Ft/m = ug
                    // 速度变化只与摩擦系数有关
                    val deltaV = 0.01f // 设定值
                    val vx = obj.velocity.x
                    if (vx.absoluteValue < deltaV) {
                        obj.velocity.setValue(0f)
                    } else {
                        obj.velocity.setValue(vx.absoluteValue - deltaV)
                    }
                }
            }
        }
}