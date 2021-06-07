package top.littlefogcat.joysticksample

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.widget.FrameLayout
import top.littlefogcat.common.ui.FullscreenActivity
import top.littlefogcat.common.ui.JoystickListenerAdapter
import top.littlefogcat.common.ui.JoystickView
import top.littlefogcat.common.util.ScreenUtil
import top.littlefogcat.math.FreeVector

class MainActivity : FullscreenActivity() {
    private lateinit var joyView: JoystickView
    private lateinit var view: View
    private val vec = FreeVector()

    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            vec.setValue(8f)
            val lp = view.layoutParams as FrameLayout.LayoutParams
            lp.leftMargin = (lp.leftMargin + vec.x).toInt()
            lp.topMargin = (lp.topMargin + vec.y).toInt()
            view.requestLayout()
            sendEmptyMessageDelayed(0, 16)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        joyView = findViewById(R.id.joy)
        view = findViewById(R.id.view)

        joyView.setJoystickListener(object : JoystickListenerAdapter() {
            override fun onActionDown(vector: FreeVector) {
                vec.setValue(vector.x, vector.y)
                handler.sendEmptyMessage(0)
            }

            override fun onActionMove(vector: FreeVector) {
                vec.setValue(vector.x, vector.y)
            }

            override fun onActionUp(vector: FreeVector) {
                handler.removeMessages(0)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        ScreenUtil.update(this)
    }
}