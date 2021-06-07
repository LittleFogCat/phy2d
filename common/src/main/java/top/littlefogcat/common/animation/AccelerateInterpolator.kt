package top.littlefogcat.common.animation

class AccelerateInterpolator(
    speedX: Float,
    speedY: Float,
    var gx: Float, // x轴加速度
    var gy: Float // y轴加速度
) : AbstractInterpolator(speedX, speedY) {

    override fun next(): Speed {
        speed.x += gx
        speed.y += gy
        return speed
    }

}