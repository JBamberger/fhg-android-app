package de.jbamberger.fhgapp.ui.about

import android.content.Context
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
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
//        FIXME: implement
//        val items = activity.adapter.items
//
//        assertThat(R.layout.about_disclaimer, equalTo(items[0].layoutId))
//        assertThat(R.layout.about_contact, equalTo(items[1].layoutId))
//        assertThat(R.layout.about_version, equalTo(items[2].layoutId))
//        assertThat(R.layout.about_oss_licenses, equalTo(items[3].layoutId))

    }

    @After
    fun tearDown() {
        controller
                .pause()
                .stop()
                .destroy();
    }

}