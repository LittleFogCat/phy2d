package top.littlefogcat.airecraftwar.game

import android.util.Log
import top.littlefogcat.airecraftwar.game.objects.*
import top.littlefogcat.phy2d.base.Force
import top.littlefogcat.phy2d.scene.Scene
import top.littlefogcat.phy2d.utils.checkCollision
import top.littlefogcat.common.util.ScreenUtil
import top.littlefogcat.math.FreeVector
import top.littlefogcat.math.Point
import kotlin.random.Random

/**
 * @Author：littlefogcat
 * @Email：littlefogcat@foxmail.com
 */
class GameController(private val gameView: GameView?) {
    companion object {
        const val STATE_READY = 0
        const val STATE_RUNNING = 1
        const val STATE_DEAD = 2
        const val STATE_IMMUNE = 3
        const val STATE_GAME_OVER = 4

        @Suppress("unused")
        const val TAG = "GameController"
    }

    // ------------------- field --------------------

    var state = STATE_READY

    var scene: Scene? = null

    /**
     * 主机
     */
    lateinit var mainAircraft: MainAircraft

    var joystickForce = Force()
    private var enableMoving = true

    /**
     * 子弹池
     */
    private val bulletPool = BulletPool()

    private val bullets = mutableSetOf<Bullet>()
    private val removedBullets = mutableSetOf<Bullet>()

    /**
     * 敌机
     */
    private val enemyPool = AircraftPool()
    private val enemies = mutableSetOf<Aircraft>()
    private val removedEnemies = mutableSetOf<Aircraft>()

    /**
     * 计时
     */
    private var timeTick = 0

    /**
     * 剩余生命
     */
    private var life = GameOptions.initialLife

    private var gameOverObserver: OnGameOverListener? = null


    // ------------------- public functions ----------------------

    fun start() {
        if (!this::mainAircraft.isInitialized) {
            mainAircraft = MainAircraft(this)
        }
        mainAircraft.apply {
            addShape(GameOptions.mainAircraftShape)
            giveForce(joystickForce)
            velocity.setValue(0f)
            start()
        }
        scene?.let {
            it.removeAllObject()
            it.addObject(
                mainAircraft,
                Point(gameView!!.width / 2, gameView.height - GameOptions.mainAircraftSize * 1.2)
            )
        }

        life = GameOptions.initialLife
        enableMoving = true
        state = STATE_RUNNING

//        Log.d(TAG, "start: ${mainAircraft.shape}")
    }

    fun changeDirection(vector: FreeVector) {
//        Log.v(TAG, "changeDirection: $enableMoving $vector")
        if (enableMoving) joystickForce.setValue(FreeVector.of(vector, GameOptions.joystickForce))
    }

    fun stopMoving() {
        joystickForce.setValue(0f)
    }

    fun disableMoving() {
        enableMoving = false
    }

    fun setMainAircraftPosition(x: Number, y: Number) {
        mainAircraft.position.set(x, y)
    }

    /**
     * 游戏结束回调
     */
    fun setOnGameOverListener(observer: OnGameOverListener) {
        gameOverObserver = observer
    }

    /**
     * 每帧的运算
     */
    fun timeTick() {
//        Log.d(TAG, "timeTick: $joystickForce")
        when (state) {
            STATE_READY -> {
            }
            STATE_RUNNING -> {
//                Log.d(TAG, "timeTick: ${mainAircraft.isDead()}")
//                Log.d(TAG, "timeTick: ${mainAircraft.shape}")
                if (mainAircraft.isDead()) {
                    performGameOver()
                } else {
                    // 检查需要移除的对象
                    recycleObjects()
                    // 检查是否吃到奖励
                    checkBuff()
                    // 发射子弹
                    performFire()
                    // 射击敌人
                    checkHitEnemy()
                    // 创造敌人
                    createRandomEnemy()
                    // 检查主机是否与敌机相撞
                    checkCollision()
                }
            }
            STATE_DEAD -> {
            }
            STATE_IMMUNE -> {
            }
            STATE_GAME_OVER -> {
            }
            else -> {
            }
        }
        timeTick++
    }

