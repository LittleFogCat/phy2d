package top.littlefogcat.airecraftwar

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import kotlinx.android.synthetic.main.activity_game.*
import top.littlefogcat.airecraftwar.game.GameController
import top.littlefogcat.airecraftwar.game.OnGameOverListener
import top.littlefogcat.common.ui.JoystickListenerAdapter
import top.littlefogcat.math.FreeVector

class GameActivity : FullscreenActivity(), OnGameOverListener {
    companion object {
        const val TAG = "GameActivity"
    }

    private val handler = Handler(Looper.getMainLooper())
    private var gameController: GameController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        Log.i(TAG, "onCreate: ")

        joystick.setJoystickListener(object : JoystickListenerAdapter() {
            override fun onActionDown(vector: FreeVector) {
                gameController?.changeDirection(vector)
            }

            override fun onActionMove(vector: FreeVector) {
                gameController?.changeDirection(vector)
            }

            override fun onActionUp(vector: FreeVector) {
                gameController?.stopMoving()
            }
        })

        gameController = GameController(gameView).also {
            it.setOnGameOverListener(this)
            gameView.setGameController(it)
        }

        tvRestart.setOnClickListener {
            tvGameOver.clearAnimation()
            tvGameOver.visibility = View.GONE
            tvRestart.visibility = View.GONE
            gameController?.start()
        }

        tvTest.setOnClickListener {
            println(gameController?.mainAircraft?.position)
        }
    }

    override fun onGameOver() {
        handler.postDelayed({
            tvGameOver.visibility = View.VISIBLE
            tvGameOver.startAnimation(AlphaAnimation(0f, 1f).apply {
                interpolator = LinearInterpolator()
                duration = 1500
                fillAfter = true
                setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {
                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        tvRestart.visibility = View.VISIBLE
                        tvRestart.startAnimation(AlphaAnimation(0f, 1f)).apply {
                            interpolator = LinearInterpolator()
                            duration = 1500
                            fillAfter = true
                        }
                    }

                    override fun onAnimationRepeat(animation: Animation?) {
                    }
                })
            })
        }, 10)
    }

}