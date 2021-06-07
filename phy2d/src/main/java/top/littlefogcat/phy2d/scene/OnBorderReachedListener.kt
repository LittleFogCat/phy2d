package top.littlefogcat.phy2d.scene

import top.littlefogcat.phy2d.base.Object

/**
 * @Author：littlefogcat
 * @Email：littlefogcat@foxmail.com
 */
interface OnBorderReachedListener {
    fun onObjectArriveLeft(obj: Object) {}
    fun onObjectArriveTop(obj: Object) {}
    fun onObjectArriveRight(obj: Object) {}
    fun onObjectArriveBottom(obj: Object) {}
}