package de.jbamberger.fhg.repository.db

import android.preference.PreferenceManager
import de.jbamberger.fhg.repository.api.NetModule
import de.jbamberger.fhg.repository.data.VPlan
import de.jbamberger.fhg.repository.data.VPlanDay
import de.jbamberger.fhg.repository.data.VPlanHeader
import de.jbamberger.fhg.repository.data.VPlanRow
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment.application

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
@RunWith(RobolectricTestRunner::class)
class KeyValueStorageTest {

    internal lateinit var store: KeyValueStorage

    @Before
    fun setUp() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(application)
        val moshi = NetModule().providesMoshi()
        store = KeyValueStorage(moshi, prefs)
    }

    @Test
    fun test_saveAndLoadString() {
        store.save("s", "Hello World.")
        assertThat(store.get<String>("s")).isEqualTo("Hello World.")
    }

    @Test
    fun test_saveAndLoadComplexObject() {
        val o = VPlanDay(VPlanHeader("a", "a", "c"),
                listOf(VPlanRow(
                        "a", false, "b", "c",
                        "d", "e", "f", false/*,
                        "g", "h", "i", "j"*/)))
        store.save("c", o)
        assertThat(store.get<VPlanDay>("c")).isEqualTo(o)
    }

    @Test
    fun test_restoreBroken() {
        store.save("c", "Hello broken Object")
        assertThat(store.get<VPlanDay>("c")).isEqualTo(null)
    }

    @Test
    fun test_loadNotPresent() {
        assertThat(store.get<String>("b")).isEqualTo(null)
    }

    @Test
    fun legacy() {
        val x = "{\"day1\":{\"dateAndDay\":\"b\",\"lastUpdated\":\"a\",\"motd\":\"bla\",\"vPlanRows\":[{\"content\":\"---\",\"grade\":\"314\",\"hour\":\"Entfall\",\"isMarkedNew\":false,\"isOmitted\":false,\"kind\":\"\\u003cs\\u003eHenß\\u003c/s\\u003e\",\"room\":\"5a\",\"subject\":\"1\"}]}, \"day2\":{\"dateAndDay\":\"\",\"lastUpdated\":\"\",\"motd\":\"\",\"vPlanRows\":[]}}"

        store.save("a", x)
        assertThat(store.get<VPlan>("a")).isNull()
    }
}



