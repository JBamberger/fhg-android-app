/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.jbamberger.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.ResponseBody;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Common class used by API responses.
 *
 * @param <T>
 */
public class ApiResponse<T> {

    @Nullable
    public final Headers headers;

    public final int code;
    @Nullable
    public final T body;
    @Nullable
    public final String errorMessage;

    public ApiResponse(@Nullable T body, @NonNull ApiResponse<?> response) {
        this.headers = response.headers;
        this.code = response.code;
        this.errorMessage = response.errorMessage;
        this.body = body;
    }

    public ApiResponse(Throwable error) {
        this.headers = null;
        code = 500;
        body = null;
        errorMessage = error.getMessage();
    }

    public ApiResponse(@NonNull Response<T> response) {
        this.headers = response.headers();
        code = response.code();
        if (response.isSuccessful()) {
            body = response.body();
            errorMessage = null;
        } else {
            String message = null;
            ResponseBody errorBody = response.errorBody();
            if (errorBody != null) {
                try {
                    message = errorBody.string();
                } catch (IOException ignored) {
                    Timber.e(ignored, "error while parsing response");
                }
            }
            if (message == null || message.trim().length() == 0) {
                message = response.message();
            }
            errorMessage = message;
            body = null;
        }
    }

    public boolean isSuccessful() {
        return code >= 200 && code < 300;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "headers=" + headers +
                ", code=" + code +
                ", body=" + body +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
