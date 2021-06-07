package top.littlefogcat.phy2d.utils

private var enableLog = false
private var logLevel = 0
private var onlyLevel = -1

internal fun enableLog(enable: Boolean = true) {
    enableLog = enable
}

internal fun enableLog(level: Int) {
    logLevel = level
}

internal fun enableLogOnly(level: Int) {
    onlyLevel = level
}

internal fun log(msg: Any) {
    log(msg, 0)
}

internal fun log(msg: Any, level: Int) {
    if (enableLog && level >= logLevel || level == onlyLevel)
        println(msg)
}