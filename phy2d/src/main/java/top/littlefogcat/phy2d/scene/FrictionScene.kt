package top.littlefogcat.phy2d.scene

import top.littlefogcat.math.isZero
import top.littlefogcat.phy2d.base.Force
import top.littlefogcat.phy2d.base.Object
import top.littlefogcat.phy2d.shape.Border
import top.littlefogcat.phy2d.utils.log
import kotlin.math.absoluteValue

/**
 * 有摩擦力的俯瞰场景
 *
 * 在这个场景中，F必须大于um才能进行移动。当物体静止时，摩擦力视为0，不计算静摩擦力。
 *
 * [u] 摩擦力公式 f = μmg，这里的[u]相当于ug
 *
 * @Author：littlefogcat
 * @Email：littlefogcat@foxmail.com
 */
open class FrictionScene(
    var u: Float, // 摩擦系数
    border: Border = Border(0, 0, 0, 0),
) : Scene(border) {
    companion object {
        const val TAG = "FrictionScene"
    }

    init {
        setOnBorderReachedListener(object : OnBorderReachedListener {
            override fun onObjectArriveLeft(obj: Object) {
                if (obj.bounded) obj.velocity.x = -obj.velocity.x
            }

            override fun onObjectArriveTop(obj: Object) {
                if (obj.bounded) obj.velocity.y = -obj.velocity.y
            }

            override fun onObjectArriveRight(obj: Object) {
                if (obj.bounded) obj.velocity.x = -obj.velocity.x
            }

            override fun onObjectArriveBottom(obj: Object) {
                if (obj.bounded) obj.velocity.y = -obj.velocity.y
            }
        })
    }

    /**
     * 摩擦力
     */
    private val f = Force()

    override fun timerTick() {
        removePendingObjects()
        objects.values.forEach { obj ->
            // 摩擦力与其他力分开计算
            val F = obj.force // 物体除去摩擦力收到的总力
            val m = obj.mass
            val v = obj.velocity
            val u = u * obj.u
            obj.apply {
                if (obj.name == "bullet-1") {
//                    println(obj.velocity)
                }
                // 根据速度计算出摩擦力
                if (v.absoluteValue.isZero()) {
                    // 静止，不考虑静摩擦力
                    f.setValue(0f, 0f)
                } else {
                    // 摩擦力与速度相反，大小 = μmg，这里的u相当于μg，相当于摩擦力造成的加速度
                    val direction = -obj.velocity
                    val friction = u * obj.mass
                    f.setValue(friction, direction)
                }
                // 当前静止且受力小于最大静摩擦力，保持静止
                if (f.absoluteValue == 0f) {
                    if (F.absoluteValue < u * mass) {
                        velocity.setValue(0f)
                        return@apply
                    }
                }

                // 位置变化
                if (!bounded) {
                    position += velocity
                } else {
                    // 检查边界
                    val newPosX = position.x + velocity.x
                    if (newPosX < border.left - shape.left.x) {
                        // 达到上边界
                        position.x = border.left - shape.left.x
                        velocity.x = -velocity.x * elasticity
                    } else if (newPosX > border.right - shape.right.x) {
                        position.x = border.right - shape.right.x
                        velocity.x = -velocity.x * elasticity
                    } else position.x = newPosX
                    val newPosY = position.y + velocity.y
                    if (newPosY < border.top - shape.top.y) {
                        position.y = border.top - shape.top.y
                        velocity.y = -velocity.y * elasticity
                    } else if (newPosY > border.bottom - shape.bottom.y) {
                        position.y = border.bottom - shape.bottom.y
                        velocity.y = -velocity.y * elasticity
                    } else position.y += velocity.y
                }

                log("================================")
                log("timerTick: force = $F")
                log("timerTick: accelerate = $accelerate")
                log("timerTick: velocity0 = $velocity")
                // 速度变化
                // 先看摩擦力
                // x方向，摩擦力不可以将速度反转
                val vx = velocity.x
                // 计算摩擦力造成的速度变化
                val deltaX = f.x / m

                if (vx.absoluteValue < deltaX.absoluteValue) {
                    velocity.x = 0f
                } else {
                    velocity.x += deltaX
                }
                val vy = velocity.y
                val deltaY = f.y / m
                if (vy.absoluteValue < deltaY.absoluteValue) {
                    velocity.y = 0f
                } else {
                    velocity.y += deltaY
                }

                log("timerTick: velocity1 = $velocity")
                // 外力
                if (!bounded) {
                    velocity += accelerate
                } else {
                    if ((left.x <= border.left && F.x < 0 || right.x >= border.right && F.x > 0)
                        && accelerate.x.absoluteValue > velocity.x.absoluteValue * 1.5f
                    ) velocity.x = 0f
                    else velocity.x += accelerate.x
                    if ((top.y <= border.top && F.y < 0 || bottom.y >= border.bottom && F.y > 0)
                        && accelerate.y.absoluteValue > velocity.y.absoluteValue * 1.5f
                    ) velocity.y = 0f
                    else velocity.y += accelerate.y
                }
                log("timerTick: velocity2 = $velocity")

                // 保证速度小于最大值
                if (velocity.absoluteValue > maxVelocity.absoluteValue) {
                    velocity.setValue(maxVelocity)
                }
                shape.rotate(angularVelocity)
            }
            // 检查物体是否触碰到边界
            if (shouldCheckObjectReachEdge) {
                checkObjectReachEdge(obj)
            }
            defaultHandleObjectTimerTick(obj)
            obj.onTimerTick()
        }
    }

    override fun preObjectStateChanged(obj: Object) {
        val force = sceneForces[obj] // 摩擦力
        force?.let {
            val v = obj.velocity
            val m = obj.mass
            if (v.absoluteValue.isZero()) {
                // 速度为0 摩擦力为0，不考虑静摩擦力
                it.setValue(0f, 0f)
            } else {
                // 摩擦力与速度相反，大小 = μmg，这里的u相当于μg，相当于摩擦力造成的加速度
                val direction = -obj.velocity
                val friction = u * obj.mass
                it.setValue(friction, direction)

                // 检查是否会因为摩擦力过头，以免反复横跳
                // t = 1, a = f / m, △x = at = fx / m
                if (v.x.absoluteValue < it.x.absoluteValue / m) {
                    v.x = 0f
                    it.x = 0f
                }
                if (v.y.absoluteValue < it.y.absoluteValue / m) {
                    v.y = 0f
                    it.y = 0f
                }
            }
        }
    }

}