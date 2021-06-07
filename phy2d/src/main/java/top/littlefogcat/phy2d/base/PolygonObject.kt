package top.littlefogcat.phy2d.base

import top.littlefogcat.math.FreeVector
import top.littlefogcat.math.Point
import top.littlefogcat.phy2d.shape.Polygon
import top.littlefogcat.phy2d.shape.Shape

/**
 * @Author：littlefogcat
 * @Email：littlefogcat@foxmail.com
 */
class PolygonObject(
    mass: Float,
    vertices: List<Point>,
    velocity: FreeVector = FreeVector(),
    angularVelocity: Float = 0f,
    bounded: Boolean = true,
    elasticity: Float = 1f,
    maxVelocity: Float = Float.MAX_VALUE,
    u: Float = 1f,
    initShape: Shape? = null,
    name: String = ""
) : SimpleShapeObject(mass, velocity, angularVelocity, bounded, elasticity, maxVelocity, u, initShape, name) {
    override val shape = Polygon(vertices)
    val vertices = shape.vertices // 直接引用shape的vertices

}