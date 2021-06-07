package top.littlefogcat.airecraftwar.game

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.SurfaceHolder
import top.littlefogcat.airecraftwar.game.objects.Aircraft
import top.littlefogcat.airecraftwar.game.objects.Bullet
import top.littlefogcat.phy2d.android.BaseSurfaceView
import top.littlefogcat.phy2d.base.Object
import top.littlefogcat.phy2d.scene.FrictionScene
import top.littlefogcat.phy2d.shape.Border
import top.littlefogcat.common.util.ScreenUtil
import top.littlefogcat.phy2d.scene.Scene

/**
 * @Author：littlefogcat
 * @Email：littlefogcat@foxmail.com
 */
class GameView : BaseSurfaceView {
    // ----------------- constructors -----------------

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    // ----------------- fields -----------------

    private var controller: GameController? = null

    // ----------------- public functions -----------------

    fun setGameController(controller: GameController) {
        this.controller = controller
    }

    // ----------------- override functions -----------------

    override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        ScreenUtil.update(context as Activity)

        initGameOptions()
        initScene()
    }

    override fun onCreateScene(holder: SurfaceHolder, format: Int, width: Int, height: Int): Scene {
        TODO("Not yet implemented")
    }

    override fun preDraw(canvas: Canvas) {
        canvas.drawColor(Color.parseColor("#FFEEEEEE"))
    }

    override fun onSurfaceUpdated() {
        controller!!.timeTick()
    }

    override fun onDraw(canvas: Canvas, obj: Object) {
        if (obj is Aircraft) {
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 3f
        } else if (obj is Bullet) {
            paint.style = Paint.Style.FILL
        }
        super.onDraw(canvas, obj)
    }

    // ----------------- private functions -----------------

    private fun initController() {
    }

    private fun initScene() {
        // 如果场景为空，初始化场景，否则只改变场景的边界
        if (scene == null) {
            scene = FrictionScene(GameOptions.u, Border(0, 0, width, height)).apply {
                shouldCheckObjectReachEdge = true
            }
        } else scene!!.setBorder(0, 0, width, height)

        controller?.let {
            // 初始化场景
            if (it.scene == null) it.scene = scene
            // 如果游戏没有开始，那么初始化主机，并添加到场景中
            if (it.state == GameController.STATE_READY) {
                it.start()
            } else {
                it.setMainAircraftPosition(width / 2, height - GameOptions.mainAircraftSize * 1.2)
            }
        }
    }

    private fun initGameOptions() {
        GameOptions.mainAircraftSize = height.toFloat() / 27
        GameOptions.smallEnemySize = height.toFloat() * GameOptions.smallEnemySizePercent
    }

}