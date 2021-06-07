package top.littlefogcat.gravitysample

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import top.littlefogcat.math.FreeVector
import top.littlefogcat.math.Point
import top.littlefogcat.math.isZero
import top.littlefogcat.phy2d.android.BaseSurfaceView
import top.littlefogcat.phy2d.base.CircleObject
import top.littlefogcat.phy2d.scene.Scene
import top.littlefogcat.phy2d.shape.Border
import kotlin.math.absoluteValue

/**
 * @Author：littlefogcat
 * @Email：littlefogcat@foxmail.com
 */
class BilliardsSurface : BaseSurfaceView {
    private var mainBall: CircleObject? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun addMaimBall(obj: CircleObject, pos: Point) {
        mainBall = obj
        addObject(obj, pos)
    }

    override fun onCreateScene(holder: SurfaceHolder, format: Int, width: Int, height: Int): Scene {
        setFrameRate(60)
        return BilliardsScene(Border(100, 100, width - 100, height - 100))
    }

    override fun preDraw(canvas: Canvas) {
        canvas.drawColor(Color.parseColor("#33BB55"))
    }

    override fun afterDraw(canvas: Canvas) {
        if (aiming) drawCue(canvas)
    }

    private fun drawCue(canvas: Canvas) {
        val main = mainBall ?: return

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 10f
        paint.color = Color.parseColor("#88ffffff")
        paint.pathEffect = DashPathEffect(floatArrayOf(24f, 24f), 0f)
        canvas.drawLine(main.position.x, main.position.y, main.position.x * 5 - ex * 4, main.position.y * 5 - ey * 4, paint)

        paint.pathEffect = null
        paint.color = Color.BLACK
        canvas.drawLine(main.position.x, main.position.y, ex, ey, paint)

        paint.color = Color.parseColor("#ee0000")
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = 15f
        canvas.drawLine(100f, 100f, 100 + strengthPercent.absoluteValue * 9, 100f, paint)

        paint.reset()
    }

    override fun onSurfaceUpdated() {
        val balls = scene?.objects?.values?.toList() ?: return
        isAllStopped = balls.all {
            it.velocity.absoluteValue.isZero()
        }
        if (aiming) {
            // 更新蓄力
            strengthPercent++
            if (strengthPercent > 100) strengthPercent -= 200
        }
    }

    var isAllStopped = true
    private var aiming = false
    private var ex = 0f
    private var ey = 0f

    private val maxStrength by lazy { mainBall!!.mass * 45 }
    private var strengthPercent = 0f

    fun startAim() {
        aiming = true
    }

    fun setEdge(x: Float, y: Float) {
        ex = x
        ey = y
    }

    fun shoot() {
        if (!aiming) return
        val main = mainBall ?: return
        aiming = false
        // 手指位置 -> 球心位置
        val impulseVec = FreeVector(main.position.x - ex, main.position.y - ey)
        Log.d(TAG, "shoot: $impulseVec")
        impulseVec.setValue(maxStrength * (strengthPercent.absoluteValue / 100 + 0.1f))
        main.giveImpulse(impulseVec, Point(ex, ey) - main.position)

        strengthPercent = 0f
    }
}