package top.littlefogcat.phy2d.scene

import top.littlefogcat.phy2d.base.Force
import top.littlefogcat.phy2d.base.Object
import top.littlefogcat.math.Point
import top.littlefogcat.phy2d.shape.Border
import top.littlefogcat.phy2d.utils.checkAndApplyCollision

/**
 * 一个容纳物体的场景，包含若干个物体。
 * A area to contain 2D objects.
 */
abstract class Scene(
    val border: Border
) {
    companion object {
        const val TAG = "Scene"
    }

    /**
     * Map contains all objects in this scope.
     */
    val objects: MutableMap<Any, Object> = mutableMapOf()

    val objectsRemovePending = mutableSetOf<Object>()

    /**
     * 场景对物体的作用力
     */
    val sceneForces = mutableMapOf<Object, Force>()

    /**
     * 是否检查物体抵达边界
     */
    var shouldCheckObjectReachEdge = true

    private var onBorderReachedListener: OnBorderReachedListener? = null

    /**
     * Add an object to this scope.
     */
    fun addObject(obj: Object, point: Point) {
        objects[obj.identify] = obj
        obj.attachTo(this, point)
        initForceToObject(obj)?.let {
            obj.giveForce(it)
            sceneForces.put(obj, it)
        }
    }

    fun setBorder(l: Int, t: Int, r: Int, b: Int) {
        border.left = l
        border.top = t
        border.right = r
        border.bottom = b
    }

    /**
     * 在下一时刻移除物体[obj]
     */
    fun removeObject(obj: Object) {
        objectsRemovePending.add(obj)
    }

    /**
     * 在下一时刻移除所有物体
     */
    fun removeAllObject() {
        objectsRemovePending.addAll(objects.values)
    }

    /**
     * 初始化场景对一个物体的力。
     */
    open fun initForceToObject(obj: Object): Force? = null

    fun getForceByObject(obj: Object) = sceneForces[obj]

    open fun timerTick() {
        // 移除预定的objects
        removePendingObjects()
        // 处理场景内物体的变化
        handleObjectsTimerTick()
        // 处理碰撞
        checkCollisions()
    }

    open fun handleObjectsTimerTick() {
        try {
            objects.values.forEach {
                defaultHandleObjectTimerTick(it)
            }
        } catch (e: Exception) {
        }

    }

    /**
     * 碰撞检测
     */
    open fun checkCollisions() {
        val objectList = objects.values.toList()
        for (i in 0 until objectList.size - 1) {
            for (j in i + 1 until objectList.size) {
                checkAndApplyCollision(objectList[i], objectList[j])
            }
        }
    }

    fun removePendingObjects() {
        try {
            objectsRemovePending.forEach { obj ->
                objects.remove(obj.identify)
                obj.detach()
                sceneForces.remove(obj)
            }
        } catch (e: Exception) {
            e.printStackTrace() // TODO: 2021/5/11 ConcurrentModificationException
        }
        objectsRemovePending.clear()
    }

    /**
     * 预处理物体运动状态变化
     */
    open fun preObjectStateChanged(obj: Object) {}

    /**
     * 物体运动状态变化
     */
    open fun afterObjectStateChanged(obj: Object) {}

    fun checkObjectReachEdge(obj: Object) {
        obj.apply {
            if (left.x <= border.left && obj.velocity.x < 0) onObjectArriveLeft(obj)
            if (top.y <= border.top && obj.velocity.y < 0) onObjectArriveTop(obj)
            if (right.x >= border.right && obj.velocity.x > 0) onObjectArriveRight(obj)
            if (bottom.y >= border.bottom && obj.velocity.y > 0) onObjectArriveBottom(obj)
            if (right.x < border.left || left.x > border.right || top.y > border.bottom || bottom.y < border.top) {
                onObjectLeaveScene(obj)
            }
        }
//        obj.position.let {
//            val pos = it
//            val shape = obj.shape
//            // 物体的在场景中的位置
//            val left = pos + shape.left
//            val right = pos + shape.right
//            val top = pos + shape.top
//            val bottom = pos + shape.bottom
//            if (left <= border.left && obj.velocity.x < 0) onObjectArriveLeft(obj)
//            if (top <= border.top && obj.velocity.y < 0) onObjectArriveTop(obj)
//            if (right >= border.right && obj.velocity.x > 0) onObjectArriveRight(obj)
//            if (bottom >= border.bottom && obj.velocity.y > 0) onObjectArriveBottom(obj)
//            if (right < border.left || left > border.right || top > border.bottom || bottom < border.top) {
//                onObjectLeaveScene(obj)
//            }
//        }
    }

    /**
     * 物理运动状态
     */
    open fun defaultHandleObjectTimerTick(obj: Object) {
        obj.apply {
            preObjectStateChanged(this)
            val oldVelocity = velocity
            val newVelocity = velocity + accelerate
            velocity = newVelocity
            if (velocity.absoluteValue > maxVelocity) {
                velocity.setValue(maxVelocity)
            }
            shape.rotate(angularVelocity)
            position += (oldVelocity + newVelocity) / 2f
            afterObjectStateChanged(this)
            if (shouldCheckObjectReachEdge) {
                // 检查物体是否触碰到边界
                checkObjectReachEdge(this)
            }
        }
    }
    //            it.apply {
//                preObjectStateChanged(it)
//                position.x += velocity.x
//                position.y += velocity.y
//                velocity.x += accelerate.x
//                velocity.y += accelerate.y
//                if (velocity.absoluteValue > maxVelocity) {
//                    velocity.setValue(maxVelocity)
//                }
//                shape.rotate(angularVelocity)
//                afterObjectStateChanged(it)
//            }
//            if (shouldCheckObjectReachEdge) {
//                // 检查物体是否触碰到边界
//                checkObjectReachEdge(it)
//            }


    /*
     * 当物体触碰到场景边界
     */
    open fun onObjectArriveLeft(obj: Object) {
        onBorderReachedListener?.onObjectArriveLeft(obj)
    }

    open fun onObjectArriveTop(obj: Object) {
        onBorderReachedListener?.onObjectArriveTop(obj)
    }

    open fun onObjectArriveRight(obj: Object) {
        onBorderReachedListener?.onObjectArriveRight(obj)
    }

    open fun onObjectArriveBottom(obj: Object) {
        onBorderReachedListener?.onObjectArriveBottom(obj)
    }

    fun setOnBorderReachedListener(listener: OnBorderReachedListener) {
        this.onBorderReachedListener = listener
        shouldCheckObjectReachEdge = true
    }

    /**
     * 当物体离开此场景。
     * 只应该执行一次。
     */
    open fun onObjectLeaveScene(obj: Object) {
        obj.onLeaveScene(this)
    }
}