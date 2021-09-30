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

package de.jbamberger.fhgapp.repository.api

import okhttp3.Headers
import retrofit2.Response
import timber.log.Timber
import java.io.IOException

/**
 * Network response wrapper around a retrofit Response that contains either a success or a failure.
 *
 * @param <T> response content
</T> */
internal class ApiResponse<T> {


    val code: Int
    private val headers: Headers?
    val body: T?
    val errorMessage: String?

    val isSuccessful: Boolean
        get() = code in 200..299

    constructor(body: T?, response: ApiResponse<*>) {
        this.headers = response.headers
        this.code = response.code
        this.errorMessage = response.errorMessage
        this.body = body
    }

    constructor(error: Throwable) {
        this.headers = null
        this.code = 500
        this.body = null
        this.errorMessage = error.message
    }

    constructor(response: Response<T>) {
        this.headers = response.headers()
        code = response.code()
        if (response.isSuccessful) {
            body = response.body()
            errorMessage = null
        } else {
            var message: String? = null
            val errorBody = response.errorBody()
            if (errorBody != null) {
                try {
                    message = errorBody.string()
                } catch (e: IOException) {
                    Timber.e(e, "error while parsing response")
                }

            }
            if (message == null || message.trim { it <= ' ' }.isEmpty()) {
                message = response.message()
            }
            errorMessage = message
            body = null
        }
    }

    override fun toString(): String {
        return "ApiResponse{" +
                "headers=" + headers +
                ", code=" + code +
                ", body=" + body +
                ", errorMessage='" + errorMessage + '\'' +
                '}'
    }
}
