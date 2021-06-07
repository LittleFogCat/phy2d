package top.littlefogcat.common.animation

import kotlin.math.pow
import kotlin.math.sqrt

abstract class AbstractInterpolator(
    initSpeed: Speed, // 初始速度
) : Interpolator {
    constructor(speedX: Float = 0f, speedY: Float = 0f) : this(Speed(speedX, speedY))

    override val speed = Speed(initSpeed.x, initSpeed.y)

    open val maxSpeedDirection = Speed(0f, 0f)

}
