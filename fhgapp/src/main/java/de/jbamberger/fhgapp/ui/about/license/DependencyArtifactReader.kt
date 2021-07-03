package de.jbamberger.fhgapp.ui.about.license

private val SIMPLIFIED_BSD = LicenseeKnownLicense(
    name = "BSD 2-Clause \"Simplified\" License",
    id = "BSD-2-Clause",
    url = "https://opensource.org/licenses/BSD-2-Clause"
)

private val APACHE_2_0 = LicenseeKnownLicense(
    name = "Apache License 2.0",
    id = "Apache-2.0",
    url = "https://www.apache.org/licenses/LICENSE-2.0"
)

private val URL_TO_LICENSE = mapOf(
    "http://opensource.org/licenses/bsd-license" to SIMPLIFIED_BSD,
    "http://www.apache.org/license/LICENSE-2.0.txt" to APACHE_2_0,
)

private val NAME_TO_LICENSE = mapOf(
    "Simplified BSD License" to SIMPLIFIED_BSD,
    "The Apache Software License, Version 2.0" to APACHE_2_0,
)

object DependencyArtifactReader {

    fun mapLicenses(inLicense: LicenseeUnknownLicense): LicenseeKnownLicense {
        if (!inLicense.url.isNullOrBlank() && URL_TO_LICENSE.contains(inLicense.url)) {
            return URL_TO_LICENSE.getValue(inLicense.url)
        }
        if (!inLicense.name.isNullOrBlank() && NAME_TO_LICENSE.contains(inLicense.name)) {
            return NAME_TO_LICENSE.getValue(inLicense.name)
        }
        throw IllegalArgumentException("Failed to map unknown license to known license.")
    }
}