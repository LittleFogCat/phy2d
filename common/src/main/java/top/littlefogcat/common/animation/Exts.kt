package top.littlefogcat.common.animation

import kotlin.random.Random

/**
 * @Author：littlefogcat
 * @Email：littlefogcat@foxmail.com
 */

fun Random.nextFloat(from: Float, until: Float): Float {
    return from + nextFloat() * (until - from)
}