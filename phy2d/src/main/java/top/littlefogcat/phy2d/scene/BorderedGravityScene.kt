package top.littlefogcat.phy2d.scene

import top.littlefogcat.math.FreeVector
import top.littlefogcat.phy2d.base.Force
import top.littlefogcat.phy2d.base.Object
import top.littlefogcat.phy2d.shape.Border
import top.littlefogcat.phy2d.utils.checkAndApplyCollision
import kotlin.math.absoluteValue

/**
 * @Author：littlefogcat
 * @Email：littlefogcat@foxmail.com
 */
open class BorderedGravityScene(
    border: Border,
    el: Float = 1f,
    et: Float = 1f,
    er: Float = 1f,
    eb: Float = 1f,
    var gravity: FreeVector = FreeVector(0f, 1.5f)
) : BorderedScene(border, el, et, er, eb) {

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

    override fun onObjectArriveBottom(obj: Object) {
        super.onObjectArriveBottom(obj)
        val delta = obj.bottom.y - border.bottom
        if (delta > 0) {
            val v = obj.velocity.y.absoluteValue
            val g = gravity.y.absoluteValue
            val t = v / g
            if (0.5f * g * t * t < delta) {
                // 再怎么也弹不出来了，防止陷入底部
                obj.setBottom(border.bottom)
                obj.velocity.y = 0f
            }
        }
    }

    override fun checkCollisions() {
        val objectList = objects.values.toList()
        for (i in 0 until objectList.size - 1) {
            for (j in i + 1 until objectList.size) {
                val obj1 = objectList[i]
                val obj2 = objectList[j]
                val cp = checkAndApplyCollision(obj1, obj2)
                // 防止粘连
                if (cp != null) {
                    // cp -> O1
                    val vec1 = obj1.position - cp
                    // cp -> O2
                    val vec2 = obj2.position - cp
                    if (vec1.absoluteValue < obj1.radius) {
                        vec1.setValue(obj1.radius + 0.55f)
                        obj1.position.set(cp + vec1)
                    }
                    if (vec2.absoluteValue < obj2.radius) {
                        vec2.setValue(obj2.radius + 0.55f)
                        obj2.position.set(cp + vec2)
                    }
                }
            }
        }
    }
}