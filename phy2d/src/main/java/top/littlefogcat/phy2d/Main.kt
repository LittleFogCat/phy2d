package top.littlefogcat.phy2d

import top.littlefogcat.phy2d.shape.Polygon
import top.littlefogcat.phy2d.utils.enableLog

fun main() {

    val polygon = Polygon(
            0, 0,
            1, 2,
            3, 2,
            1, 3,
            2, 5,
            0, 4
    )
//
//    println(polygon.containsPoint(Point(0.5f, 3f))) // t
//    println(polygon.containsPoint(Point(1.5f, 3f))) // f
//    println(polygon.containsPoint(Point(0f, 4f))) // t
//    println(polygon.containsPoint(Point(0f, 2f))) // t
//    println(polygon.containsPoint(Point(-1f, 2f))) // f
//    println(polygon.containsPoint(Point(1, 3))) // t
//
//    val v = FixedVector(1, 3, 2, 5f)
//    println(v.atWhichSide(0f, 4f))
    val polygon2 = Polygon(
            1, 0,
            2, 0,
            2, 1,
            1, 1
    )
    val polygon3 = Polygon(
            1, 0,
            2, 0,
            2, 1,
            0.6, 1
    )
    val polygon4 = Polygon(
            1, 0,
            2, 0,
            2, 1,
            0.4, 1
    )
    val polygon5 = Polygon(
            -1, -1,
            4, -1,
            4, 6,
            -1, 6
    )
    enableLog(1)

//    println(checkCollision(polygon, polygon2)) // false
//    println(checkCollision(polygon, polygon3)) // false
//    println(checkCollision(polygon, polygon4)) // true
//    println(checkCollision(polygon, polygon5)) // true

//    println(polygon.containsPoint(Point(0, 0))) // true
//    println(polygon.containsPoint(Point(0, 2))) // true
//    println(polygon.containsPoint(Point(0, 4))) // true
//    println(polygon.containsPoint(Point(1, 3))) // true
//    println(polygon.containsPoint(Point(0.5, 2))) // true
//    println(polygon.containsPoint(Point(0.5, 3))) // true
//    println(polygon.containsPoint(Point(0.4, 1)))
//    println("----")
//    println(polygon.containsPoint(Point(-1, -1)))
//    println(polygon.containsPoint(Point(-1, 0)))
//    println(polygon.containsPoint(Point(-1, 2)))
//    println(polygon.containsPoint(Point(-1, 3)))
//    println(polygon.containsPoint(Point(0, 4.5)))
//    println(polygon.containsPoint(Point(0, 5)))
//    println(polygon.containsPoint(Point(0.5, 4.5)))
//    println(polygon.containsPoint(Point(5, 4.5)))
//    println(polygon.containsPoint(Point(5, 0)))
//    println(polygon.containsPoint(Point(5, 2)))
//    println(polygon.containsPoint(Point(5, 5)))
//    println(polygon.containsPoint(Point(5, -5)))

//    println(getIntersection(FixedVector(0.1, 1, 100, 1), FixedVector(0, 0, 1, 2)))

    val shape = Polygon(
            0, 0,
            1, 0,
            1, 1,
            0, 1
    )
    println("0: " + shape.vertices)
    for (i in 0 until 90) {
        shape.rotate(1f)
        println("${i + 1}: " + shape.vertices)
    }

}