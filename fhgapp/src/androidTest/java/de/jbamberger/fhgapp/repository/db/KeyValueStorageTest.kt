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

import androidx.preference.PreferenceManager
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import de.jbamberger.fhgapp.repository.FhgRepositoryModule
import de.jbamberger.fhgapp.repository.data.VPlan
import de.jbamberger.fhgapp.repository.data.VPlanDay
import de.jbamberger.fhgapp.repository.data.VPlanHeader
import de.jbamberger.fhgapp.repository.data.VPlanRow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
@SmallTest
class KeyValueStorageTest {

    private lateinit var store: KeyValueStorage

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.edit().clear().commit()
        val moshi = FhgRepositoryModule().providesMoshi()
        store = KeyValueStorage(moshi, prefs)
    }

    @Test
    fun test_saveAndLoadString() {
        store.save("s", "Hello World.")
        assertEquals("Hello World.", store.get<String>("s"))
    }

    @Test
    fun test_saveAndLoadComplexObject() {
        val o = VPlanDay(
            VPlanHeader("a", "a", "c"),
            listOf(VPlanRow("a", false, "b", "c", "d", "e", "f", false))
        )
        store.save("c", o)
        assertEquals(o, store.get<VPlanDay>("c"))
    }

    @Test
    fun test_restoreBroken() {
        store.save("c", "Hello broken Object")
        assertEquals(null, store.get<VPlanDay>("c"))
    }

    @Test
    fun test_loadNotPresent() {
        assertEquals(null, store.get<String>("b"))
    }

    @Test
    fun legacy() {
        val x = "{\"day1\":" +
                "{\"dateAndDay\":\"b\",\"lastUpdated\":\"a\",\"motd\":\"bla\",\"vPlanRows\":[" +
                "{\"content\":\"---\",\"grade\":\"314\",\"hour\":\"Entfall\"," +
                "\"isMarkedNew\":false,\"isOmitted\":false," +
                "\"kind\":\"\\u003cs\\u003eHenß\\u003c/s\\u003e\",\"room\":\"5a\"," +
                "\"subject\":\"1\"}]}, \"day2\":" +
                "{\"dateAndDay\":\"\",\"lastUpdated\":\"\",\"motd\":\"\",\"vPlanRows\":[]}}"

        store.save("a", x)
        assertNull(store.get<VPlan>("a"))
    }
}



