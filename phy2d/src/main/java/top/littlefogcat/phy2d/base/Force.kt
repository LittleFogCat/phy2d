package top.littlefogcat.phy2d.base

import top.littlefogcat.math.FreeVector

/**
 * 力是一个向量。
 *
 * @Author：littlefogcat
 * @Email：littlefogcat@foxmail.com
 */
open class Force(x: Float, y: Float) : FreeVector(x, y) {
    constructor() : this(0f, 0f)
    constructor(vector: FreeVector) : this(vector.x, vector.y)

    override fun clone(): Force {
        return Force(x, y)
    }
}