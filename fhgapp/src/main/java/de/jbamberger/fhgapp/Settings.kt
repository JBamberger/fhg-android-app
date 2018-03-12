package de.jbamberger.fhgapp

import android.app.Application
import android.content.SharedPreferences
import java.util.*
import javax.inject.Inject

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */


class Settings @Inject
constructor(private val app: Application, private val prefs: SharedPreferences) {

    var vPlanShowAll: Boolean
        get() = prefs.getBoolean(app.getString(R.string.settings_grade_show_all_key), true)
        set(value) = prefs.edit()
                .putBoolean(app.getString(R.string.settings_grade_show_all_key), value)
                .apply()
    var vPlanGrades: Set<String>
        get() = prefs.getStringSet(app.getString(R.string.settings_grade_key), Collections.emptySet())
        set(value) = prefs.edit()
                .putStringSet(app.getString(R.string.settings_grade_key), value)
                .apply()
    var vPlanCourses: Set<String>
        get() = prefs.getString(app.getString(R.string.settings_course_key), "")
                .split(",")
                .map { it.trim() }
                .filter { !it.isBlank() }
                .toSet()
        set(value) = prefs.edit()
                .putString(app.getString(R.string.settings_course_key), value.joinToString(separator = ","))
                .apply()

    val vPlanSettings: VPlanSettings
        get() = VPlanSettings(
                vPlanShowAll,
                vPlanGrades,
                vPlanCourses
        )

    data class VPlanSettings(val showAll: Boolean, val grades: Set<String>, val courses: Set<String>)
}
