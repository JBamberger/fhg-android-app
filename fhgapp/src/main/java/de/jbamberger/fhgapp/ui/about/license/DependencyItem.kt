package de.jbamberger.fhgapp.ui.about.license

data class DependencyInformation(
    val name: String,
    val licenses: Set<DependencyLicense>,
    val url: String?
)

data class DependencyLicense(
    val name: String,
    val url: String?
)