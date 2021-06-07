package top.littlefogcat.common

import android.view.View

/**
 * @Author：littlefogcat
 * @Email：littlefogcat@foxmail.com
 */

@Suppress("DEPRECATION")
fun View.getColor(id: Int): Int {
    return context.resources.getColor(id)
}

