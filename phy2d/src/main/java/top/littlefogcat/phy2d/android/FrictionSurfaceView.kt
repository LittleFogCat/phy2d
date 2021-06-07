package top.littlefogcat.phy2d.android

import android.content.Context
import android.util.AttributeSet
import android.view.SurfaceHolder
import top.littlefogcat.phy2d.scene.FrictionScene
import top.littlefogcat.phy2d.shape.Border

/**
 * @Author：littlefogcat
 * @Email：littlefogcat@foxmail.com
 */
open class FrictionSurfaceView : BaseSurfaceView, SurfaceHolder.Callback {
    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    open var u = 0.1f

    open fun onSceneCreated(scene: FrictionScene) {
    }

    override fun onCreateScene(holder: SurfaceHolder, format: Int, width: Int, height: Int) =
        FrictionScene(u, Border(0, 0, width, height)).also {
            onSceneCreated(it)
        }
}