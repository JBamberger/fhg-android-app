
# jsoup
-keeppackagenames org.jsoup.nodes


# gson

# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# for gsons @SerializedName
-keepattributes *Annotation*
