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



