package top.littlefogcat.phy2d.shape

import android.graphics.Color
import top.littlefogcat.math.Point
import kotlin.math.hypot

class Circle : Shape {
    var r: Float
        set(value) {
            field = value
            updateBorder()
        }

    constructor(r: Float, color: Int = Color.RED, style: Style = Style.FILLED) : super() {
        this.r = r
        this.color = color
        this.style = style
    }

    override fun containsPoint(point: Point): Boolean {
        if (rawPosition == null) return false // 没有添加到场景中
        return hypot(point.x - rawPosition!!.x, point.y - rawPosition!!.y) <= r
    }

    override fun updateBorder() {
        left.set(positionInParent.x - r, positionInParent.y)
        top.set(positionInParent.x, positionInParent.y - r)
        right.set(positionInParent.x + r, positionInParent.y)
        bottom.set(positionInParent.x, positionInParent.y + r)
    }

}