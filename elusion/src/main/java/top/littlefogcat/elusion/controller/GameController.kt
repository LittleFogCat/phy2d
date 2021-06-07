package top.littlefogcat.elusion.controller

import android.graphics.Color
import android.util.Log
import top.littlefogcat.common.util.ScreenUtil
import top.littlefogcat.elusion.model.Ball
import top.littlefogcat.elusion.ui.GamePanel
import top.littlefogcat.math.FreeVector

/**
 * @Author：littlefogcat
 * @Email：littlefogcat@foxmail.com
 */
class GameController(val panel: GamePanel) {

    /**
     * 主角小球
     */
    val player = Ball().apply {
        r = 16f
        color = Color.GREEN
    }

    /**
     * 敌人小球
     */
    val enemies = mutableListOf<Ball>()

    /**
     * 有向加速插值器
     */
    val interpolator = DirectedAccelerateInterpolator(0f, 0f, 0f, FreeVector(0f, 0f), 0f)

    fun createEnemyAt(x: Float, y: Float) {
        val ball = Ball()
        ball.x = x
        ball.y = y
        ball.r = 16f
        enemies.add(ball)
    }

    fun gameOver() {
        Log.i("Controller", "gameOver!")
        panel.running = false
    }

    fun move() {
        val speed = interpolator.next()
        val isFree = interpolator.direction.absoluteValue == 0f

        var newX = player.x + speed.x
        if (newX < player.r) {
            newX = player.r
            speed.x = if (isFree) -speed.x else 0f
        } else if (newX > ScreenUtil.realWidth - player.r) {
            newX = ScreenUtil.realWidth - player.r
            speed.x = if (isFree) -speed.x else 0f
        }

        var newY = player.y + speed.y
        if (newY < player.r) {
            newY = player.r
            speed.y = if (isFree) -speed.y else 0f
        } else if (newY > ScreenUtil.realHeight - player.r) {
            newY = ScreenUtil.realHeight - player.r
            speed.y = if (isFree) -speed.y else 0f
        }
        player.x = newX
        player.y = newY
        for (enemy in enemies) {
            if (player.checkCollision(enemy)) {
                gameOver()
                break
            }
        }
    }
}