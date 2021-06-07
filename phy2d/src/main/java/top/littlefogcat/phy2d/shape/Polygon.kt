package top.littlefogcat.phy2d.shape

import top.littlefogcat.math.FixedVector
import top.littlefogcat.math.Point
import top.littlefogcat.math.getIntersection
import top.littlefogcat.math.isOdd
import top.littlefogcat.phy2d.degreeToRad
import top.littlefogcat.phy2d.utils.*
import kotlin.math.cos
import kotlin.math.sin

/**
 * 一个多边形。
 * A polygon.
 *
 * [vertices] 这个多边形的顶点。注意必须按照顺时针的方向存放。
 * [vertices] the vertexes of the polygon.
 */
open class Polygon : Shape {
    val sides: MutableList<FixedVector> by lazy {
        MutableList(vertices.size) { index ->
            val start = vertices[index]
            val end = vertices[if (index == vertices.size - 1) 0 else index + 1]
            return@MutableList FixedVector(start.x, start.y, end.x, end.y)
        }
    }

    constructor(vararg points: Number) {
        for (i in points.indices step 2) {
            vertices.add(Point(points[i].toFloat(), points[i + 1].toFloat()))
        }
        updateBorder()
    }

    constructor(points: List<Point>) {
        this.vertices.addAll(points)
        updateBorder()
    }

    /**
     * 用射线法判断点是否在多边形内。
     *
     * 从点[point]任意做一条射线。如果与多边形的边（不包括顶点）交点为奇数个，说明点在多边形内；如果是偶数个，则在多边形外。
     */
    override fun containsPoint(point: Point): Boolean {
        if (parent == null || parent!!.scene == null || rawPosition == null) return false // 没有添加到场景中

        // 首先做简单的判断
        // 不在边界内，则返回false
        if (point.x > rawPosition!!.x + right.x ||
            point.x < rawPosition!!.x + left.x ||
            point.y < rawPosition!!.y + top.y ||
            point.y > rawPosition!!.y + bottom.y
        ) return false

        // 做一条以point为起点，平行于x轴，正方向的射线
        val ray = FixedVector(point, Point(1000000000f, point.y))
        val ray2 = FixedVector(point, Point(-1000000000f, point.y))
        var count = 0 // 交点个数，为奇数则点在内部
        var count2 = 0 // 交点个数，为奇数则点在内部
        // 遍历顶点用到的
        var last: Point? // 上一个顶点
        var current: Point? = null // 当前遍历到的顶点
        var next: Point? = null // 下一个即将遍历的顶点
        var sideBefore: FixedVector?
        var sideAfter: FixedVector? = null
        for (i in 0 until vertices.size) {
            last = current ?: getVertexRawPosition(if (i == 0) vertices.size - 1 else i - 1)!!
            current = next ?: getVertexRawPosition(i)!! // 当前点
            next = getVertexRawPosition(if (i == vertices.size - 1) 0 else i + 1)!!

            sideBefore = sideAfter ?: FixedVector(last, current) // 当前顶点之前的边
            sideAfter = FixedVector(current, next) // 当前顶点之后的点
            if (sideBefore.containsPoint(point) || sideAfter.containsPoint(point)) return true // 在边上，直接返回true

            //            else if (ray.isIntersectWith(side1)) {
//                val whichSide = side1.atWhichSide(point.x, point.y)
//                if (whichSide > 0) { // point在side右侧
//                    count++
//                } else if (whichSide < 0) {
//                    count--
//                }
//            }

            fun checkRayIntersection(ray: FixedVector): Boolean {
                val iPoint = getIntersection(ray, sideBefore) // 射线和边的交点
//                log("$ray 与 $sideBefore 相交: $iPoint   current: $current")
                if (iPoint != null) { // 和sideBefore有交点
                    log(1)
                    if (iPoint == last) return false // 相交在上一个顶点，跳过
                    if (iPoint == current) { // 相交在顶点
                        // 检查前后顶点是否在同一面，是的话就不算交点，否则交点数+1
                        val lastValidVertex: Point = last
                        // 下个有效的顶点，如果下条边平行于射线，则顺延之
                        val nextValidVertex: Point = if (next.y == current.y) { // 如果下一条边平行于x轴
                            getVertexRawPosition((i + 2) % vertices.size)!! // 下下个顶点，也就是下下条边的终点
                        } else next // 下个顶点
                        log("nextValidVertex: $nextValidVertex")
                        if (lastValidVertex.y > point.y && nextValidVertex.y < point.y ||
                            lastValidVertex.y < point.y && nextValidVertex.y > point.y
                        ) {
                            log(2)
                            return true
                        }
                        log(5)
                    } else { // 相交在边
                        log(3)
                        return true
                    }
                }
                log(4)
                return false
            }

            if (checkRayIntersection(ray)) count++
            if (checkRayIntersection(ray2)) count2++
//            val iPoint = getIntersection(ray, sideBefore) // 射线和边的交点
//            if (iPoint != null) { // 和sideBefore有交点
//                if (iPoint == current) { // 相交在顶点
//                    // 检查前后顶点是否在同一面，是的话就不算交点，否则交点数+1
//                    val lastValidVertex: Point = current
//                    // 下个有效的顶点，如果下条边平行于射线，则顺延之
//                    val nextValidVertex: Point = if (next.y == current.y) { // 如果下一条边平行于x轴
//                        vertices[(i + 2) % vertices.size] // 下下个顶点，也就是下下条边的终点
//                    } else next // 下个顶点
//                    if (lastValidVertex.y > point.y && nextValidVertex.y < point.y ||
//                            lastValidVertex.y < point.y && nextValidVertex.y > point.y) {
//                        count++
//                    }
//                } else { // 相交在边
//                    count++
//                }
//            }
//            val iPoint2 = getIntersection(ray2, sideBefore) // 射线和边的交点
//            if (iPoint2 != null) { // 和sideBefore有交点
//                if (iPoint2 == current) { // 相交在顶点
//                    // 检查前后顶点是否在同一面，是的话就不算交点，否则交点数+1
//                    val lastValidVertex: Point = current
//                    // 下个有效的顶点，如果下条边平行于射线，则顺延之
//                    val nextValidVertex: Point = if (next.y == current.y) { // 如果下一条边平行于x轴
//                        vertices[(i + 2) % vertices.size] // 下下个顶点，也就是下下条边的终点
//                    } else next // 下个顶点
//                    if (lastValidVertex.y > point.y && nextValidVertex.y < point.y ||
//                            lastValidVertex.y < point.y && nextValidVertex.y > point.y) {
//                        count2++
//                    }
//                } else {
//                    count2++
//                }
//            }
        }
        log("$count $count2")
        return count.isOdd && count2.isOdd
    }

