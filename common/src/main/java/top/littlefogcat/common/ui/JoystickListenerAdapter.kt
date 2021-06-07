package top.littlefogcat.common.ui

import top.littlefogcat.math.FreeVector

/**
 * @Author：littlefogcat
 * @Email：littlefogcat@foxmail.com
 */
abstract class JoystickListenerAdapter : (JoystickView.JoystickEvent) -> Unit {
    override fun invoke(e: JoystickView.JoystickEvent) {
        when (e.type) {
            JoystickView.JoystickEvent.Type.ACTION_DOWN -> onActionDown(e.vector)
            JoystickView.JoystickEvent.Type.ACTION_MOVE -> onActionMove(e.vector)
            JoystickView.JoystickEvent.Type.ACTION_UP -> onActionUp(e.vector)
            JoystickView.JoystickEvent.Type.OTHER -> onOtherAction(e)
        }
    }

    open fun onActionDown(vector: FreeVector) {}
    open fun onActionMove(vector: FreeVector) {}
    open fun onActionUp(vector: FreeVector) {}
    open fun onOtherAction(e: JoystickView.JoystickEvent) {}
}