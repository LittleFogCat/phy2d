package top.littlefogcat.airecraftwar.game

import android.graphics.Color
import top.littlefogcat.math.FreeVector
import top.littlefogcat.phy2d.shape.CombinedShape
import top.littlefogcat.phy2d.shape.Polygon

/**
 * 全局游戏设置
 *
 * @Author：littlefogcat
 * @Email：littlefogcat@foxmail.com
 */
object GameOptions {

    /**
     * 场景阻力系数
     */
    var u = 0.25f

    /**
     * 手柄给予小球的力
     */
    var joystickForce: Float = 3f

    // ----------- 主机属性 ------------
    var mainAircraftSize = 0f
    var mainAircraftMass = 10f
    var mainAircraftMaxSpeed = 5f
    val mainAircraftShape
        get() = CombinedShape.of(
            Polygon(
                mainAircraftSize / 24, -mainAircraftSize * 1.2, // 头尖
                mainAircraftSize / 3 - mainAircraftSize / 24, -mainAircraftSize + mainAircraftSize / 6, // 头底
                mainAircraftSize / 3, -mainAircraftSize / 5, // 翅根
                mainAircraftSize * 1.1, mainAircraftSize / 2.5, // 翅尖
                mainAircraftSize / 1.1, mainAircraftSize / 2, // 翅中
                mainAircraftSize / 4.5, mainAircraftSize / 2, // 尾根
                mainAircraftSize / 4.8, mainAircraftSize / 1.4, // 尾底
                -mainAircraftSize / 4.8, mainAircraftSize / 1.4, // 尾底
                -mainAircraftSize / 4.5, mainAircraftSize / 2, // 尾根
                -mainAircraftSize / 1.1, mainAircraftSize / 2, // 翅中
                -mainAircraftSize * 1.1, mainAircraftSize / 2.5, // 翅尖
                -mainAircraftSize / 3, -mainAircraftSize / 5, // 翅根
                -mainAircraftSize / 3 + mainAircraftSize / 24, -mainAircraftSize + mainAircraftSize / 6, // 头底
                -mainAircraftSize / 24, -mainAircraftSize * 1.2, // 头尖
                mainAircraftSize / 24, -mainAircraftSize * 1.2, // 头尖
            ).also { it.color = Color.DKGRAY },
            Polygon(
                mainAircraftSize / 24, -mainAircraftSize,
                mainAircraftSize / 7, -mainAircraftSize / 1.3,
                mainAircraftSize / 7, -mainAircraftSize / 5,
                mainAircraftSize / 24, -mainAircraftSize / 12,
                -mainAircraftSize / 24, -mainAircraftSize / 12,
                -mainAircraftSize / 7, -mainAircraftSize / 5,
                -mainAircraftSize / 7, -mainAircraftSize / 1.3,
                -mainAircraftSize / 24, -mainAircraftSize,
                mainAircraftSize / 24, -mainAircraftSize,
            ).also { it.color = Color.DKGRAY },
        )
    var initialLife = 1

    // ----------- 子弹属性 ------------
    class BulletOption(
        var length: Float,
        var width: Float,
        var damage: Int,
        var velocity: FreeVector,
        var maxVelocity: Float,
        var rate: Int // 值越小，射速越快
    )

    var bulletOption = BulletOption(
        length = 18f,
        width = 10f,
        damage = 10,
        velocity = FreeVector(0f, -10f),
        maxVelocity = 10f,
        rate = 30
    )

    // --------------- 敌机设置 ------------------
    class AircraftOption {

    }

    var enemyRate = 120

    var maxEnemyNumber = 5
    var smallEnemySize = 0f
    var smallEnemySizePercent = 0.02f
    var smallEnemyHp = 10
    var smallEnemyVelocity = 4f
    val smallEnemyShape
        get() = Polygon(
            0, 0,
            smallEnemySize, -smallEnemySize,
            -smallEnemySize, -smallEnemySize,
            0, 0
        ).apply { color = Color.DKGRAY }
}