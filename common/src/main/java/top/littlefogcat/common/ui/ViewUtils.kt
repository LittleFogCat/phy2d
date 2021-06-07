package top.littlefogcat.common.ui

import android.graphics.Path
import kotlin.math.absoluteValue
import kotlin.math.acos
import kotlin.math.pow
import kotlin.math.sqrt


/**
 * @Author：littlefogcat
 * @Email：littlefogcat@foxmail.com
 */
/**
 * 绘制一条以([x1], [y1])为起点，([x2], [y2])为终点，曲率半径为[r]的弧线。
 *
 * [reverse]为可选项。因为可能的圆心有2个，当对应弧线的圆心为左侧那个时，[reverse]设置为false；否则为true。
 * 即[reverse]为true时，圆弧左凸；否则圆弧右凸。
 *
 * 见[centerPos]。
 */
fun Path.arcFromTo(x1: Float, y1: Float, x2: Float, y2: Float, r: Float, reverse: Boolean = false) {
//    println("($x1, $y1) => ($x2, $y2), r = $r")

    val centerPosList = try {
        centerPos(x1, y1, x2, y2, r)
    } catch (e: NoSolutionException) {
        e.printStackTrace()
        return
    }
//    println("centerpos0 = ${centerPosList[0].contentToString()}")
//    println("centerpos1 = ${centerPosList[1].contentToString()}")
    // 圆心坐标
    val centerPos = centerPosList[if (reverse) 1 else 0]
    val x0 = centerPos[0]
    val y0 = centerPos[1]
//    println("圆心坐标 ($x0, $y0)")

    val left = x0 - r
    val top = y0 - r
    val right = x0 + r
    val bottom = y0 + r
    val startAngle = acos((x1 - x0) / r) / Math.PI.toFloat() * 180
    val endAngle = acos((x2 - x0) / r) / Math.PI.toFloat() * 180

//    canvas.drawLine(x0, y0, x1, y1, Paint().apply { color = Color.parseColor("#EE33FFff") })
//    canvas.drawLine(x0, y0, x2, y2, Paint().apply { color = Color.parseColor("#EE33FFff") })

//    canvas.drawCircle(x0, y0, r, Paint().also { it.color = Color.parseColor("#44FF0000") })
//    println("arcFromTo: left$left, top$top, right$right, bottom$bottom, startAngle$startAngle, endAngle$endAngle")
    arcTo(
        left, top, right, bottom,
        if (reverse) -startAngle else startAngle,
        if (reverse) startAngle - endAngle else endAngle - startAngle,
        false
    )
}

/**
 * 根据圆上2点(x1, y1) (x2, y2)和半径r求圆心坐标，如果无解则抛出[NoSolutionException]。
 * 满足条件的圆心坐标有2个，按照从左往右、从上到下的顺序排序，返回一个二维数组。
 *
 * 联立方程组求解：
 * (x - x1) ^ 2 + (y - y1) ^ 2 = r ^ 2
 * (x - x2) ^ 2 + (y - y2) ^ 2 = r ^ 2
 *
 * 得到：
 * x = (y2 - y1) / (x1 - x2) * y + (x1^2 - x2^2 + y1^2 - y2^2) / (2*x2 - 2*x1)
 *
 * 记：
 * A = (y2 - y1) / (x1 - x2),
 * B = (x1^2 - x2^2 + y1^2 - y2^2) / (2*x2 - 2*x1) - x1
 * 即：
 * x = Ay + B + x1
 *
 * 带入原方程，得：
 * (Ay + B) ^ 2 + (y - y1) ^ 2 = r ^ 2，
 * 即：
 * (1 + A^2)y^2 + 2(AB - y1)y + B^2 + y1^2 - r^2 = 0
 */
