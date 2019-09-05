package de.jbamberger.fhgapp

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment.application

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
@Ignore("robolectric does not work with api level 29")
@RunWith(RobolectricTestRunner::class)
class SettingsTest {

    private val ALL : String = application.getString(R.string.settings_grade_show_all_key)
    private val GRADES : String = application.getString(R.string.settings_grade_key)
    private val COURSES : String = application.getString(R.string.settings_course_key)

    lateinit var settings: Settings
    lateinit var prefs: SharedPreferences

    @Before
    fun setUp() {
        prefs = PreferenceManager.getDefaultSharedPreferences(application)
        settings = Settings(application, prefs)
    }

    @Test
    fun showAll_notSet() {
        assertThat(settings.vPlanShowAll, equalTo(true))
    }

    @Test
    fun showAll_Set() {

        settings.vPlanShowAll = false
        assertThat(settings.vPlanShowAll, equalTo(false))
        assertThat(prefs.getBoolean(ALL, true), equalTo(false))

        settings.vPlanShowAll = true
        assertThat(settings.vPlanShowAll, equalTo(true))
        assertThat(prefs.getBoolean(ALL, false), equalTo(true))
    }

    @Test
    fun testVPlanGrades_notSet() {
        assertThat(settings.vPlanGrades, equalTo(emptySet()))
    }

    @Test
    fun testVPlanGrades_Set() {

        settings.vPlanGrades = emptySet()
        assertThat(settings.vPlanGrades, equalTo(emptySet()))
        assertThat(prefs.getStringSet(GRADES, null), equalTo(emptySet()))

        settings.vPlanGrades = setOf("Hello", "World")
        assertThat(settings.vPlanGrades, equalTo(setOf("Hello", "World")))
        assertThat(prefs.getStringSet(GRADES, null), equalTo(setOf("Hello", "World")))
    }

    @Test
    fun testVPlanCourses_notSet() {
        assertThat(settings.vPlanCourses, equalTo(emptySet()))
    }

    @Test
    fun testVPlanCourses_Set() {

        settings.vPlanCourses = emptySet()
        assertThat(settings.vPlanCourses, equalTo(emptySet()))
        assertThat(prefs.getString(COURSES, null), equalTo(""))

        settings.vPlanCourses = setOf("Hello", "World")
        assertThat(settings.vPlanCourses, equalTo(setOf("Hello", "World")))
        assertThat(prefs.getString(COURSES, null), equalTo("Hello,World"))
    }
}