package top.littlefogcat.phy2d.base

import top.littlefogcat.math.FreeVector
import top.littlefogcat.phy2d.shape.Circle
import top.littlefogcat.phy2d.shape.Shape

/**
 * [r] 半径
 *
 * @Author：littlefogcat
 * @Email：littlefogcat@foxmail.com
 */
class CircleObject(
    var r: Float,
    color: Int,
    mass: Float,
    style: Shape.Style = Shape.Style.FILLED,
    velocity: FreeVector = FreeVector(),
    angularVelocity: Float = 0f,
    bounded: Boolean = true,
    elasticity: Float = 1f,
    maxVelocity: Float = Float.MAX_VALUE,
    u: Float = 1f,
    name: String = ""
) : SimpleShapeObject(mass, velocity, angularVelocity, bounded, elasticity, maxVelocity, u, null, name) {
    override val shape = Circle(r, color, style)

    init {
        shape.attachTo(this)
        radius = r
    }
}