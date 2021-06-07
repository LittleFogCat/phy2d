package top.littlefogcat.bouncedemo

import android.app.Service
import android.content.Intent
import android.os.IBinder

class MyService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("Thread = ${Thread.currentThread()}")
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
}