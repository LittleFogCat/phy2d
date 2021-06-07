package top.littlefogcat.math

/**
 * 坐标点
 */
open class Point(x: Number, y: Number) : FreeVector(x.toFloat(), y.toFloat()) {

    fun set(x: Number, y: Number) {
        this.x = x.toFloat()
        this.y = y.toFloat()
    }

    fun set(other: FreeVector) {
        x = other.x
        y = other.y
    }

    override operator fun plus(vector: FreeVector): Point {
        return Point(x + vector.x, y + vector.y)
    }

    override operator fun minus(vector: FreeVector): Point {
        return Point(x - vector.x, y - vector.y)
    }

    override fun toString(): String {
        return "($x, $y)"
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Point) return false
        return (other.x == x && other.y == y)
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        return result
    }

    companion object {
        val ZERO get() = Point(0, 0)
    }
}