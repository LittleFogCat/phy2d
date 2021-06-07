package top.littlefogcat.phy2d.base

import android.util.Log
import top.littlefogcat.math.FreeVector
import top.littlefogcat.math.Point
import top.littlefogcat.math.getVectorFromScalar
import top.littlefogcat.math.sq
import top.littlefogcat.phy2d.getProjectionValue
import top.littlefogcat.phy2d.getRotateEnergy
import top.littlefogcat.phy2d.getRotateInertia
import top.littlefogcat.phy2d.scene.Scene
import top.littlefogcat.phy2d.shape.CombinedShape
import top.littlefogcat.phy2d.shape.Shape
import top.littlefogcat.phy2d.x

/**
 * 一个二维物体
 *
 * [mass] 质量
 * [position] position
 * [velocity] 速度
 * [angularVelocity] 角速度，以顺时针为正方向
 * [force] 受力
 *
 * @Author：littlefogcat
 * @Email：littlefogcat@foxmail.com
 */
open class Object(
    var mass: Float,
    var velocity: FreeVector = FreeVector(),
    var angularVelocity: Float = 0f, // 角速度
    var bounded: Boolean = true, // 是否受到边界束缚
    var elasticity: Float = 1f, // 弹性为1时，完全弹性碰撞
    var maxVelocity: Float = Float.MAX_VALUE, // 人为限制最大速度
    var u: Float = 1f,
    initShape: Shape? = null,
    open var name: String = ""
) {
    init {
        if (initShape != null) addShape(initShape)
    }

    val invMass by lazy { 1f / mass }

    /**
     * 物体的受力
     */
    var force: CombinedForce = CombinedForce()

    /**
     * 加速度
     * a = F / m
     */
    val accelerate: FreeVector get() = force / mass

    /**
     * 动量
     * p = mv
     */
    val momentum: FreeVector get() = velocity * mass

    /**
     * 动能
     * Ek = 1/2 mv^2
     */
    val kineticEnergy: Float get() = mass * velocity.absoluteValue.sq / 2

    /**
     * 转动惯量
     */
    val inertia: Float get() = getRotateInertia(mass, radius)

    /**
     * 1 / 转动惯量
     */
    val invInertia by lazy { 1f / inertia }

    /**
     * 旋转动能
     */
    val rotateEnergy: Float get() = getRotateEnergy(inertia, angularVelocity)

    /**
     * 重写[identify]以指定物体特定的标识符。
     * 在使用[Scene.addObject]时会用到。
     */
    open val identify: Any get() = hashCode()

    /**
     * 这个物体在场景中的位置。
     * Position of this object.
     */
    lateinit var position: Point

    /**
     * 物体的重心，这个位置是对于[position]的相对位置。
     */
    var gravityCenter: Point = Point.ZERO

    /**
     * 这个物体所处的场景
     */
    var scene: Scene? = null

    /**
     * 这个物体的形状，可以是多个[Shape]迭加起来的组合形状。使用[addShape]来为物体添加形状，[removeShape]删除形状。
     *
     * [shape]默认是[CombinedShape]类型的对象。如果重写[shape]，且类型不为[CombinedShape]，那么必须也重写
     * [addShape]、[removeShape]、[removeAllShapes]函数。
     *
     * @see SimpleShapeObject
     * @see CircleObject
     * @see PolygonObject
     */
    open val shape: Shape = CombinedShape().also { it.attachTo(this) }

    /**
     * left = position.x + shape.left
     */
    val left: Point get() = position + shape.left
    val top: Point get() = position + shape.top
    val right: Point get() = position + shape.right
    val bottom: Point get() = position + shape.bottom

    /**
     * 旋转半径
     */
    var radius = 0f

    var onLeaveSceneListener: ((Scene, Object) -> Unit)? = null

    // ---------------------------------- public functions ---------------------------------------

    /**
     * 给物体施加力[F]
     */
    fun giveForce(F: Force) {
        force.add(F)
    }

    /**
     * 移除物体上的力[f]
     */
    fun removeForce(f: Force) {
        force.remove(f)
    }

    /**
     * 移除物体的所有受力
     */
    fun clearForce() {
        force.clear()
    }

    /**
     * 在点[atPoint]给予物体一个冲量[impulse]，这个点坐标是相对于物体重心[gravityCenter]的。
     *
     * 由于状态过于复杂，这里做近似处理，将冲量分解成指向重心和垂直于重心的两个分量。
     */
    fun giveImpulse(impulse: FreeVector, atPoint: Point) {
        Log.d(TAG, "giveImpulse: $this impulse = $impulse at $atPoint")
        Log.d(TAG, "giveImpulse: position = $position, atPoint = $atPoint")
        Log.d(TAG, "giveImpulse: 初始速度$velocity, impulse = $impulse, at $atPoint")
        val vectorToCenter = FreeVector(atPoint, gravityCenter) // 从接触点到重心连线
        val vectorVertical = FreeVector(vectorToCenter.y, -vectorToCenter.x) // 垂直方向
        val impulseToCenterValue = getProjectionValue(impulse, vectorToCenter) // 指向重心的冲量：平移冲量
        val impulseVerticalValue = getProjectionValue(impulse, vectorVertical) // 指向垂直方向的冲量：旋转冲量
        Log.v(TAG, "giveImpulse: 指向重心：$vectorToCenter")
        Log.v(TAG, "giveImpulse: 指向垂直：$vectorVertical")
        Log.v(TAG, "giveImpulse: 指向重心value：$impulseToCenterValue")
        Log.v(TAG, "giveImpulse: 指向垂直value：$impulseVerticalValue")

        val deltaVelocityValue = impulseToCenterValue / mass // △v = I / m 变化速度标量
        val deltaVelocity = getVectorFromScalar(vectorToCenter, deltaVelocityValue) // 变化速度向量

        val absoluteDeltaAngularVelocityValue = impulseVerticalValue / mass // 角速度变化的绝对值
        // 如果向量[impulse]在向量[vectorToCenter]的左侧，那么角速度变化为正，否则为负
        val cp = vectorToCenter.x(impulse) // 叉积
        val deltaAngularVelocityValue = if (cp > 0) { // 角速度变化值
            absoluteDeltaAngularVelocityValue // 向量[impulse]在向量[vectorToCenter]的左侧，角速度变化为正
        } else if (cp < 0f) -absoluteDeltaAngularVelocityValue // 角速度变化为负
        else 0f // 向量[impulse]向量[vectorToCenter]的同线，角速度不变

        velocity += deltaVelocity
        angularVelocity += deltaAngularVelocityValue

        Log.d(TAG, "giveImpulse: $this 最终速度$velocity, impulse = $impulse, at $atPoint")
    }

    /**
     * 将[shape]添加到物体中的相对位置[position]。
     */
    open fun addShape(shape: Shape, position: Point = Point.ZERO) {
        (this.shape as CombinedShape).addShape(shape)
        shape.attachTo(this, position)
    }

    open fun removeShape(shape: Shape) {
        (this.shape as CombinedShape).removeShape(shape)
        shape.detach()
    }

    open fun removeAllShapes() {
        (this.shape as CombinedShape).clearShape()
    }

    internal open fun detach() {
        scene = null
    }

    open fun onTimerTick() {
    }

    open fun onLeaveScene(scene: Scene) {
        onLeaveSceneListener?.invoke(scene, this)
    }

    /**
     * 将这个物体与一个场景[Scene]绑定。
     */
    internal fun attachTo(scene: Scene, point: Point) {
        this.scene = scene
        this.position = point
    }

    override fun toString(): String {
        return "$name / $identify"
    }

    fun setBottom(value: Number) {
        val b = value.toFloat()
        position.y  = position.y + b - bottom.y
    }

    companion object {
        const val TAG = "Object"
    }
}