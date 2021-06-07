package top.littlefogcat.math

import kotlin.math.*

/**
 * 固定向量，以([x1], [y1])为起点，([x2], [y2])为终点。
 *
 * A fixed vector which starts at ([x1], [y1]), ends at ([x2], [y2]).
 */
open class FixedVector(var x1: Float, var y1: Float, var x2: Float, var y2: Float) {
    constructor(x1: Number, y1: Number, x2: Number, y2: Number) : this(x1.toFloat(), y1.toFloat(), x2.toFloat(), y2.toFloat())
    constructor(p1: Point, p2: Point) : this(p1.x, p1.y, p2.x, p2.y)

    val module: Float get() = hypot((x2 - x1), (y2 - y1))

    fun toFreeVector(): FreeVector {
        return FreeVector(x2 - x1, y2 - y1)
    }

    /**
     * 这个向量是否与向量[other]相交。
     * 向量的起点视为实点，终点视为虚点。
     */
    fun isIntersectWith(other: FixedVector): Boolean {
        if (isParallelWith(other)) return false
        val r1 = atWhichSide(other.x1, other.y1) // other的起点是否在this右侧
        val r2 = atWhichSide(other.x2, other.y2) // other的终点是否在this右侧
        // other起点在右侧，终点不在右侧；或者起点在左侧，终点不在左侧
        if (r1 > 0 && r2 >= 0 || r1 < 0 && r2 <= 0) return false
        val r3 = other.atWhichSide(x1, y1) // this的起点是否在other右侧
        val r4 = other.atWhichSide(x2, y2) // this的起点是否在other右侧
        if (r3 > 0 && r4 >= 0 || r3 < 0 && r4 <= 0) return false; // 同侧
        return true // this起始点在other两侧、other起始点在this两侧，说明相交
    }

    /**
     * 是否与[other]平行
     */
    fun isParallelWith(other: FixedVector): Boolean {
        return (x2 - x1) * (other.y2 - other.y1) == (y2 - y1) * (other.x2 - other.x1)
    }

    /**
     * 判断点在向量哪一侧，使用行列式计算。
     *
     * 如果大于0，说明点在右侧；
     * 如果等于0，说明点在向量所在直线上；
     * 如果小于0，说明点在向量左侧。
     */
    fun atWhichSide(x3: Float, y3: Float): Float {
        return ((x2 - x1) * (y3 - y1) - (x3 - x1) * (y2 - y1))
    }

    /**
     * 判断点[p]是否落在向量上
     */
    fun containsPoint(p: Point): Boolean {
        return p.x <= max(x1, x2) && p.x >= min(x1, x2) && p.y >= min(y1, y2) && p.y <= max(y1, y2)
                && atWhichSide(p.x, p.y) == 0f
    }

    override fun toString(): String {
        return "[$x1, $y1]->[$x2, $y2]"
    }

}