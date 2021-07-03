package de.jbamberger.fhgapp.ui.about.license

data class DependencyInformation(
    val name: String,
    val licenses: Set<DependencyLicense>,
    val url: String?
) {
    val licenseString: String
        get() = licenses.joinToString(separator = "\n") { license ->
            license.url?.let { "${license.name}: $it" } ?: license.name
        }
}

data class DependencyLicense(
    val name: String,
    val url: String?
)