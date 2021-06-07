package top.littlefogcat.airecraftwar.game.objects

import androidx.core.util.Pools
import top.littlefogcat.airecraftwar.game.GameOptions
import top.littlefogcat.math.FreeVector
import java.lang.Exception

/**
 * 子弹池
 *
 * @Author：littlefogcat
 * @Email：littlefogcat@foxmail.com
 */
class BulletPool : Pools.SimplePool<Bullet>(100) {
    private var size = 0

    fun get(length: Float, width: Float, damage: Int, velocity: FreeVector, maxVelocity: Float): Bullet {
        val ac = acquire() ?: return Bullet(length, width, damage, velocity, maxVelocity).apply {
            name = "bullet-$size"
            size++
        }
        ac.length = length
        ac.width = width
        ac.damage = damage
        ac.velocity = velocity
        ac.maxVelocity = maxVelocity
        return ac
    }

    fun get(option: GameOptions.BulletOption): Bullet =
        get(option.length, option.width, option.damage, option.velocity, option.maxVelocity)

    fun recycle(bullet: Bullet) {
        try {
            release(bullet)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}