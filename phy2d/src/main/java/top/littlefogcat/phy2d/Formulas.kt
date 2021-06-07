package top.littlefogcat.phy2d

import top.littlefogcat.math.FreeVector
import top.littlefogcat.math.Point
import top.littlefogcat.math.sq
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * @Author：littlefogcat
 * @Email：littlefogcat@foxmail.com
 */


/**
 * 加速度公式
 * a = F / m
 */
fun getAcceleration(F: Float, m: Float) = F / m


/**
 * 转动动能 E = 1/2 J * w^2
 *
 * [inertia] 转动惯量
 * [angularVelocity] 角速度
 */
fun getRotateEnergy(inertia: Float, angularVelocity: Float) = inertia * angularVelocity.sq / 2

/**
 * 圆盘的转动惯量 J = 1/2 m * r^2
 *
 * [m] 质量
 * [r] 半径
 */
fun getRotateInertia(m: Float, r: Float) = m * r.sq / 2


/**
 * 点[point]围绕原点旋转角度θ
 */
fun rotatePoint(point: Point, theta: Float): Point {
    val x = cos(theta) * point.x - sin(theta) * point.y
    val y = cos(theta) * point.y + sin(theta) * point.x
    return Point(x, y)
}

/**
 * 弧度转度数
 */
fun radToDegree() {

}

/**
 * 度数转弧度
 */
fun degreeToRad(degree: Float): Float {
    return (degree / 180 * PI).toFloat()
}

/**
 * 返回向量[a]在[b]上的投影的模
 */
fun getProjectionValue(a: FreeVector, b: FreeVector): Float {
    return a * b / b.absoluteValue
}

/**
 * 叉乘公式，返回叉乘的模，标量。
 *
 * 用于判断向量的方向，如果结果为正，那么[other]在[this]的左侧；
 * 如果结果为0，[other]和[this]在一条直线上；
 * 如果结果为负，[other]在[this]的右侧。
 */
fun FreeVector.x(other: FreeVector): Float {
    return x * other.y - other.x * y
}