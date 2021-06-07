package top.littlefogcat.phy2d.base

import top.littlefogcat.math.FreeVector
import top.littlefogcat.math.Point
import top.littlefogcat.phy2d.shape.Shape

/**
 * 简单形状的物体
 *
 * @see CircleObject
 * @see PolygonObject
 *
 * @Author：littlefogcat
 * @Email：littlefogcat@foxmail.com
 */
abstract class SimpleShapeObject(
    mass: Float,
    velocity: FreeVector = FreeVector(),
    angularVelocity: Float = 0f,
    bounded: Boolean = true,
    elasticity: Float = 1f,
    maxVelocity: Float = Float.MAX_VALUE,
    u: Float = 1f,
    initShape: Shape? = null,
    name: String = ""
) : Object(mass, velocity, angularVelocity, bounded, elasticity, maxVelocity, u, initShape, name) {

    abstract override val shape: Shape

    // ------------- 不支持增删形状 -----------------
    override fun addShape(shape: Shape, position: Point) {
        throw UnsupportedOperationException()
    }

    override fun removeShape(shape: Shape) {
        throw UnsupportedOperationException()
    }

    override fun removeAllShapes() {
        throw UnsupportedOperationException()
    }
}