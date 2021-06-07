package top.littlefogcat.common.base

/**
 * 标记一个可触发的项目。
 * 当满足了触发条件，则调用[onTrigger]回调。
 *
 * @Author：littlefogcat
 * @Email：littlefogcat@foxmail.com
 */
interface Triggerable {
    fun onTrigger()
}