    // ------------------ private functions ---------------------

    private fun checkCollision(): Boolean {
        enemies.forEach {
            if (checkCollision(mainAircraft, it) != null) {
                Log.w(TAG, "performCollision: 发生碰撞with $it, life = $life")
                // 发生碰撞
                mainAircraft.explode()
                state = STATE_DEAD
                disableMoving()
                return true
            }
        }
        return false
    }

    fun performGameOver() {
        bullets.clear()
        enemies.clear()
        disableMoving()
        mainAircraft.velocity.setValue(0f)
        state = STATE_GAME_OVER
        gameOverObserver?.onGameOver()
    }

    /**
     * 吃到加成
     */
    private fun checkBuff() {
    }

    /**
     * 发射子弹
     */
    private fun performFire() {
        // 每隔bulletOption.rate帧发射一颗子弹
        if (timeTick % GameOptions.bulletOption.rate != 0) return
        val pos = mainAircraft.position
        // 从子弹池中取得子弹
        val bullet: Bullet = bulletPool.get(GameOptions.bulletOption)
        if (bullet.onLeaveSceneListener == null) {
            bullet.onLeaveSceneListener = { _, it ->
                removedBullets.add(it as Bullet)
//                Log.d(TAG, "performFire: ${bullet.name}离开了场景")
            }
        }
        if (bullet.scene == null) {
            gameView?.scene?.addObject(bullet, Point(pos.x, pos.y - GameOptions.mainAircraftSize * 1.2))
        } else {
            bullet.position.set(pos.x, pos.y - GameOptions.mainAircraftSize * 1.2)
        }
        bullets.add(bullet)
//        Log.v(TAG, "performFire: create bullet: ${bullet.name} / ${bullet.identify}")
    }

    /**
     * 随机生成敌机
     */
    private fun createRandomEnemy() {
        if (timeTick % GameOptions.enemyRate != 0) return
        // level: 1~4
        val level = Random.Default.nextInt(1)
        val enemy = enemyPool.get(level)
        if (enemy.onLeaveSceneListener == null) {
            enemy.onLeaveSceneListener = { _, it ->
                removedEnemies.add(it as Aircraft)
            }
        }
        if (enemy.onExplosion == null) {
            enemy.onExplosion = {
                removedEnemies.add(it)
            }
        }
        val x = Random.Default.nextInt(0, gameView?.width ?: ScreenUtil.realWidth)
        gameView?.scene?.addObject(enemy, Point(x, 0))
        enemies.add(enemy)
//        Log.v(TAG, "createRandomEnemy: $enemy (time = $timeTick) (velocity=${enemy.velocity})")
    }

    private fun checkHitEnemy() {
        bullets.forEach { b ->
            enemies.forEach { e ->
                if (checkCollision(b, e) != null) {
                    e.hp -= b.damage
                    removedBullets.add(b)
                    Log.d(TAG, "checkHitEnemy: ${b.name}击中${e.name}")
                    if (e.hp <= 0) {
                        removedEnemies.add(e)
                        e.explode()
                    }
                }
            }
        }
    }

    private fun recycleObjects() {
        removeBullet()
        removeEnemy()
    }

    private fun removeBullet() {
        if (removedBullets.size <= 0) return
//        Log.v(TAG, "removeBullet: ${Thread.currentThread()}===================")
        removedBullets.forEach {
//            Log.d(TAG, "removeBullet: ${it.name} / ${it.identify}")
            bullets.remove(it)
            it.scene?.removeObject(it) ?: return
            bulletPool.recycle(it)
        }
        removedBullets.clear()
//        Log.d(TAG, "removeBullet: ${bullets.size}")
    }

    private fun removeEnemy() {
        if (removedEnemies.size <= 0) return
        removedEnemies.forEach {
            enemies.remove(it)
            scene?.removeObject(it)
            enemyPool.recycle(it)
        }
        removedEnemies.clear()
//        Log.d(TAG, "removeEnemy: ${enemies.size}")
    }

    // ---------------- classes and interfaces -------------------

}