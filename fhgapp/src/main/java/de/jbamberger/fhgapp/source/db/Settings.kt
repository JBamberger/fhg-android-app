package de.jbamberger.fhgapp.source.db

import android.app.Application
import android.content.SharedPreferences
import de.jbamberger.fhgapp.R
import java.util.*
import javax.inject.Inject

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */


class Settings @Inject
constructor(private val app: Application, private val prefs: SharedPreferences) {

    val vPlanShowAll: Boolean
        get() = prefs.getBoolean(app.getString(R.string.settings_grade_show_all_key), true)
    val vPlanGrades: Set<String>
        get() = prefs.getStringSet(app.getString(R.string.settings_grade_key), Collections.emptySet())
    val vPlanCourses: String
        get() = prefs.getString(app.getString(R.string.settings_course_key), "")

}
