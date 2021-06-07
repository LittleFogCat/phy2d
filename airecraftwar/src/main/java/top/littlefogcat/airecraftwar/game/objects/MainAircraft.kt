package top.littlefogcat.airecraftwar.game.objects

import android.graphics.Color
import android.util.Log
import top.littlefogcat.airecraftwar.game.GameController
import top.littlefogcat.airecraftwar.game.GameOptions
import top.littlefogcat.math.FreeVector
import top.littlefogcat.math.Point
import top.littlefogcat.phy2d.shape.CombinedShape
import top.littlefogcat.phy2d.shape.Polygon
import kotlin.math.sqrt

/**
 * @Author：littlefogcat
 * @Email：littlefogcat@foxmail.com
 */
class MainAircraft(val controller: GameController) : Aircraft(
    hp = 100,
    mass = GameOptions.mainAircraftMass,
    velocity = FreeVector(),
    maxVelocity = GameOptions.mainAircraftMaxSpeed,
    bounded = true,
    elasticity = 0f,
) {
    companion object {
        const val TAG = "MainAircraft"
    }

    override var name = "main"

    private val STATE_ALIVE = 0
    private val STATE_EXPLODING = 1
    private val STATE_CRASHED = 2
    private var state = STATE_ALIVE
    private var explodeFrameCount = 0
    private val explodeShape = ExplodeShape()

    fun start() {
        state = STATE_ALIVE
    }

    override fun explode() {
        if (state != STATE_ALIVE) return
        performExplode()
    }

    override fun onTimerTick() {
        if (state == STATE_ALIVE) {
            return
        } else if (state == STATE_EXPLODING) {
            explodeFrameCount++
            explodeShape.setScale((1 / sqrt(explodeFrameCount.toDouble())).toFloat())
        }

        if (explodeFrameCount == 60) {
            performCrash()
        }
    }

    private fun performExplode() {
        Log.d(TAG, "performExplode: ")
        state = STATE_EXPLODING
        (shape as CombinedShape).clearShape()
        addShape(explodeShape)
    }

    private fun performCrash() {
        Log.d(TAG, "performCrash: ")
        state = STATE_CRASHED
        (shape as CombinedShape).clearShape()
        scene?.removeObject(this)
        explodeFrameCount = 0
        controller.performGameOver()
    }

    fun isDead(): Boolean {
        return state == STATE_CRASHED
    }

    class ExplodeShape(
        vertices: Array<Number> = arrayOf(
            30, 0,
            52, 10,
            20, 20,
            30, 50,
            0, 30,
            -20, 50,
            -20, 30,
            -70, 40,
            -35, 10,
            -80, -20,
            -25, -18,
            -30, -55,
            0, -30,
            20, -50,
            20, -25,
            65, -30,
            30, 0
        )
    ) : Polygon(*vertices) {
        private val originVertices: List<Point> = List(vertices.size / 2) { index ->
            Point(vertices[index * 2], vertices[index * 2 + 1])
        }
        override var color: Int = Color.DKGRAY

        fun setScale(scale: Float) {
            forEachIndexVertex { i, v ->
                val origin = originVertices[i]
                v.set(origin.x * scale, origin.y * scale)
            }
        }
    }
}