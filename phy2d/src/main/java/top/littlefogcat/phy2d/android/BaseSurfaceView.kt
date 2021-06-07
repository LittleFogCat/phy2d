package top.littlefogcat.phy2d.android

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.*
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import top.littlefogcat.math.FreeVector
import top.littlefogcat.math.Point
import top.littlefogcat.phy2d.base.Object
import top.littlefogcat.phy2d.scene.Scene
import top.littlefogcat.phy2d.shape.Circle
import top.littlefogcat.phy2d.shape.CombinedShape
import top.littlefogcat.phy2d.shape.Polygon
import top.littlefogcat.phy2d.shape.Shape
import java.lang.IllegalStateException
import java.lang.ref.WeakReference

/**
 * @Author：littlefogcat
 * @Email：littlefogcat@foxmail.com
 */
abstract class BaseSurfaceView : SurfaceView, SurfaceHolder.Callback {
    companion object {
        const val MSG_DRAW = 10001
        const val TAG = "BaseSurfaceView"
    }

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        @Suppress("LeakingThis")
        holder.addCallback(this)
        paint.isAntiAlias = true
    }

    /**
     * 场景，所有的物体[Object]都包括在其中。
     */
    var scene: Scene? = null
    val paint = Paint()
    protected val path = Path()
    private var thread: HandlerThread? = null
    private var handler: SurfaceHandler? = null

    /**
     * 帧率
     */
    private var frameRate = 60

    /**
     * 往场景中添加物体
     */
    fun addObject(obj: Object, pos: Point) {
        scene?.addObject(obj, pos)
    }

    // --------------------------------------------------------------
    // ------------------------ Callbacks ---------------------------
    // --------------------------------------------------------------

    override fun surfaceCreated(holder: SurfaceHolder) {
        thread = HandlerThread(this::class.java.simpleName).also {
            it.start()
            handler = SurfaceHandler(it.looper, this, frameRate).apply {
                sendEmptyMessage(MSG_DRAW)
            }
        }
        onSurfaceCreated(holder)
    }

    /**
     * Surface创建完毕回调
     */
    open fun onSurfaceCreated(holder: SurfaceHolder) {}

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        onSurfaceChanged(holder, format, width, height)
    }

    /**
     * Surface改变回调。当窗口初次创建，或者横竖屏切换的时候，会调用。
     * 可以在此回调中创建[Scene]。
     */
    open fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        if (scene == null) {
            scene = onCreateScene(holder, format, width, height)
        } else {
            scene?.setBorder(0, 0, width, height)
        }
    }

    abstract fun onCreateScene(holder: SurfaceHolder, format: Int, width: Int, height: Int): Scene

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        thread?.interrupt()
        handler?.removeMessages(MSG_DRAW)
        holder.removeCallback(this)
    }


    /**
     * Surface销毁回调
     */
    open fun onSurfaceDestroyed(holder: SurfaceHolder) {
    }

    // --------------------------------------------------------------
    // --------------------- Draw and Refresh -----------------------
    // --------------------------------------------------------------

    /**
     * 绘制[scene]
     *
     * 一帧绘制流程：
     * [Scene.timerTick] ->
     * [preDraw] ->
     * [onDraw] ->
     * [afterDraw] ->
     * [onSurfaceUpdated]
     */
    @SuppressLint("WrongCall")
    open fun draw() {
        val canvas = holder.lockCanvas() ?: return
        preDraw(canvas)
        try {
            scene?.objects?.values?.forEach { obj ->
                onDraw(canvas, obj)
            }
        } catch (e: Exception) {
            e.printStackTrace() // TODO: 2021/5/11 ConcurrentModificationException
        }
        afterDraw(canvas)
        holder.unlockCanvasAndPost(canvas)
    }

    /**
     * 在开始绘制[scene]之前调用。
     */
    open fun preDraw(canvas: Canvas) {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
    }

    /**
     * 默认绘制纯色形状。如果要改变绘制物体的方式，重写此方法
     */
    open fun onDraw(canvas: Canvas, obj: Object) {
        val shape = obj.shape
        drawShape(canvas, shape)
    }

    private fun drawShape(canvas: Canvas, shape: Shape) {
        if (shape is CombinedShape) {
            shape.shapes.forEach { drawShape(canvas, it) }
            return
        }
        paint.color = shape.color
        paint.style = when (shape.style) {
            Shape.Style.BORDERED -> Paint.Style.STROKE
            Shape.Style.FILLED -> Paint.Style.FILL
        }
        if (shape is Circle) {
            // 圆心
            val o = shape.rawPosition ?: throw IllegalStateException("Shape $shape's rawPosition is null, supposed not.")
            canvas.drawCircle(o.x, o.y, shape.r, paint)
        } else if (shape is Polygon) {
            path.reset()
            for (index in 0 until shape.getVertexSize()) {
                val pos = shape.getVertexRawPosition(index)
                    ?: throw IllegalStateException("Shape $shape's rawPosition is null, supposed not.")
                if (index == 0) path.moveTo(pos.x, pos.y)
                else path.lineTo(pos.x, pos.y)
            }
            canvas.drawPath(path, paint)
        }
    }

    /**
     * 在绘制[scene]完毕之后调用
     */
    open fun afterDraw(canvas: Canvas) {}

    /**
     * 处理绘制刷新的Handler
     */
    open class SurfaceHandler(looper: Looper, view: BaseSurfaceView, frameRate: Int) : Handler(looper) {
        private val viewRef = WeakReference(view)
        var delay = 1000L / frameRate

        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_DRAW -> {
                    val now = SystemClock.uptimeMillis()
                    val nextRunningTime = now + delay // 预先计算下一帧时间，保证帧率
                    val view = viewRef.get() ?: return
                    view.scene?.timerTick() // 计算当前帧
                    view.draw() // 绘制
                    view.onSurfaceUpdated()
                    sendEmptyMessageAtTime(MSG_DRAW, nextRunningTime)
                }
            }
        }

    }

    /**
     * 设置帧率
     */
    @Suppress("unused")
    fun setFrameRate(rate: Int) {
        handler?.delay = 1000L / rate
    }

    /**
     * 整个帧的绘制流程完毕之后，调用这个回调。
     */
    open fun onSurfaceUpdated() {
    }

    // --------------------------------------------------------------
    // ------------------------- Utility ----------------------------
    // --------------------------------------------------------------

    /**
     * 创建一个圆形物体，并添加到场景中
     *
     * [r] - 半径
     * [color] - 颜色
     * [x] - x坐标（相对于scene）
     * [y] - y坐标（相对于scene）
     * [maxVelocity] - 最大速度
     * [vx] - x方向初速度
     * [vy] - y方向初速度
     * [mass] - 质量
     * [bounded] - 是否受到边界束缚（回弹）
     * [elasticity] - 弹性，应小于等于1
     *
     * @return 创建的物体
     */
    fun createCircle(
        r: Float,
        color: Int,
        x: Float,
        y: Float,
        maxVelocity: Float,
        vx: Float,
        vy: Float,
        mass: Float,
        bounded: Boolean,
        elasticity: Float
    ): Object = createObject(maxVelocity, vx, vy, mass, bounded, elasticity) {
        addShape(Circle(r, color))
        addObject(this, Point(x, y))
    }

    /**
     * 创建一个多边形物体，并添加到[scene]中.
     *
     * [vertices] 顶点坐标
     * [x] - x坐标（相对于scene）
     * [y] - y坐标（相对于scene）
     * [maxVelocity] - 最大速度
     * [vx] - x方向初速度
     * [vy] - y方向初速度
     * [mass] - 质量
     * [bounded] - 是否受到边界束缚（回弹）
     * [elasticity] - 弹性，应小于等于1
     */
    fun createPolygon(
        vertices: List<Point>,
        color: Int,
        x: Float,
        y: Float,
        maxVelocity: Float,
        vx: Float,
        vy: Float,
        mass: Float,
        bounded: Boolean,
        elasticity: Float
    ): Object = createObject(maxVelocity, vx, vy, mass, bounded, elasticity) {
        addShape(Polygon(vertices).also { it.color = color })
        addObject(this, Point(x, y))
    }

    /**
     * 创建一个物体
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun createObject(
        maxVelocity: Float,
        vx: Float,
        vy: Float,
        mass: Float,
        bounded: Boolean,
        elasticity: Float,
        action: Object.() -> Unit
    ): Object {
        return Object(mass, FreeVector(vx, vy), 0f, bounded, elasticity, maxVelocity).apply(action)
    }
}