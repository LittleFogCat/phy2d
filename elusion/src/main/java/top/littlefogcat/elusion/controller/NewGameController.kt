package top.littlefogcat.elusion.controller

import android.graphics.Color
import android.util.Log
import top.littlefogcat.common.animation.nextFloat
import top.littlefogcat.phy2d.base.Force
import top.littlefogcat.phy2d.base.Object
import top.littlefogcat.phy2d.utils.checkCollision
import top.littlefogcat.common.util.ScreenUtil
import top.littlefogcat.elusion.ui.NewGamePanel
import kotlin.random.Random

/**
 * @Author：littlefogcat
 * @Email：littlefogcat@foxmail.com
 */
class NewGameController(val gamePanel: NewGamePanel) {
    companion object {
        const val TAG = "NewGameController"
    }

    val main get() = gamePanel.main
    val enemies = mutableListOf<Object>()
    val random = Random.Default


    fun checkGameOver() {
        enemies.forEach {
            if (checkCollision(main.shape, it.shape) == null) {
                Log.e(TAG, "GameOver!")
            }
        }
    }

    fun createOneRandomEnemy() {
        val mass = 10f
        val r = random.nextFloat(8f, 40f)
        val x = random.nextFloat(r, ScreenUtil.realWidth - r)
        val y = r
        val vx = 0f
        val vy = random.nextFloat(3f, 5f)
        val elastic = false
        val elasticity = -1f
        val maxVelocity = vy
        val color = Color.parseColor("#bb00EE00")
        val enemy = gamePanel.createCircle(r, color, x, y, maxVelocity, vx, vy, mass, elastic, elasticity)
        enemy.name = "enemy-0"
        enemy.giveForce(Force(0f, 10f))
        enemies.add(enemy)
    }

    fun onSurfaceUpdate() {
        enemies.forEach {
//            Log.d(BaseSurfaceView.TAG, "onSurfaceUpdate: ${it.position}")
//            Log.d(BaseSurfaceView.TAG, "onSurfaceUpdate: ${it.name} ${it.force}")
        }
    }
}