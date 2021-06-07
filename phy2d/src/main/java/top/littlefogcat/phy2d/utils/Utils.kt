@file:Suppress("unused")

package top.littlefogcat.phy2d.utils

import top.littlefogcat.math.*
import top.littlefogcat.phy2d.base.Object
import top.littlefogcat.phy2d.shape.Circle
import top.littlefogcat.phy2d.shape.CombinedShape
import top.littlefogcat.phy2d.shape.Polygon
import top.littlefogcat.phy2d.shape.Shape
import java.lang.IllegalStateException
import kotlin.math.*


inline fun <T> Iterable<T>.sumByFloat(selector: (T) -> Float): Float {
    var sum = 0f
    for (element in this) {
        sum += selector(element)
    }
    return sum
}


/**
 * 解一元二次方程
 */
fun solveQuadraticEquationOfOne(a: Float, b: Float, c: Float): Array<Float>? {
    val delta = b.sq - 4 * a * c
    if (delta < 0) {
        return null
    }
    //  x1,x2 = (-b ± √△ ) / (2a)
    return arrayOf(
        (-b + sqrt(delta)) / (2 * a),
        (-b - sqrt(delta)) / (2 * a)
    )
}

/**
 * 保留[r]位小数
 */
fun Float.round(r: Int): Float {
    val dim = 10f.pow(r)
    return (this * dim).roundToInt() / dim
}

/**
 * 如果接近整数，就直接舍入为整数
 * [accuracy] 精度
 */
fun Float.roundIfCan(accuracy: Float = 0.0001f): Float {
    val round = roundToInt().toFloat()
    if (abs(this - round) < accuracy) {
        return round
    }
    return this
}

// ---------------- Vector --------------------

/**
 * 是否是0向量
 */
fun FreeVector.isZero(): Boolean {
    return x == 0f && y == 0f
}

/**
 * 返回两个向量是否方向相同
 */
fun isSameDirection(v1: FreeVector, v2: FreeVector): Boolean {
    if (v1.isZero()) return v2.isZero()
    if (v2.isZero()) return v1.isZero()
    if (v1.x == 0f) return v2.x == 0f && v1.y > 0 == v2.y > 0
    if (v1.y == 0f) return v2.y == 0f && v1.x > 0 == v2.x > 0
    if (v2.x == 0f) return false
    return v1.x > 0 == v2.x > 0 && (v1.y / v1.x - v2.y / v2.x).isZero()
}

// ---------------------- 碰撞 Collision ------------------------
private const val TAG = "Utils"

/**
 * 两个物体碰撞
 */
fun checkAndApplyCollision(obj1: Object, obj2: Object): Point? {
    if (obj1 === obj2) return null
    val collidePoint = checkCollision(obj1, obj2) ?: return null
    // 产生碰撞
    val normal = getNormal(obj2.shape, collidePoint)
    applyCollisionImpulse(obj1, obj2, collidePoint, normal, obj1.elasticity * obj2.elasticity)
    return collidePoint
}

/**
 * 检查两个多边形是否产生碰撞
 * 返回碰撞点
 */
fun checkCollision(polygon1: Polygon, polygon2: Polygon): Point? {
    polygon1.vertices.forEachIndexed { index, _ ->
        val rawPoint = polygon1.getVertexRawPosition(index) ?: return null
        if (polygon2.containsPoint(rawPoint)) return rawPoint
    }
    polygon2.vertices.forEachIndexed { index, _ ->
        val rawPoint = polygon2.getVertexRawPosition(index) ?: return null
        if (polygon1.containsPoint(rawPoint)) return rawPoint
    }
    return null
}

/**
 * 检查两个圆形是否产生碰撞
 * 返回碰撞点
 */
fun checkCircleCollision(c1: Circle, c2: Circle): Point? {
    val pos1 = c1.rawPosition as Point
    val pos2 = c2.rawPosition as Point
    val dist = (pos1 - pos2).absoluteValue
    if (dist > c1.r + c2.r) return null
    // 计算碰撞点
    // 圆心坐标
    val o1 = c1.rawPosition
    val o2 = c2.rawPosition
    if (o1 == null || o2 == null) return null
    val vec12 = o2 - o1 // o1指向o2的向量
    return o1 + vec12 * c1.r / (c1.r + c2.r)
}

