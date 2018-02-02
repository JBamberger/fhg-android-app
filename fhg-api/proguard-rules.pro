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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-verbose

########
# Gson #
########

# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# for gsons @SerializedName
-keepattributes *Annotation*


############
# Retrofit #
############

# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain service method parameters.
-keepclassmembernames interface * {
    @retrofit2.http.* <methods>;
}
# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement


##########
# OkHttp #
##########
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

#########
# JSoup #
#########
-keeppackagenames org.jsoup.nodes

##########
# Dagger #
##########
-dontwarn com.google.errorprone.annotations.*


#############
# SimpleXml #
#############
-keepattributes Signature, *Annotation*, ElementList, Root

-dontwarn org.simpleframework.xml.stream.**
-keep class org.simpleframework.xml.**{ *; }
-keepclassmembers class * {
    @org.simpleframework.xml.* <fields>;
    @org.simpleframework.xml.* <init>(...);
}
