package top.littlefogcat.airecraftwar.game.objects

import top.littlefogcat.math.FreeVector
import top.littlefogcat.phy2d.base.Object
import top.littlefogcat.phy2d.shape.Shape

/**
 * 代表一架战机。
 *
 * [hp] 生命值
 *
 * @Author：littlefogcat
 * @Email：littlefogcat@foxmail.com
 */
open class Aircraft(
    var hp: Int,
    mass: Float,
    velocity: FreeVector = FreeVector(),
    maxVelocity: Float,
    bounded: Boolean,
    elasticity: Float = 0f,
    angularVelocity: Float = 0f,
    u: Float = 1f,
    initShape: Shape? = null
) : Object(mass, velocity, angularVelocity, bounded, elasticity, maxVelocity, u, initShape) {
    var onExplosion: ((Aircraft) -> Unit)? = null

    /**
     * 爆炸
     */
    open fun explode() {
        onExplosion?.invoke(this)
    }
}