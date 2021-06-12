package de.jbamberger.fhg.repository.db

import android.content.SharedPreferences
import android.util.Base64
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import javax.inject.Inject

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

internal class KeyValueStorage @Inject
constructor(private val moshi: Moshi, private val prefs: SharedPreferences) {

    fun <T : Any> save(key: String, value: T) {
        val json: String = moshi.adapter<T>(value::class.java).toJson(value)
        val base64: ByteArray = Base64.encode(json.toByteArray(), Base64.DEFAULT)
        prefs.edit().putString(key, String(base64)).apply()
    }

    inline fun <reified T> get(key: String): T? {
        return get(key, T::class.java)
    }

    fun <T> get(key: String, type: Class<T>): T? {
        val base64 = prefs.getString(key, null) ?: return null
        val json = String(Base64.decode(base64, Base64.DEFAULT))
        return try {
            val adapter = moshi.adapter(type)
            adapter.fromJson(json)
        } catch (e: JsonDataException) {
            null
        }
    }
}
