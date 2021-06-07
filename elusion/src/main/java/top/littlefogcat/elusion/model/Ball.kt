package top.littlefogcat.elusion.model

import android.graphics.Color
import top.littlefogcat.common.base.AreaTrigger
import top.littlefogcat.math.getDistance

/**
 * 一个小球
 *
 * @Author：littlefogcat
 * @Email：littlefogcat@foxmail.com
 */
class Ball : AreaTrigger {
    var x: Float = 0f
    var y: Float = 0f
    var r: Float = 0f
    var color: Int = Color.RED

    override fun isInArea(x: Int, y: Int): Boolean = false

    override fun onTrigger() {
    }

    /**
     * 检查两个球是否碰撞
     */
    fun checkCollision(other: Ball): Boolean {
        val dist = getDistance(x, y, other.x, other.y)
        return dist < r + other.r
    }
}