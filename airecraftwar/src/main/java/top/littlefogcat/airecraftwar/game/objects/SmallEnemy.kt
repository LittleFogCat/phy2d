package top.littlefogcat.airecraftwar.game.objects

import top.littlefogcat.airecraftwar.game.GameOptions
import top.littlefogcat.math.FreeVector

/**
 * @Author：littlefogcat
 * @Email：littlefogcat@foxmail.com
 */
class SmallEnemy(
    hp: Int = 10,
    mass: Float = 10f,
    velocity: FreeVector = FreeVector(0f, GameOptions.smallEnemyVelocity),
    maxVelocity: Float = GameOptions.smallEnemyVelocity,
    bounded: Boolean = false,
    elasticity: Float = 0f,
    angularVelocity: Float = 0f,
    u: Float = 0f
) : Aircraft(hp, mass, velocity, maxVelocity, bounded, elasticity, angularVelocity, u) {
    init {
        addShape(GameOptions.smallEnemyShape)
    }
}