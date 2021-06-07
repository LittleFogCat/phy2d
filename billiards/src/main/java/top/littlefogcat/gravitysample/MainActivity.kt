package top.littlefogcat.gravitysample

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import kotlinx.android.synthetic.main.activity_main.*
import top.littlefogcat.common.ui.FullscreenActivity
import top.littlefogcat.common.util.ScreenUtil
import top.littlefogcat.math.Point
import top.littlefogcat.phy2d.base.CircleObject

class MainActivity : FullscreenActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        initDesk()
    }

    private fun initDesk() {
        ScreenUtil.update(this)
        Handler().postDelayed(
            {
                val obj1 = CircleObject(30f, Color.RED, 40f, name = "obj1", elasticity = 0.88f)
                val obj2 = CircleObject(30f, Color.YELLOW, 40f, name = "obj2", elasticity = 0.88f)
                val obj3 = CircleObject(30f, Color.GREEN, 40f, name = "obj3", elasticity = 0.88f)
                val obj4 = CircleObject(30f, Color.BLUE, 40f, name = "obj4", elasticity = 0.88f)
                val obj5 = CircleObject(30f, Color.BLACK, 40f, name = "obj5", elasticity = 0.88f)
                val obj6 = CircleObject(30f, Color.MAGENTA, 40f, name = "obj6", elasticity = 0.88f)

                surface.addObject(obj1, Point(550, 600))
                surface.addObject(obj2, Point(515, 545))
                surface.addObject(obj3, Point(580, 545))
                surface.addObject(obj4, Point(485, 490))
                surface.addObject(obj5, Point(550, 490))
                surface.addObject(obj6, Point(615, 490))

                surface.addMaimBall(
                    CircleObject(30f, Color.WHITE, 40f, name = "main", elasticity = 0.88f),
                    Point(550, 1800)
                )
            },
            300
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (surface.isAllStopped) {
                    surface.startAim()
                    surface.setEdge(event.x, event.y)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                surface.setEdge(event.x, event.y)
            }
            MotionEvent.ACTION_UP -> {
                surface.shoot()
            }
        }
        return super.onTouchEvent(event)
    }


}