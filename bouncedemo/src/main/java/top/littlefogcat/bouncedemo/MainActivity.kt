package top.littlefogcat.bouncedemo

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import top.littlefogcat.common.ui.FullscreenActivity
import top.littlefogcat.math.FreeVector
import top.littlefogcat.math.Point
import top.littlefogcat.phy2d.base.CircleObject
import top.littlefogcat.phy2d.shape.Shape
import kotlin.random.Random

/**
 * WTF is this class?
 *
 * @deprecated d
 */
class MainActivity : FullscreenActivity() {
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        start.setOnClickListener {
            handler.post(task)
        }
    }

    private val task: () -> Unit = {
        val r = Random.Default

        val redCircle = CircleObject(
            100f, Color.RED, 10f, Shape.Style.BORDERED, elasticity = 0.88f,
            velocity = FreeVector(r.nextInt(10) - 5, 0),
            name = "小红"
        )
        val greenCircle = CircleObject(
            100f, Color.GREEN, 10f, Shape.Style.BORDERED, elasticity = 0.86f,
            name = "小绿"
        )
        val blueCircle = CircleObject(
            100f, Color.BLUE, 10f, Shape.Style.BORDERED, elasticity = 0.85f,
            velocity = FreeVector(r.nextInt(10) - 5, 0),
            name = "小蓝"
        )

        mySurface.addObject(redCircle, Point(300, 420))
        mySurface.addObject(greenCircle, Point(500, 500))
        mySurface.addObject(blueCircle, Point(670, 350))
    }


}