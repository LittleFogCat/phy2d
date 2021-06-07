package top.littlefogcat.elusion.controller

import top.littlefogcat.common.animation.AbstractInterpolator
import top.littlefogcat.common.animation.Speed
import top.littlefogcat.math.FreeVector

/**
 * 有向加速插值器。
 *
 * 通过[acceleration]指定加速度，通过[direction]指定加速方向，通过[maxSpeed]指定最大速度。
 *
 * @Author：littlefogcat
 * @Email：littlefogcat@foxmail.com
 */
class DirectedAccelerateInterpolator(
    speedX: Float = 0f,
    speedY: Float = 0f,
    var acceleration: Float = 0f,
    var direction: FreeVector = FreeVector(0f, 0f),
    var maxSpeed: Float = 0f,
) : AbstractInterpolator(speedX, speedY) {
    override fun next(): Speed {
        // 加速度太小，忽略
        if (acceleration < 0.0001 && acceleration > -0.0001) return speed
        // 目标速度
        val targetSpeed = FreeVector(maxSpeed, direction)
        // 向量：当前速度 -> 目标速度
        val vector = FreeVector(targetSpeed.x - speed.x, targetSpeed.y - speed.y)
        // 速度变化向量
        val deltaVelocity = FreeVector(acceleration, vector)
        if (speed.x < targetSpeed.x && speed.x + deltaVelocity.x > targetSpeed.x || speed.x > targetSpeed.x && speed.x + deltaVelocity.x < targetSpeed.x) {
            speed.x = targetSpeed.x
        } else speed.x += deltaVelocity.x
        if (speed.y < targetSpeed.y && speed.y + deltaVelocity.y > targetSpeed.y || speed.y > targetSpeed.y && speed.y + deltaVelocity.y < targetSpeed.y) {
            speed.y = targetSpeed.y
        } else speed.y += deltaVelocity.y
        return speed
    }
}