package top.littlefogcat.elusion

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_main.*
import top.littlefogcat.math.FreeVector
import top.littlefogcat.common.ui.JoystickView
import top.littlefogcat.common.util.ScreenUtil
import top.littlefogcat.elusion.model.PreferencesManager

class MainActivity : FullscreenActivity() {
    companion object {
        @Suppress("unused")
        const val TAG = "MainActivity"
        const val MSG_MOVING = 1001
        const val MSG_DISMISS_SETTING = 1002

        const val KEY_MAX_SPEED = "max_speed"
        const val KEY_ACCELERATE = "accelerate"


        const val MAX_MAX_SPEED = 50f
        const val MAX_ACCELERATE = 5f
        const val SETTING_DISMISS_DELAY = 5000L
    }

    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_MOVING -> {
                    movingByDirection()
                }
                MSG_DISMISS_SETTING -> showSettingLayout(false)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PreferencesManager.init(this)
        initJoystick()
        initUI()
        initGamePanel()
    }

    override fun onResume() {
        super.onResume()
        ScreenUtil.update(this)
    }

    /**
     * 初始化移动面板
     */
    private fun initJoystick() {
        joy.setJoystickListener { e ->
            when (e.type) {
                JoystickView.JoystickEvent.Type.ACTION_DOWN -> {
                    changeMovingDirection(e.vector)
                    handler.removeMessages(MSG_MOVING)
                    handler.sendEmptyMessage(MSG_MOVING)
                }
                JoystickView.JoystickEvent.Type.ACTION_MOVE -> {
                    changeMovingDirection(e.vector)
                }
                JoystickView.JoystickEvent.Type.ACTION_UP -> {
                    changeMovingDirection(FreeVector(0f, 0f))
                }
                JoystickView.JoystickEvent.Type.OTHER -> {
                }
            }
        }
    }

    private fun initUI() {

        settingButton.setOnClickListener {
            showSettingLayout(true)
            handler.sendEmptyMessageDelayed(MSG_DISMISS_SETTING, SETTING_DISMISS_DELAY)
            seekMaxSpeed.progress = (panel.getMaxSpeed() * 100 / MAX_MAX_SPEED).toInt()
            seekAccelerate.progress = (panel.getAccelerate() * 100 / MAX_ACCELERATE).toInt()
        }

        closeButton.setOnClickListener {
            showSettingLayout(false)
            handler.removeMessages(MSG_DISMISS_SETTING)
        }

        seekMaxSpeed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val ms = progress.toFloat() * MAX_MAX_SPEED / 100 // 0 ~ 50
                panel.setMaxSpeed(ms)
                val msStr = "%.0f".format(ms)
                textMaxSpeed.text = msStr
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                PreferencesManager.putFloat(KEY_MAX_SPEED, seekBar.progress.toFloat() * MAX_MAX_SPEED / 100)
            }
        })

        seekAccelerate.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val acc = progress.toFloat() * MAX_ACCELERATE / 100 // 0~10
                panel.setAcceleration(acc)
                val accStr = "%.2f".format(acc)
                textAccelerate.text = accStr
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                handler.removeMessages(MSG_DISMISS_SETTING)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                handler.sendEmptyMessageDelayed(MSG_DISMISS_SETTING, SETTING_DISMISS_DELAY)
                PreferencesManager.putFloat(KEY_ACCELERATE, seekBar.progress.toFloat() * MAX_ACCELERATE / 100)
            }
        })
    }

    /**
     * 初始化游戏面板
     */
    private fun initGamePanel() {
        panel.setAcceleration(PreferencesManager.getFloat(KEY_ACCELERATE, 1f)) // 初始化加速度
        panel.setMaxSpeed(PreferencesManager.getFloat(KEY_MAX_SPEED, 12f)) // 初始化最大速度
    }

    /**
     * 显/隐设置面板
     */
    private fun showSettingLayout(show: Boolean) {
        settingLayout.visibility = if (show) View.VISIBLE else View.GONE
        settingButton.visibility = if (show) View.GONE else View.VISIBLE
    }

    /**
     * 改变移动方向
     */
    private fun changeMovingDirection(direction: FreeVector) {
        panel.changeDirection(direction)
    }

    /**
     * 每隔16ms根据方向改变小球位置
     */
    private fun movingByDirection() {
        panel.move()
        handler.sendEmptyMessageDelayed(MSG_MOVING, 16L)
    }

}