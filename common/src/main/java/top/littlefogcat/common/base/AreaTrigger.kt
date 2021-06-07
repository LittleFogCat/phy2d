package top.littlefogcat.common.base

/**
 * 进入某个区域为触发条件的触发器。
 *
 * @Author：littlefogcat
 * @Email：littlefogcat@foxmail.com
 */
interface AreaTrigger : Triggerable {
    fun isInArea(x: Int, y: Int): Boolean
}