    fun setVertexAt(i: Int, x: Number, y: Number) {
        vertices[i].set(x, y)
    }

    fun forEachVertex(action: (Point) -> Unit) {
        vertices.forEach(action)
    }

    fun forEachIndexVertex(action: (Int, Point) -> Unit) {
        vertices.forEachIndexed(action)
    }

    fun getVertexSize(): Int {
        return vertices.size
    }

    /**
     * 获取顶点的绝对坐标
     */
    fun getVertexRawPosition(index: Int): Point? {
        if (rawPosition == null) return null
        return vertices[index] + rawPosition!!
    }

    /**
     * 如果边界上有多个顶点，那么就取平均值
     */
    final override fun updateBorder() {
        val lefts = mutableListOf(vertices[0])
        val rights = mutableListOf(vertices[0])
        val tops = mutableListOf(vertices[0])
        val bottoms = mutableListOf(vertices[0])
        vertices.forEach { vertex ->
            if (vertex.x < left.x) left = vertex
            if (vertex.x > right.x) right = vertex
            if (vertex.y < top.y) top = vertex
            if (vertex.y > bottom.y) bottom = vertex
        }
    }

    /**
     *
     * @see Formulas.rotatePoint
     */
    override fun onRotate(angle: Float) {
        val rad = degreeToRad(angle)
        val cos = cos(rad)
        val sin = sin(rad)
        var x: Float
        var y: Float
        vertices.forEach {
            x = cos * it.x - sin * it.y
            y = cos * it.y + sin * it.x
            it.x = x.roundIfCan()
            it.y = y.roundIfCan()
        }
        updateBorder()
    }

}