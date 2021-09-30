/*
 *    Copyright 2021 Jannik Bamberger
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.jbamberger.fhgapp.repository.db

import android.content.SharedPreferences
import android.util.Base64
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import javax.inject.Inject

/**
 * Key-value storage backed by SharedPreferences of the application. Data is serialized to json and
 * then encoded as Base64. Therefore, only objects serializable by Moshi can be saved with this
 * class.
 *
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
