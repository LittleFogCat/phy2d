package top.littlefogcat.common.animation

import kotlin.math.pow
import kotlin.math.sqrt

class Speed internal constructor(
    var x: Float,
    var y: Float
) {
    override fun equals(other: Any?): Boolean {
        if (other !is Speed) return false
        return x == other.x && y == other.y
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        return result
    }

    fun getAbsoluteSpeed(): Float {
        return sqrt(x.pow(2) + y.pow(2))
    }

    override fun toString(): String {
        return "($x, $y)"
    }

}

fun Speed.toArray(): FloatArray {
    return floatArrayOf(x, y)
}

fun FloatArray.toSpeed(): Speed {
    return Speed(this[0], this[1])
}