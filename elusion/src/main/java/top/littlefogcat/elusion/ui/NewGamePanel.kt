package top.littlefogcat.elusion.ui

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import top.littlefogcat.phy2d.android.FrictionSurfaceView
import top.littlefogcat.phy2d.base.Object
import top.littlefogcat.phy2d.scene.FrictionScene
import top.littlefogcat.common.util.ScreenUtil
import top.littlefogcat.elusion.controller.NewGameController

/**
 * This may be the abandoned draft of aircraft war? I can't remember.
 *
 * @Author：littlefogcat
 * @Email：littlefogcat@foxmail.com
 */
class NewGamePanel : FrictionSurfaceView {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    lateinit var main: Object
    override var u: Float = 0.1f // 摩擦系数
    val controller = NewGameController(this)

    override fun preDraw(canvas: Canvas) {
        canvas.drawColor(Color.GRAY)
    }

    override fun afterDraw(canvas: Canvas) {
        controller.checkGameOver()
    }

    override fun onSurfaceUpdated() {
        super.onSurfaceUpdated()
        controller.onSurfaceUpdate()
    }
    
    override fun onSceneCreated(scene: FrictionScene) {
        ScreenUtil.update(context as Activity)

        val cx: Float = (ScreenUtil.realWidth / 2).toFloat()
        val cy: Float = (ScreenUtil.realHeight / 2).toFloat()
        main = createCircle(
            r = 18f,
            color = Color.parseColor("#BBFF0000"),
            mass = 24f,
            x = cx,
            y = cy,
            vx = 0f,
            vy = 0f,
            bounded = true,
            elasticity = 0f,
            maxVelocity = 11f,
        )
        Log.d(TAG, "onSceneCreated: ")
        controller.createOneRandomEnemy()
    }

    companion object {
        const val TAG = "NewGamePanel"
    }
}