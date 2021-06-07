package top.littlefogcat.phy2d.shape

import top.littlefogcat.phy2d.base.Object
import top.littlefogcat.math.Point
import top.littlefogcat.phy2d.utils.sumByFloat

/**
 * 物体的形状
 */
abstract class Shape {
    /**
     * 属于哪个物体，在调用[Object.addShape]之前，形状不属于任何物体。
     */
    var parent: Object? = null

    // 边界
    /**
     * 相对位置，相对于[Object.position]
     */
    var positionInParent: Point = Point.ZERO

    /**
     * 绝对位置，相对于[Object.scene]。
     * 这个是根据当前形状在对应Object中的位置和对应Object在Scene中的位置决定的。所以，
     * 在形状没有添加到物体，或者物体没有添加到场景的时候，该值为null。
     */
    val rawPosition: Point? get() = parent?.position?.plus(positionInParent)
    var left: Point = Point.ZERO
    var top: Point = Point.ZERO
    var right: Point = Point.ZERO
    var bottom: Point = Point.ZERO

    open var color: Int = -0x1000000

    open var style = Style.BORDERED

    enum class Style {
        FILLED, BORDERED
    }

    /**
     * 顶点
     */
    val vertices: MutableList<Point> = mutableListOf()

    val lefts = mutableListOf<Point>()
    val rights = mutableListOf<Point>()
    val tops = mutableListOf<Point>()
    val bottoms = mutableListOf<Point>()

    /**
     * 判断一个点是否在这个形状中。
     */
    abstract fun containsPoint(point: Point): Boolean

    /**
     * 更新矩形边界[left] [right] [top] [bottom]
     */
    open fun updateBorder() {
        lefts.clear()
        rights.clear()
        tops.clear()
        bottoms.clear()
        lefts.add(vertices[0])
        rights.add(vertices[0])
        tops.add(vertices[0])
        bottoms.add(vertices[0])
        val left = vertices[0].x
        val right = vertices[0].x
        val top = vertices[0].y
        val bottom = vertices[0].y
        vertices.forEach { vertex ->
            if (vertex.x == left) lefts.add(vertex)
            if (vertex.x < left) {
                lefts.clear()
                lefts.add(vertex)
            }
            if (vertex.x == right) rights.add(vertex)
            if (vertex.x > right) {
                rights.clear()
                rights.add(vertex)
            }
            if (vertex.y <= top) {
                if (vertex.y < top) tops.clear()
                tops.add(vertex)
            }
            if (vertex.y >= bottom) {
                if (vertex.y > bottom) bottoms.clear()
                bottoms.add(vertex)
            }
        }
        this.left.set(
            lefts.sumByFloat { it.x } / lefts.size,
            lefts.sumByFloat { it.y } / lefts.size
        )
        this.right.set(
            rights.sumByFloat { it.x } / rights.size,
            rights.sumByFloat { it.y } / rights.size
        )
        this.top.set(
            tops.sumByFloat { it.x } / tops.size,
            tops.sumByFloat { it.y } / tops.size
        )
        this.bottom.set(
            bottoms.sumByFloat { it.x } / bottoms.size,
            bottoms.sumByFloat { it.y } / bottoms.size
        )
    }

    /**
     * 旋转这个形状
     */
    fun rotate(angle: Float) {
        onRotate(angle)
    }

    open fun onRotate(angle: Float) {
    }

    fun attachTo(obj: Object, position: Point = Point.ZERO) {
        parent = obj
        positionInParent.set(position.x, position.y)
        onAttached()
    }

    fun detach() {
        parent = null
        positionInParent.set(0, 0)
        onDetached()
    }

    open fun onAttached() {}
    open fun onDetached() {}
}