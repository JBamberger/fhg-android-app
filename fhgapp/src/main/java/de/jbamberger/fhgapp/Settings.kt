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

package de.jbamberger.fhgapp

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */


class Settings @Inject
constructor(
    @ApplicationContext private val context: Context,
    private val prefs: SharedPreferences
) {

    var vPlanShowAll: Boolean
        get() = prefs.getBoolean(context.getString(R.string.settings_grade_show_all_key), true)
        set(value) = prefs.edit()
            .putBoolean(context.getString(R.string.settings_grade_show_all_key), value)
            .apply()
    var vPlanGrades: Set<String>
        // should be safe due to default value
        get() = prefs.getStringSet(
            context.getString(R.string.settings_grade_key),
            Collections.emptySet()
        )!!
        set(value) = prefs.edit()
            .putStringSet(context.getString(R.string.settings_grade_key), value)
            .apply()
    var vPlanCourses: Set<String>
        // should be safe due to default val
        get() = prefs.getString(context.getString(R.string.settings_course_key), "")!!
            .split(",")
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .toSet()
        set(value) = prefs.edit()
            .putString(
                context.getString(R.string.settings_course_key),
                value.joinToString(separator = ",")
            )
            .apply()

    val vPlanSettings: VPlanSettings
        get() = VPlanSettings(
            vPlanShowAll,
            vPlanGrades,
            vPlanCourses
        )

    data class VPlanSettings(
        val showAll: Boolean,
        val grades: Set<String>,
        val courses: Set<String>
    )
}
