package top.littlefogcat.phy2d.base

import top.littlefogcat.phy2d.utils.sumByFloat
import java.lang.UnsupportedOperationException

/**
 * 合力。
 *
 * 合力是一个虚拟的力，等效于若干个力合成在一起的力。
 */
class CombinedForce() : Force(0f, 0f) {
    private val forces = mutableSetOf<Force>()

    override var x: Float
        get() = forces.sumByFloat { it.x }
        set(value) = throw UnsupportedOperationException("Cannot set value to a CombinedForce")

    override var y: Float
        get() = forces.sumByFloat { it.y }
        set(value) = throw UnsupportedOperationException("Cannot set value to a CombinedForce")

    fun add(force: Force) {
        forces.add(force)
    }

    fun remove(force: Force) {
        forces.remove(force)
    }

    fun clear() {
        forces.clear()
    }

    override fun toString(): String {
        return "CombinedForce(forces=$forces, x=$x, y=$y)"
    }

}