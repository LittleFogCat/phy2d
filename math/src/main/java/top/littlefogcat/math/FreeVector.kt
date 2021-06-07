package top.littlefogcat.math

import kotlin.math.hypot

/**
 * 自由向量
 * A free vector which can be expressed as ([x], [y]).
 *
 * @Author：littlefogcat
 * @Email：littlefogcat@foxmail.com
 */
open class FreeVector(open var x: Float = 0f, open var y: Float = 0f, var name: String = "") {

    constructor(x: Number, y: Number) : this(x.toFloat(), y.toFloat())
    constructor(other: FreeVector) : this(other.x, other.y)

    constructor(absValue: Float, other: FreeVector) : this() {
        setValue(absValue, other)
    }

    constructor(from: FreeVector, to: FreeVector) : this(to.x - from.x, to.y - from.y)

    /**
     * 模、绝对值，同一个意思
     */
    val absoluteValue: Float get() = hypot(x, y)

    fun setValue(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    fun setValue(x: Number, y: Number) {
        this.x = x.toFloat()
        this.y = y.toFloat()
    }

    /**
     * 设置绝对值
     */
    fun setValue(newValue: Float) {
        val oldValue = absoluteValue
        if (oldValue.isZero()) return
        x = x * newValue / oldValue
        y = y * newValue / oldValue
    }

    /**
     * 设置绝对值
     */
    fun setValue(newValue: FreeVector) {
        x = newValue.x
        y = newValue.y
    }

    /**
     * 根据模的大小[absoluteValue]和方向[direction]，设置[x]与[y]
     */
    fun setValue(absoluteValue: Float, direction: FreeVector) {
        val directionAbsVal = direction.absoluteValue
        if (directionAbsVal.isZero()) {
            x = 0f
            y = 0f
        } else {
            x = direction.x * absoluteValue / directionAbsVal
            y = direction.y * absoluteValue / directionAbsVal
        }
    }

    /**
     * 向量的加法
     */
    open operator fun plus(other: FreeVector): FreeVector {
        val c = clone()
        c.x += other.x
        c.y += other.y
        return c
    }

    /**
     * 向量的减法
     */
    open operator fun minus(other: FreeVector): FreeVector {
        val c = clone()
        c.x -= other.x
        c.y -= other.y
        return c
    }

    /**
     * 向量 * 数
     */
    open operator fun times(float: Float): FreeVector {
        val c = clone()
        c.x *= float
        c.y *= float
        return c
    }

    /**
     * 向量的点乘
     */
    open operator fun times(other: FreeVector): Float {
        return x * other.x + y * other.y
    }

    /**
     * 向量的叉乘，结果为一标量。符号代表其方向，为正则代表指向平面内侧，否则指向平面外侧。
     *
     *
     * 可以用于判断向量的方向，如果结果为正，那么[other]在[this]的左侧；
     * 如果结果为0，[other]和[this]在一条直线上；
     * 如果结果为负，[other]在[this]的右侧。
     */
    @Suppress("KDocUnresolvedReference")
    open fun cross(other: FreeVector): Float {
        return x * other.y - other.x * y
    }

    /**
     * 表示向量的叉乘，其中[other]为垂直于平面的向量，其中为正则代表指向平面内侧，否则指向平面外侧。
     * 其实质上为三维向量[[0, 0, other]]。
     *
     * 结果返回一个平面内的向量。
     */
    open fun cross(other: Float): FreeVector {
        return FreeVector(y * other, -x * other)
    }

    /**
     * 向量 / 数
     */
    open operator fun div(float: Float): FreeVector {
        val c = clone()
        c.x /= float
        c.y /= float
        return c
    }

    /**
     * 数 * 向量
     */
    open operator fun Float.times(v: FreeVector): FreeVector {
        return v * this
    }

    /**
     * 方向相反，大小相同的向量
     */
    open operator fun unaryMinus(): FreeVector {
        return FreeVector(-x, -y)
    }

    open fun clone(): FreeVector {
        return FreeVector(x, y)
    }

    override fun toString(): String {
        return "[${if (name != "") "$name: " else ""}$x, $y]"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FreeVector) return false

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }

    companion object {
        /**
         * 根据方向[directionality]和绝对值[value]生成对应的向量
         */
        fun of(directionVector: FreeVector, value: Float): FreeVector {
            return getVectorFromScalar(directionVector, value)
        }
    }

}