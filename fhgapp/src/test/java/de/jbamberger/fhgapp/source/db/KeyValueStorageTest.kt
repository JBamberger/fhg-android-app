package de.jbamberger.fhgapp.source.db

import android.preference.PreferenceManager
import com.squareup.moshi.Moshi
import de.jbamberger.api.data.VPlanDay
import de.jbamberger.api.data.VPlanHeader
import de.jbamberger.api.data.VPlanRow
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
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

    lateinit var store: KeyValueStorage

    @Before
    fun setUp() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(application)
        val moshi = Moshi.Builder().build()
        store = KeyValueStorage(moshi, prefs)
    }

    @Test
    fun test_saveAndLoadString() {
        store.save("s", "Hello World.")
        assertThat("Hello World.", `is`(equalTo(store.get("s"))))
    }

    @Test
    fun test_saveAndLoadComplexObject() {
        val o = VPlanDay(VPlanHeader("a", "a", "c"),
                listOf(VPlanRow("a", false, "b", "c", "d", "e", "f", false)))
        store.save("c", o)
        assertThat(o, `is`(equalTo(store.get("c"))))
    }

    @Test
    fun test_restoreBroken() {
        store.save("c", "Hello broken Object")
        assertThat(store.get("c"), `is`(equalTo(null as VPlanDay?)))
    }

    @Test
    fun test_loadNotPresent() {
        assertThat(null, `is`(equalTo(store.get<String>("b"))))
    }
}