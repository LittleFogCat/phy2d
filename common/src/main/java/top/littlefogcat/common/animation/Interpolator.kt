package top.littlefogcat.common.animation

interface Interpolator {
    val speed: Speed
    fun next(): Speed
}