package de.jbamberger.fhgapp

import android.content.SharedPreferences
import android.preference.PreferenceManager
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment.application

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
@RunWith(RobolectricTestRunner::class)
class SettingsTest {

    val ALL = application.getString(R.string.settings_grade_show_all_key)
    val GRADES = application.getString(R.string.settings_grade_key)
    val COURSES = application.getString(R.string.settings_course_key)

    lateinit var settings: Settings
    lateinit var prefs: SharedPreferences

    @Before
    fun setUp() {
        prefs = PreferenceManager.getDefaultSharedPreferences(application)
        settings = Settings(application, prefs)
    }

    @Test
    fun showAll_notSet() {
        assertThat(settings.vPlanShowAll, `is`(equalTo(true)))
    }

    @Test
    fun showAll_Set() {

        settings.vPlanShowAll = false
        assertThat(settings.vPlanShowAll, `is`(equalTo(false)))
        assertThat(prefs.getBoolean(ALL, true), `is`(equalTo(false)))

        settings.vPlanShowAll = true
        assertThat(settings.vPlanShowAll, `is`(equalTo(true)))
        assertThat(prefs.getBoolean(ALL, false), `is`(equalTo(true)))
    }

    @Test
    fun testVPlanGrades_notSet() {
        assertThat(settings.vPlanGrades, `is`(equalTo(emptySet())))
    }

    @Test
    fun testVPlanGrades_Set() {

        settings.vPlanGrades = emptySet()
        assertThat(settings.vPlanGrades, `is`(equalTo(emptySet())))
        assertThat(prefs.getStringSet(GRADES, null), `is`(equalTo(emptySet())))

        settings.vPlanGrades = setOf("Hello", "World")
        assertThat(settings.vPlanGrades, `is`(equalTo(setOf("Hello", "World"))))
        assertThat(prefs.getStringSet(GRADES, null), `is`(equalTo(setOf("Hello", "World"))))
    }

    @Test
    fun testVPlanCourses_notSet() {
        assertThat(settings.vPlanCourses, `is`(equalTo(emptySet())))
    }

    @Test
    fun testVPlanCourses_Set() {

        settings.vPlanCourses = emptySet()
        assertThat(settings.vPlanCourses, `is`(equalTo(emptySet())))
        assertThat(prefs.getString(COURSES, null), `is`(equalTo("")))

        settings.vPlanCourses = setOf("Hello", "World")
        assertThat(settings.vPlanCourses, `is`(equalTo(setOf("Hello", "World"))))
        assertThat(prefs.getString(COURSES, null), `is`(equalTo("Hello,World")))
    }

    @Test
    fun fail_test() {
        fail()
    }
}