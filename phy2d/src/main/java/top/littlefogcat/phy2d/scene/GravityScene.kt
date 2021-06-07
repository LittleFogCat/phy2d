package top.littlefogcat.phy2d.scene

import top.littlefogcat.phy2d.base.Force
import top.littlefogcat.math.FreeVector
import top.littlefogcat.phy2d.base.Object
import top.littlefogcat.phy2d.shape.Border

/**
 * 重力场景
 *
 * [gravity] 重力加速度
 */
open class GravityScene(
    border: Border,
    var gravity: FreeVector = FreeVector(0f, 1.5f)
) : Scene(border) {

    /**
     * G = mg
     */
    override fun initForceToObject(obj: Object): Force {
        return Force(gravity * obj.mass)
    }

    override fun timerTick() {
        super.timerTick()
        try {
            objects.values.forEach {
                defaultHandleObjectTimerTick(it)
            }
        } catch (e: Exception) {
        }
    }

}