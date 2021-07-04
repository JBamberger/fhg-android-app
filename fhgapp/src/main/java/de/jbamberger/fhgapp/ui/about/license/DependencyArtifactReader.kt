package de.jbamberger.fhgapp.ui.about.license

import android.content.res.AssetManager
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import okio.buffer
import okio.source
import java.io.IOException
import javax.inject.Inject

private val SIMPLIFIED_BSD = DependencyLicense(
    name = "BSD 2-Clause \"Simplified\" License",
    url = "https://opensource.org/licenses/BSD-2-Clause"
)

private val APACHE_2_0 = DependencyLicense(
    name = "Apache License 2.0",
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

enum class DependencyGroup(val key: String, val displayName: String) {
    AOSP("androidx", "Android Open Source Project"),
    GLIDE("com.github.bumptech.glide", "Bumptech: Glide"),
    DAGGER("com.google.dagger", "Dagger 2"),
    MOSHI("com.squareup.moshi", "Square: Moshi"),
    OKHTTP("com.squareup.okhttp3", "Square: OkHttp 3"),
    OKIO("com.squareup.okio", "Square: OkIo"),
    RETROFIT("com.squareup.retrofit2", "Square: Retrofit 2"),
    KOTLIN("org.jetbrains.kotlin", "Kotlin"),
    KOTLINX("org.jetbrains.kotlinx", "Kotlin Extensions"),
    OTHERS("", "Others");

    companion object {
        private val GROUPID_TO_NAME = values().map { it.key to it }.toMap()

        fun getGroup(artifact: LicenseeArtifactInfo): DependencyGroup {
            val key = if (artifact.groupId.startsWith("androidx")) "androidx" else artifact.groupId

            return GROUPID_TO_NAME[key] ?: OTHERS
        }
    }
}


class DependencyReadingException(message: String? = null, cause: Throwable? = null) :
    Exception(message, cause)

class DependencyReader
@Inject constructor(
    moshi: Moshi,
    private val assets: AssetManager
) {
    private val artifactsFile: String = "artifacts.json"
    private val jsonAdapter: JsonAdapter<List<LicenseeArtifactInfo>> = moshi.adapter(
        Types.newParameterizedType(List::class.java, LicenseeArtifactInfo::class.java)
    )

    private fun mapUnknownLicense(inLicense: LicenseeUnknownLicense): DependencyLicense {
        if (!inLicense.url.isNullOrBlank() && inLicense.url in URL_TO_LICENSE) {
            return URL_TO_LICENSE.getValue(inLicense.url)
        }
        if (!inLicense.name.isNullOrBlank() && inLicense.name in NAME_TO_LICENSE) {
            return NAME_TO_LICENSE.getValue(inLicense.name)
        }
        throw DependencyReadingException("Failed to map unknown license to known license.")
    }

    private fun readArtifacts(): List<LicenseeArtifactInfo> {
        try {
            assets.open(artifactsFile).source().buffer().use {
                return jsonAdapter.fromJson(it) ?: throw DependencyReadingException(
                    "Failed to parse artifact file: $artifactsFile."
                )
            }
        } catch (e: IOException) {
            throw DependencyReadingException("Could not read artifact file: $artifactsFile.", e)
        }
    }

    private fun convertArtifacts(artifacts: List<LicenseeArtifactInfo>): List<DependencyInformation> {
        return artifacts.map(::convertArtifact)
    }

    private fun convertArtifact(artifact: LicenseeArtifactInfo): DependencyInformation {
        val licenses = LinkedHashSet<DependencyLicense>()
        artifact.knownLicenses.mapTo(licenses) { DependencyLicense(it.name, it.url) }
        artifact.unknownLicenses.mapTo(licenses, this::mapUnknownLicense)

        val name = artifact.groupId + ":" + artifact.artifactId // + ":" + artifact.version
        return DependencyInformation(
            name = name,
            licenses = licenses,
            url = artifact.scm?.url
        )
    }


    fun getDependencies(): Map<DependencyGroup, List<DependencyInformation>> =
        readArtifacts()
            .groupBy(DependencyGroup::getGroup)
            .toSortedMap()
            .map { it.key to convertArtifacts(it.value) }
            .toMap()
}