package top.littlefogcat.common.util

import android.app.Activity
import android.util.DisplayMetrics

/**
 * @Author：littlefogcat
 * @Email：littlefogcat@foxmail.com
 */
object ScreenUtil {
    var screenWidth = 0
    var screenHeight = 0
    var realWidth = 0
    var realHeight = 0

    fun update(activity: Activity) {
        val metrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(metrics)
        screenWidth = metrics.widthPixels
        screenHeight = metrics.heightPixels

        activity.windowManager.defaultDisplay.getRealMetrics(metrics)
        realWidth = metrics.widthPixels
        realHeight = metrics.heightPixels
    }
}