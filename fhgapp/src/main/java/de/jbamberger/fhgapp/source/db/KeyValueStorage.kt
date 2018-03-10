package de.jbamberger.fhgapp.source.db

import android.content.SharedPreferences
import android.util.Base64
import com.google.gson.Gson
import javax.inject.Inject

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

class KeyValueStorage @Inject
constructor(private val gson: Gson, private val prefs: SharedPreferences) {

    fun save(key: String, value: Any) {
        val json: String = gson.toJson(value)
        val base64: ByteArray = Base64.encode(json.toByteArray(), Base64.DEFAULT)
        prefs.edit().putString(key, String(base64)).apply()
    }

    inline fun <reified T> get(key: String): T? {
        return get<T>(key, T::class.java)
    }

    fun <T> get(key: String, type: Class<T>): T? {
        val base64 = prefs.getString(key, null) ?: return null
        val json = String(Base64.decode(base64, Base64.DEFAULT))
        return gson.fromJson(json, type)
    }
}
