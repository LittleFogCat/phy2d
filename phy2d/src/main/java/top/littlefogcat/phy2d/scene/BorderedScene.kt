package top.littlefogcat.phy2d.scene

import top.littlefogcat.math.FreeVector
import top.littlefogcat.phy2d.base.Object
import top.littlefogcat.phy2d.shape.Border

/**
 * @Author：littlefogcat
 * @Email：littlefogcat@foxmail.com
 */
open class BorderedScene(
    border: Border,
    // 四个边界的弹性系数
    var el: Float = 1f,
    val et: Float = 1f,
    val er: Float = 1f,
    var eb: Float = 1f,
) : Scene(border) {

    override fun onObjectArriveLeft(obj: Object) {
        if (obj.velocity.x > 0 || !obj.bounded) return
        obj.giveImpulse(
            impulse = FreeVector(-(et + obj.elasticity) * obj.velocity.x * obj.mass, 0),
            atPoint = obj.shape.left
        )
    }

    override fun onObjectArriveRight(obj: Object) {
        if (obj.velocity.x < 0 || !obj.bounded) return
        obj.giveImpulse(
            impulse = FreeVector(-(et + obj.elasticity) * obj.velocity.x * obj.mass, 0),
            atPoint = obj.shape.right
        )
    }

    override fun onObjectArriveTop(obj: Object) {
        if (obj.velocity.y > 0 || !obj.bounded) return
        obj.giveImpulse(
            impulse = FreeVector(0, -(et + obj.elasticity) * obj.velocity.y * obj.mass),
            atPoint = obj.shape.top
        )
    }

    override fun onObjectArriveBottom(obj: Object) {
        if (obj.velocity.y < 0 || !obj.bounded) return
        obj.giveImpulse(
            impulse = FreeVector(0, -(et + obj.elasticity) * obj.velocity.y * obj.mass),
            atPoint = obj.shape.bottom
        )
    }

    override fun onObjectLeaveScene(obj: Object) {
        super.onObjectLeaveScene(obj)
        removeObject(obj)
    }

}