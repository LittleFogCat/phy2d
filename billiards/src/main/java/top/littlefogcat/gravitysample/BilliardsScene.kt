package top.littlefogcat.gravitysample

import top.littlefogcat.phy2d.base.Object
import top.littlefogcat.phy2d.scene.BorderedScene
import top.littlefogcat.phy2d.shape.Border
import top.littlefogcat.phy2d.utils.checkAndApplyCollision

/**
 * [u] 摩擦系数 f = umg，这里的ug简化为u
 *
 * @Author：littlefogcat
 * @Email：littlefogcat@foxmail.com
 */
class BilliardsScene(border: Border, var u: Float = 0.043f) : BorderedScene(border) {

    override fun defaultHandleObjectTimerTick(obj: Object) {
        super.defaultHandleObjectTimerTick(obj)
        // 因为f = um，时间1，冲量为um，速度变化为u
        val v = obj.velocity
        if (v.absoluteValue < u) {
            v.setValue(0f)
        } else {
            v.setValue(v.absoluteValue - u)
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