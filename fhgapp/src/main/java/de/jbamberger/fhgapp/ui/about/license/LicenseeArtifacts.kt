/*
 *    Copyright 2021 Jannik Bamberger
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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