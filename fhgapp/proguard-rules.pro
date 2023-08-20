# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}


###################################################################################################
# Debugging proguard:
###################################################################################################

#-dontobfuscate
#-dontshrink

# Uncomment this to preserve the line number information for debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to hide the original source file name.
#-renamesourcefileattribute SourceFile


-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn "org.conscrypt.Conscrypt$Version"
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE

-dontnote sun.misc.Unsafe
-dontnote com.google.android.gms.common.**

###################################################################################################
# data classes
###################################################################################################
-keepnames class com.squareup.moshi.Moshi
-keep class de.jbamberger.fhgapp.repository.data.* {
    <init>(...);
    <fields>;
}

###################################################################################################
# kotlin
###################################################################################################

-dontnote kotlin.internal.PlatformImplementationsKt
-dontnote kotlin.reflect.jvm.internal.**
-dontwarn kotlin.reflect.jvm.internal.**
-keep class kotlin.reflect.jvm.internal.** { *; }

###################################################################################################
#moshi
###################################################################################################

# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

-keepclasseswithmembers class * {
    @com.squareup.moshi.* <methods>;
}

-keep @com.squareup.moshi.JsonQualifier interface *

# The name of @JsonClass types is used to look up the generated adapter.
-keepnames @com.squareup.moshi.JsonClass class *

# Retain generated JsonAdapters if annotated type is retained.
#-if @com.squareup.moshi.JsonClass class *
#-keep class <1>JsonAdapter {
#    <init>(...);
#    <fields>;
#}

# moshi kotlin converter
#-keep class kotlin.reflect.jvm.internal.impl.builtins.BuiltInsLoaderImpl
#-keepclassmembers class kotlin.Metadata {
#    public <methods>;
#}

###################################################################################################
# Retrofit
###################################################################################################

# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retrofit does reflection on method and parameter annotations.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Keep annotation default values (e.g., retrofit2.http.Field.encoded).
-keepattributes AnnotationDefault

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
# and replaces all potential values with null. Explicitly keeping the interfaces prevents this.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

# Keep inherited services.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface * extends <1>

# With R8 full mode generic signatures are stripped for classes that are not
# kept. Suspend functions are wrapped in continuations where the type argument
# is used.
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# R8 full mode strips generic signatures from return types if not kept.
-if interface * { @retrofit2.http.* public *** *(...); }
-keep,allowoptimization,allowshrinking,allowobfuscation class <3>

# With R8 full mode generic signatures are stripped for classes that are not kept.
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# For use with LiveDataCallAdapter
-keep class androidx.lifecycle.LiveData { *; }
-keep class de.jbamberger.fhgapp.repository.api.ApiResponse { *; }

###################################################################################################
# OkHttp
###################################################################################################

# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn okhttp3.internal.platform.ConscryptPlatform
-dontnote okhttp3.internal.platform.*

###################################################################################################
# Okio
###################################################################################################

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

###################################################################################################
# JSoup
###################################################################################################

-keeppackagenames org.jsoup.nodes

###################################################################################################
# Dagger
###################################################################################################

-dontwarn com.google.errorprone.annotations.*

###################################################################################################
# build system classes
###################################################################################################

-dontnote "org.apache.http.params.HttpConnectionParams"
-dontnote "org.apache.http.params.CoreConnectionPNames"
-dontnote "org.apache.http.params.HttpParams"
-dontnote "org.apache.http.conn.scheme.LayeredSocketFactory"
-dontnote "org.apache.http.conn.scheme.SocketFactory"
-dontnote "org.apache.http.conn.scheme.HostNameResolver"
-dontnote "org.apache.http.conn.ConnectTimeoutException"
-dontnote "android.net.http.SslCertificate"
-dontnote "android.net.http.SslCertificate$DName"
-dontnote "android.net.http.SslError"
-dontnote "android.net.http.HttpResponseCache"

###################################################################################################
# Glide
###################################################################################################

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

-dontwarn com.bumptech.glide.load.resource.bitmap.VideoDecoder
