package top.littlefogcat.common.animation

import kotlin.random.Random

class RandomAccelerateInterpolator(
    speedX: Float,
    speedY: Float,
    val maxSpeed: Float,
    val maxAcceleration: Float
) : AbstractInterpolator(speedX, speedY) {
    private val random = Random.Default

    override fun next(): Speed {
        val nextXSpeed = speed.x + random.nextFloat(-maxAcceleration, maxAcceleration)
        speed.x = when {
            nextXSpeed < -maxSpeed -> -maxSpeed
            nextXSpeed > maxSpeed -> maxSpeed
            else -> nextXSpeed
        }

        val nextYSpeed = speed.y + random.nextFloat(-maxAcceleration, maxAcceleration)
        speed.y = when {
            nextYSpeed < -maxSpeed -> -maxSpeed
            nextYSpeed > maxSpeed -> maxSpeed
            else -> nextYSpeed
        }

        return speed
    }
}
