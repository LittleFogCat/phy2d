package top.littlefogcat.airecraftwar.game.objects

import android.graphics.Color
import top.littlefogcat.math.FreeVector
import top.littlefogcat.phy2d.base.Object
import top.littlefogcat.phy2d.shape.Polygon

/**
 * 一个子弹，六边形
 *
 * @Author：littlefogcat
 * @Email：littlefogcat@foxmail.com
 */
class Bullet(
    var length: Float,
    var width: Float,
    var damage: Int,
    velocity: FreeVector,
    maxVelocity: Float,
    angularVelocity: Float = 0f,
    bounded: Boolean = false,
    elasticity: Float = 1f,
    mass: Float = 0.1f,
    u: Float = 0f,
) : Object(mass, velocity, angularVelocity, bounded, elasticity, maxVelocity, u) {

    private var bulletShape: Polygon = Polygon( // 六边形
        0, -length / 2,
        width / 2, width / 2 - length / 2,
        width / 2, -width / 2 + length / 2,
        0, length / 2,
        -width / 2, -width / 2 + length / 2,
        -width / 2, width / 2 - length / 2,
        0, -length / 2,
    ).also { it.color = Color.RED }

    init {
        addShape(bulletShape)
    }

    private fun updateShape() {
        bulletShape.setVertexAt(0, 0, -length / 2)
        bulletShape.setVertexAt(1, width / 2, width / 2 - length / 2)
        bulletShape.setVertexAt(2, width / 2, -width / 2 + length / 2)
        bulletShape.setVertexAt(3, 0, length / 2)
        bulletShape.setVertexAt(4, -width / 2, -width / 2 + length / 2)
        bulletShape.setVertexAt(5, -width / 2, width / 2 - length / 2)
    }

}