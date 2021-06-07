package top.littlefogcat.common.animation

/**
 * @Author：littlefogcat
 * @Email：littlefogcat@foxmail.com
 */
class LinearInterpolator: AbstractInterpolator {
    constructor(speedX: Float, speedY: Float) : super(speedX, speedY)

    override fun next(): Speed {
        return speed
    }
}