@Throws(NoSolutionException::class)
fun centerPos(x1: Float, y1: Float, x2: Float, y2: Float, r: Float): Array<FloatArray> {
//    // 用坐标轴法求得关于圆心坐标的二次方程，然后用一元二次方程求解
//    val A = (y2 - y1) / (x1 - x2) // 中间数
//    val B = (x2.pow + y2.pow - x1.pow - y1.pow) / (2 * x2 - 2 * x1)  // 中间数
//    val a = 1 + A.pow
//    val b = 2 * (A * B - A * x1 - y1)
//    val c = (B - x1).pow + y1.pow - r.pow
//
//    try {
//        val solveY = solveQuadraticEquationOfOne(a, b, c) // y的解
//        val solveX = solveY.map { y -> A * y + B + x1 } // x的解
//        return arrayOf(
//            floatArrayOf(solveX[0], solveY[0]),
//            floatArrayOf(solveX[1], solveY[1])
//        ).apply {
//            sortBy {
//                it[0]
//            }
//        }
//    } catch (e: NoSolutionException) {
//        throw NoSolutionException(
//            "centerPos: no solution for pos1 = ($x1, $y1), pos2 = ($x2, $y2), r = $r"
//        )
//    }


    // 一种更简单的方式，用相似三角形
    // AB长度 = ab
    val ab = ((x1 - x2).p2 + (y1 - y2).p2).sqrt
    if (ab > r * 2) throw  NoSolutionException("没有满足条件的圆：r = $r, 两点距离 = $ab")
    // a = AB长度/2 = AC长度，AB中点C
    val a = ab / 2
    // OC长度
    val oc = (r.p2 - a.p2).sqrt
    // 根据相似三角形原理，O和C的横坐标之差dx，有：dx / OC = (y2 - y1) / AB
    // 即dx = OC * (y2 - y1) / AB
    val dx = (oc * (y2 - y1) / ab).absoluteValue
    // 同理，dy = OC * (x2 - x1) / AB
    val dy = (oc * (x2 - x1) / ab).absoluteValue

    val cx = (x1 + x2) / 2
    val cy = (y1 + y2) / 2

//    println("C点坐标：($cx, $cy)")
//    println("dx = $dx, dy = $dy")
    return if (x1 >= x2 && y1 >= y2 || x1 <= x2 && y1 <= y2) arrayOf(
        floatArrayOf(cx + dx, cy - dy),
        floatArrayOf(cx - dx, cy + dy)
    ) else arrayOf(
        floatArrayOf(cx - dx, cy - dy),
        floatArrayOf(cx + dx, cy + dy)
    )
}

/**
 * 平方
 */
val Float.p2 get() = pow(2)

/**
 * 开方
 */
val Float.sqrt get() = sqrt(this)

/**
 * 解一元二次方程，如果无解（△<0）则抛出[NoSolutionException]异常
 */
@Throws(NoSolutionException::class)
@Suppress("unused")
fun solveQuadraticEquationOfOne(a: Float, b: Float, c: Float): Array<Float> {
    val delta = b.pow(2) - 4 * a * c
    if (delta < 0) throw NoSolutionException(
        "solveQuadraticEquationOfOne: " +
                "no solution for equation { $a y^2 + $b y + c = 0 }: delta < 0"
    )
    //  x1,x2 = (-b ± √(b^2 - 4ac)) / (2a)
    val solve1 = (-b + sqrt(b * b - 4 * a * c)) / (2 * a)
    val solve2 = (-b - sqrt(b * b - 4 * a * c)) / (2 * a)
    return arrayOf(solve1, solve2)
}

class NoSolutionException(msg: String) : Exception(msg)

object PathBuilder {
    enum class PathActionType {
        moveTo,
        lineTo,
        arcFromTo
    }

    class PathAction(
        val type: PathActionType,
        vararg val args: Any
    )

    private var path: Path? = null
    private val actionList = mutableListOf<PathAction>()

    private var x = 0f
    private var y = 0f

    fun of(path: Path): PathBuilder {
        PathBuilder.path = path
        return this
    }

    fun moveTo(x: Float, y: Float) {
        actionList.add(
            PathAction(
                PathActionType.moveTo,
                x, y
            )
        )
        PathBuilder.x = x
        PathBuilder.y = y
    }

    fun lineTo(x: Float, y: Float) {
        actionList.add(
            PathAction(
                PathActionType.lineTo,
                x, y
            )
        )
        PathBuilder.x = x
        PathBuilder.y = y
    }

    fun lineDelta(dx: Float = 0f, dy: Float = 0f) {
        lineTo(x + dx, y + dy)
    }

    fun arcTo(x: Float, y: Float, r: Float, reverse: Boolean) {
        actionList.add(
            PathAction(
                PathActionType.arcFromTo,
                PathBuilder.x, PathBuilder.y, x, y, r, reverse
            )
        )
        PathBuilder.x = x
        PathBuilder.y = y
    }

    fun build() {
        if (path == null) return
        for (action in actionList) {
            when (action.type) {
                PathActionType.moveTo -> {
                    path!!.moveTo(action.args[0] as Float, action.args[1] as Float)
                }
                PathActionType.lineTo -> {
                    path!!.lineTo(action.args[0] as Float, action.args[1] as Float)
                }
                PathActionType.arcFromTo -> {
                    path!!.arcFromTo(
                        action.args[0] as Float,
                        action.args[1] as Float,
                        action.args[2] as Float,
                        action.args[3] as Float,
                        action.args[4] as Float,
                        action.args[5] as Boolean
                    )
                }
            }
        }
        path = null
    }
}
