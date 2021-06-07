package top.littlefogcat.elusion.model

import android.content.Context
import android.content.SharedPreferences

/**
 * @Author：littlefogcat
 * @Email：littlefogcat@foxmail.com
 */
object PreferencesManager {
    private lateinit var sp: SharedPreferences
    private val editor get() = sp.edit()

    fun init(context: Context) {
        sp = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
    }

    fun getString(key: String, defVal: String) = sp.getString(key, defVal)
    fun getInt(key: String, defVal: Int) = sp.getInt(key, defVal)
    fun getFloat(key: String, defVal: Float) = sp.getFloat(key, defVal)
    fun getBoolean(key: String, defVal: Boolean) = sp.getBoolean(key, defVal)

    fun putString(key: String, value: String) {
        editor.putString(key, value).apply()
    }

    fun putInt(key: String, value: Int) {
        editor.putInt(key, value).apply()
    }

    fun putFloat(key: String, value: Float) {
        editor.putFloat(key, value).apply()
    }

    fun putBoolean(key: String, value: Boolean) {
        editor.putBoolean(key, value).apply()
    }
}