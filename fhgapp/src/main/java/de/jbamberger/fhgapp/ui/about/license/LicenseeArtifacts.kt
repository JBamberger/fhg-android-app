package de.jbamberger.fhgapp.ui.about.license

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class LicenseeArtifactInfo(
    @Json(name = "groupId")
    val groupId: String,
    @Json(name = "artifactId")
    val artifactId: String,
    @Json(name = "version")
    val version: String,
    @Json(name = "spdxLicenses")
    val knownLicenses: Set<LicenseeKnownLicense> = emptySet(),
    @Json(name = "unknownLicenses")
    val unknownLicenses: Set<LicenseeUnknownLicense> = emptySet(),
    @Json(name = "scm")
    val scm: LicenseeArtifactScm? = null
)

@JsonClass(generateAdapter = true)
data class LicenseeKnownLicense(
    @Json(name = "identifier")
    val id: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "url")
    val url: String
)

@JsonClass(generateAdapter = true)
data class LicenseeUnknownLicense(
    @Json(name = "name")
    val name: String?,
    @Json(name = "url")
    val url: String?
)

@JsonClass(generateAdapter = true)
data class LicenseeArtifactScm(
    @Json(name = "url")
    val url: String
)