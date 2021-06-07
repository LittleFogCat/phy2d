package top.littlefogcat.phy2d.shape

import top.littlefogcat.math.Point
import top.littlefogcat.phy2d.utils.sumByFloat

/**
 * 组合形状。
 */
class CombinedShape(val shapes: MutableList<Shape> = mutableListOf()) : Shape() {

    override var color: Int = 0
        set(value) {
            shapes.forEach {
                it.color = value
            }
            field = value
        }

    fun addShape(shape: Shape) {
        shapes.add(shape)
        parent?.let {
            shape.attachTo(parent!!, positionInParent)
        }
        updateBorder()
    }

    fun removeShape(shape: Shape) {
        shapes.remove(shape)
        updateBorder()
    }

    fun clearShape() {
        shapes.clear()
        updateBorder()
    }

    override fun containsPoint(point: Point): Boolean {
        return shapes.any {
            it.containsPoint(point)
        }
    }

    override fun updateBorder() {
        lefts.clear()
        rights.clear()
        tops.clear()
        bottoms.clear()
        if (shapes.size == 0) {
            left = Point.ZERO
            right = Point.ZERO
            bottom = Point.ZERO
            top = Point.ZERO
            return
        }
        lefts.add(shapes[0].left)
        rights.add(shapes[0].right)
        tops.add(shapes[0].top)
        bottoms.add(shapes[0].bottom)
        val left = shapes[0].left
        val right = shapes[0].right
        val top = shapes[0].top
        val bottom = shapes[0].bottom
        shapes.forEach { shape ->
            if (shape.left.x == left.x) lefts.add(shape.left)
            if (shape.left.x < left.x) {
                lefts.clear()
                lefts.add(shape.left)
            }
            if (shape.right.x == right.x) rights.add(shape.right)
            if (shape.right.x > right.x) {
                rights.clear()
                rights.add(shape.right)
            }
            if (shape.top.y <= top.y) {
                if (shape.top.y < top.y) tops.clear()
                tops.add(shape.top)
            }
            if (shape.bottom.y >= bottom.y) {
                if (shape.bottom.y > bottom.y) bottoms.clear()
                bottoms.add(shape.bottom)
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

    override fun onRotate(angle: Float) {
        allShapesDo { it.rotate(angle) }
        updateBorder()
    }

    override fun onAttached() {
        shapes.forEach { it.attachTo(parent!!, positionInParent) }
    }

    /**
     * 所有子shape做[action]
     */
    fun allShapesDo(action: (Shape) -> Unit) {
        shapes.forEach(action)
    }

    override fun toString(): String {
        return shapes.toString()
    }

    companion object {
        fun of(vararg shapes: Shape): CombinedShape {
            val cs = CombinedShape()
            for (shape in shapes) {
                cs.addShape(shape)
            }
            return cs
        }
    }
}