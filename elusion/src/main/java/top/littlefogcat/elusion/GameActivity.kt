package top.littlefogcat.elusion

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_game.*
import top.littlefogcat.phy2d.base.Force
import top.littlefogcat.common.ui.JoystickView

/**
 * Sure this is replaced by module [aircraftwar]
 */
class GameActivity : FullscreenActivity() {
    lateinit var force: Force

    val absoluteForce = 12f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        init()
//        enableLog()
    }

    /**
     * 初始化移动面板
     */
    private fun init() {
        force = Force()
        force.name = "Joystick"
        joy.setJoystickListener { e ->
            when (e.type) {
                JoystickView.JoystickEvent.Type.ACTION_DOWN -> {
                    force.setValue(absoluteForce, e.vector)
                    panel.main.giveForce(force)
                }
                JoystickView.JoystickEvent.Type.ACTION_MOVE -> {
                    force.setValue(absoluteForce, e.vector)
                }
                JoystickView.JoystickEvent.Type.ACTION_UP -> {
                    panel.main.removeForce(force)
                }
                JoystickView.JoystickEvent.Type.OTHER -> {
                }
            }
        }
    }
}