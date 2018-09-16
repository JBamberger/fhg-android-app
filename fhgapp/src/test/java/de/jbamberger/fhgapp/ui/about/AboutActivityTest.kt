package de.jbamberger.fhgapp.ui.about

import android.content.Context
import de.jbamberger.fhgapp.R
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.RuntimeEnvironment.application
import org.robolectric.Shadows.shadowOf
import org.robolectric.android.controller.ActivityController
import org.robolectric.shadows.ShadowPackageManager


/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
@RunWith(RobolectricTestRunner::class)
class AboutActivityTest {

    lateinit var context: Context
    lateinit var controller: ActivityController<AboutActivity>
    lateinit var shadowPackageManager: ShadowPackageManager

    @Before
    fun setUp() {
        context = RuntimeEnvironment.application
        controller = Robolectric.buildActivity(AboutActivity::class.java)
        shadowPackageManager = shadowOf(context.packageManager)
    }


    @Test
    fun test_addData() {
        val activity = controller.create().start().resume().get()
        val items = activity.addData()

        assertThat(R.layout.about_disclaimer, `is`(equalTo(items[0].layoutId)))
        assertThat(R.layout.about_contact, `is`(equalTo(items[1].layoutId)))
        assertThat(R.layout.about_version, `is`(equalTo(items[2].layoutId)))
        assertThat(R.layout.about_library_title, `is`(equalTo(items[3].layoutId)))

        val res = application.resources
        val names = res.getStringArray(R.array.about_library_names)
        val licenses = res.getStringArray(R.array.about_library_descriptions)
        val urls = res.getStringArray(R.array.about_library_urls)

        assertThat(names.size, `is`(equalTo(items.size - 4)))

        for (i in 4 until items.size) {
            assertThat(R.layout.about_library, `is`(equalTo(items[i].layoutId)))
            val lib = items[i].obj as AboutActivity.Library
            assertThat(names[i - 4], `is`(equalTo(lib.name)))
            assertThat(licenses[i - 4], `is`(equalTo(lib.description)))
            assertThat(urls[i - 4], `is`(equalTo(lib.url)))
        }
    }

    @After
    fun tearDown() {
        controller
                .pause()
                .stop()
                .destroy();
    }

}