fun checkCollision(shape1: Shape, shape2: Shape): Point? {
    if (shape1 is Circle && shape2 is Circle) return checkCircleCollision(shape1, shape2)
    else if (shape1 is Polygon && shape2 is Polygon) return checkCollision(shape1, shape2)
    else if (shape1 is CombinedShape) {
        shape1.shapes.forEach {
            val cp = checkCollision(it, shape2)
            if (cp != null) return cp
        }
    } else if (shape2 is CombinedShape) {
        shape2.shapes.forEach {
            val cp = checkCollision(shape1, it)
            if (cp != null) return cp
        }
    }
    return null
}

//fun checkCollision(shape1: CombinedShape, shape2: CombinedShape): Boolean {
//    shape1.shapes.forEach { s1 ->
//        shape2.shapes.forEach { s2 ->
//            if (s1 is Circle && s2 is Circle) {
//                if (checkCollision(s1, s2)) {
//                    return true
//                }
//            } else if (s1 is Polygon && s2 is Polygon) {
//                if (checkCollision(s1, s2)) return true
//            } else return checkCollision(s1, s2)
//        }
//    }
//    return false
//}

fun checkCollision(obj1: Object, obj2: Object): Point? {
    return checkCollision(obj1.shape, obj2.shape)
}

/*
applyCollisionImpulse: function (bodyA, bodyB, contactPoint, normal, e) {
var cp = contactPoint,
rA = cp.sub(bodyA.centroid),
rB = cp.sub(bodyB.centroid),
vA = bodyA.velocity.add(rA.scalarCross(bodyA.angularVelocity)),
vB = bodyB.velocity.add(rB.scalarCross(bodyB.angularVelocity)),
invIA = bodyA.inverseInertia,
invIB = bodyB.inverseInertia,
invMA = bodyA.inverseMass,
invMB = bodyB.inverseMass,
vr = vA.sub(vB),
rsnA = rA.cross(normal),
rsnB = rB.cross(normal),
kn = invMA + invMB + rsnA * rsnA * invIA + rsnB * rsnB * invIB,
jn = -(1 + e) * vr.dot(normal) / kn,
impulse = normal.mul(jn);

bodyA.applyImpulse(impulse, cp);
bodyB.applyImpulse(impulse.negate(), cp);
}
 */

/**
 * 假设碰撞点[point]在形状[shape]的边A上，返回A的法向量
 */
fun getNormal(shape: Shape, point: Point): FreeVector {
    if (shape is Circle) {
        val o = shape.rawPosition // 圆心
            ?: throw IllegalStateException("Shape $shape's rawPosition is null. A shape must attach to a Object.")
        val vec = FreeVector(o, point) // 法向量方向
        return FreeVector.of(vec, 1f)
    }
    TODO()
}

/**
 * 对两个碰撞的物体分别施以冲量.
 *
 * [objA] 撞的物体
 * [objB] 被撞的物体
 * cp: 碰撞点坐标
 * normal: 被撞的物体边的法向量
 * e: 弹性系数
 */
fun applyCollisionImpulse(objA: Object, objB: Object, cp: Point, normal: FreeVector, e: Float) {
    val rA = cp - objA.position
    val rB = cp - objA.position
    val vA = objA.velocity + rA.cross(objA.angularVelocity)
    val vB = objB.velocity + rB.cross(objA.angularVelocity)
//    Log.d(TAG, "applyCollisionImpulse: vA vB = $vA $vB")
    val invIA = objA.invInertia
    val invIB = objB.invInertia
    val invMA = objA.invMass
    val invMB = objA.invMass
    val vr = vA - vB
//    Log.d(TAG, "applyCollisionImpulse: vr = $vr")
    val rsnA = rA.cross(normal)
    val rsnB = rB.cross(normal)
//    Log.d(TAG, "applyCollisionImpulse: rsnA rsnB = $rsnA $rsnB")
    val kn = invMA + invMB + rsnA * rsnA * invIA + rsnB * rsnB * invIB
//    Log.d(TAG, "applyCollisionImpulse: $invMA $invMB $rsnA $rsnA $invIA $rsnB $rsnB $invIB")
//    Log.d(TAG, "applyCollisionImpulse: kn = $kn")
    val jn = -(1 + e) * (vr * normal) / kn
//    Log.d(TAG, "applyCollisionImpulse: jn = $jn")
    val impulse = normal * jn
//    Log.d(TAG, "applyCollisionImpulse: impulse = $impulse")
    objA.giveImpulse(impulse, cp - objA.position)
    objB.giveImpulse(-impulse, cp - objB.position)
}