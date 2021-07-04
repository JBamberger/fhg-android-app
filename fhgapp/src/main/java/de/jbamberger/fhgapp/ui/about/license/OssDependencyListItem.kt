package de.jbamberger.fhgapp.ui.about.license

import de.jbamberger.fhgapp.R

sealed interface OssDependencyListItem {

    val layoutId: Int

    data class Header(
        val name: String,
        val licenseString: String? = null
    ) : OssDependencyListItem {
        override val layoutId get() = R.layout.oss_list_header
    }

    data class Library(
        val name: String,
        val licenseString: String? = null
    ) : OssDependencyListItem {
        override val layoutId get() = R.layout.oss_list_item
    }
}
