package top.littlefogcat.elusion.ui

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import top.littlefogcat.math.FreeVector
import top.littlefogcat.common.util.ScreenUtil
import top.littlefogcat.elusion.controller.GameController

/**
 * @Author：littlefogcat
 * @Email：littlefogcat@foxmail.com
 */
class GamePanel : SurfaceView, SurfaceHolder.Callback {
    internal var running = true
    private val paint = Paint()
    private var thread: SurfaceThread? = null

    private val controller = GameController(this)

    /**
     * 主角小球
     */
    private val player get() = controller.player

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        holder.addCallback(this)
        paint.isAntiAlias = true
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        player.x = (ScreenUtil.realWidth / 2).toFloat()
        player.y = (ScreenUtil.realHeight / 2).toFloat()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        running = true
        thread = SurfaceThread()
        thread?.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        running = false
    }

    fun changeDirection(direction: FreeVector) {
        controller.interpolator.direction = direction
    }

    fun getAccelerate() = controller.interpolator.acceleration

    fun getMaxSpeed() = controller.interpolator.maxSpeed

    fun move() {
        if (!running) return
        controller.move()
    }

    /**
     * 重置速度
     */
    fun reset() {
        controller.interpolator.direction.setValue(0f, 0f)
    }

    fun setAcceleration(acceleration: Float) {
        controller.interpolator.acceleration = acceleration
    }

    fun setMaxSpeed(maxSpeed: Float) {
        controller.interpolator.maxSpeed = maxSpeed
    }

    inner class SurfaceThread : Thread() {
        override fun run() {
            init()

            while (running) {
                onDraw()
                sleep(16)
            }
        }

        private fun init() {
            ScreenUtil.update(context as Activity)
        }

        /**
         * 绘制Surface
         */
        private fun onDraw() {
            val canvas = holder.lockCanvas()
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

            paint.color = player.color
            canvas.drawCircle(player.x, player.y, player.r, paint)
            controller.enemies.forEach { enemy ->
                paint.color = enemy.color
                canvas.drawCircle(enemy.x, enemy.y, enemy.r, paint)
            }

            holder.unlockCanvasAndPost(canvas)
        }
    }
}