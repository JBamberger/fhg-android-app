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
