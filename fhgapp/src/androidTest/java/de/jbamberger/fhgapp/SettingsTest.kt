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

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
@SmallTest
class SettingsTest {

    private lateinit var ALL: String
    private lateinit var GRADES: String
    private lateinit var COURSES: String

    private lateinit var settings: Settings
    private lateinit var prefs: SharedPreferences

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        ALL = context.getString(R.string.settings_grade_show_all_key)
        GRADES = context.getString(R.string.settings_grade_key)
        COURSES = context.getString(R.string.settings_course_key)

        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.edit().clear().commit()
        settings = Settings(context, prefs)

    }

    @Test
    fun showAll_notSet() {
        assertTrue(settings.vPlanShowAll)
    }

    @Test
    fun showAll_Set() {

        settings.vPlanShowAll = false
        assertFalse(settings.vPlanShowAll)
        assertFalse(prefs.getBoolean(ALL, true))

        settings.vPlanShowAll = true
        assertTrue(settings.vPlanShowAll)
        assertTrue(prefs.getBoolean(ALL, false))
    }

    @Test
    fun testVPlanGrades_notSet() {
        assertEquals(emptySet<String>(), settings.vPlanGrades)
    }

    @Test
    fun testVPlanGrades_Set() {

        settings.vPlanGrades = emptySet()
        assertEquals(emptySet<String>(), settings.vPlanGrades)
        assertEquals(emptySet<String>(), prefs.getStringSet(GRADES, null))

        settings.vPlanGrades = setOf("Hello", "World")
        assertEquals(setOf("Hello", "World"), settings.vPlanGrades)
        assertEquals(setOf("Hello", "World"), prefs.getStringSet(GRADES, null))
    }

    @Test
    fun testVPlanCourses_notSet() {
        assertEquals(emptySet<String>(), settings.vPlanCourses)
    }

    @Test
    fun testVPlanCourses_Set() {
        settings.vPlanCourses = emptySet()
        assertEquals(emptySet<String>(), settings.vPlanCourses)
        assertEquals("", prefs.getString(COURSES, null))

        settings.vPlanCourses = setOf("Hello", "World")
        assertEquals(setOf("Hello", "World"), settings.vPlanCourses)
        assertEquals("Hello,World", prefs.getString(COURSES, null))
    }
}