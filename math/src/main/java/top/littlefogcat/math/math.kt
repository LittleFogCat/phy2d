package top.littlefogcat.math

import kotlin.math.absoluteValue
import kotlin.math.hypot
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * @Author：littlefogcat
 * @Email：littlefogcat@foxmail.com
 */

/**
 * 是否是奇数
 */
val Int.isOdd get() = and(1) == 1

/**
 * 是否是偶数
 */
val Int.isEven get() = and(1) == 0

/**
 * 平方 square
 */
val Float.sq: Float get() = this * this

/**
 * 判断浮点数是否是0；如果绝对值小于等于[accuracy]，就判断为0
 */
fun Float.isZero(accuracy: Float = 0.00001f): Boolean {
    return absoluteValue <= accuracy
}

fun min(vararg nums: Float): Float {
    var min = nums[0]
    for (i in 1 until nums.size) {
        if (nums[i] < min) min = nums[i]
    }
    return min
}

fun max(vararg nums: Float): Float {
    var max = nums[0]
    for (i in 1 until nums.size) {
        if (nums[i] > max) max = nums[i]
    }
    return max
}

fun Float.between(val1: Float, val2: Float): Boolean {
    return this in val1..val2 || this in val2..val1
}

/**
 * 获取两个向量的交点。如果没有交点，或者平行，返回null
 */
fun getIntersection(v1: FixedVector, v2: FixedVector): Point? {
    // 先用直线解方程
    // 直线表示形式：ax + by + c = 0
    // 对于两个点，(x1, y1) (x2, y2)，有
    // y1 = ax1 + b
    // y2 = ax2 + b
    // a = (y2 - y1) / (x2 - x1)
    // b = y1 - ax1
    // 特别的，当x1 == x2，直线为 x = x1

    // 计算出两个向量的border
    val left1 = kotlin.math.min(v1.x1, v1.x2)
    val top1 = kotlin.math.min(v1.y1, v1.y2)
    val right1 = kotlin.math.max(v1.x1, v1.x2)
    val bottom1 = kotlin.math.max(v1.y1, v1.y2)
    val left2 = kotlin.math.min(v2.x1, v2.x2)
    val top2 = kotlin.math.min(v2.y1, v2.y2)
    val right2 = kotlin.math.max(v2.x1, v2.x2)
    val bottom2 = kotlin.math.max(v2.y1, v2.y2)
    if (left1 > right2 || left2 > right1 || top1 > bottom2 || top2 > bottom1) {
        // 两个向量错位，必不相交
        return null
    }

    if (v1.x1 == v1.x2 && v2.x1 == v2.x2 || v1.y1 == v1.y2 && v2.y1 == v2.y2) {
        // 均与坐标轴平行
        return null
    }
    var a1 = 0f
    var b1 = 0f
    var a2 = 0f
    var b2 = 0f
    if (v1.x1 != v1.x2) {
        a1 = (v1.y2 - v1.y1) / (v1.x2 - v1.x1)
        b1 = v1.y1 - a1 * v1.x1
    }
    if (v2.x1 != v2.x2) {
        a2 = (v2.y2 - v2.y1) / (v2.x2 - v2.x1)
        b2 = v2.y1 - a2 * v2.x1
    }
    val x: Float // 交点横坐标
    val y: Float // 交点纵坐标
    @Suppress("CascadeIf")
    if (v1.x1 == v1.x2) {
        // vector1: x = x1, vector2: y = a2x + b2
        x = v1.x1
        y = a2 * x + b2
    } else if (v2.x1 == v2.x2) {
        // vector2: x = v2.x1, vector1: y = a1x + b1
        x = v2.x1
        y = a1 * x + b1
    } else {
        // v1: y = a1x + b1, v2: y = a2x + b2
        if (a1 == a2) return null // 平行
        x = (b2 - b1) / (a1 - a2)
        y = a1 * x + b1
    }
    // 直线有交点(x, y)
    if (x > right1 || x < left1 || x > right2 || x < left2 ||
        y > bottom1 || y < top1 || y > bottom2 || y < top2
    ) {
        return null
    }
    return Point(x, y)
}

/**
 * 返回两点间距离
 */
fun getDistance(x1: Float, y1: Float, x2: Float = 0f, y2: Float = 0f): Float {
    return sqrt((x2 - x1).pow(2) + (y2 - y1).pow(2))
}


/**
 * 将标量转换成指定方向的向量。
 * 给定方向向量[direction]和向量的模[len]，返回对应向量。
 */
fun getVectorFromScalar(direction: FreeVector, len: Float): FreeVector {
    val rx = direction.x
    val ry = direction.y
    val origLen = sqrt(rx.pow(2) + ry.pow(2))
    if (origLen == 0f) return FreeVector(0f, 0f)
    // origLen / len = rx / x
    val x = rx * len / origLen
    val y = ry * len / origLen
    return FreeVector(x, y)
}