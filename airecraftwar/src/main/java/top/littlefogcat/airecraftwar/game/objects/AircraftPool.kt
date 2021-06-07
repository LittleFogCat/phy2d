package top.littlefogcat.airecraftwar.game.objects

import androidx.core.util.Pools

/**
 * @Author：littlefogcat
 * @Email：littlefogcat@foxmail.com
 */
class AircraftPool : Pools.SimplePool<Aircraft>(100) {
    private var size = 0
    private val smallEnemyPool = Pools.SimplePool<SmallEnemy>(20)

    fun get(level: Int): Aircraft {
        return when (level) {
            0 -> {
                val e = smallEnemyPool.acquire()
                e ?: SmallEnemy().apply {
                    name = "enemy-$size"
                    size++
                }
            }
            else -> smallEnemyPool.acquire() ?: SmallEnemy()
        }
    }

    fun recycle(aircraft: Aircraft) {
        if (aircraft is SmallEnemy) try {
            smallEnemyPool.release(aircraft)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}